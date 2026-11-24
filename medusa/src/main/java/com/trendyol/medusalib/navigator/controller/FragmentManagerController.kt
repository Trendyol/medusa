package com.trendyol.medusalib.navigator.controller

import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.trendyol.medusalib.R
import com.trendyol.medusalib.common.extensions.detach
import com.trendyol.medusalib.common.extensions.hide
import com.trendyol.medusalib.common.extensions.remove
import com.trendyol.medusalib.common.extensions.show
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.data.FragmentData
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import com.trendyol.medusalib.navigator.transaction.TransactionType
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType
import java.lang.ref.WeakReference

private data class RetainedViewLayout(
    val view: WeakReference<View>,
    val height: Int,
)

internal class FragmentManagerController(
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
    private val navigatorTransaction: NavigatorTransaction,
    private val stagedFragmentHolder: StagedFragmentHolder,
) {

    private var currentTransaction: FragmentTransaction? = null

    private var currentTransitionAnimationType: TransitionAnimationType? = null

    private val retainedViewLayouts = mutableMapOf<String, RetainedViewLayout>()
    private val retainedSources = mutableMapOf<String, String>()

    fun enableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)
        checkAndCreateTransaction()
        restoreRetainedFragmentState(fragmentTag)

        when (navigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> commitShow(fragmentTag)
            TransactionType.ATTACH_DETACH -> commitAttach(fragmentTag)
        }
    }

    fun disableFragment(fragmentTag: String) {
        releaseRetainedSource(fragmentTag)
        restoreRetainedViewLayout(fragmentTag)
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        when (navigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> commitHide(fragmentTag)
            TransactionType.ATTACH_DETACH -> commitDetach(fragmentTag)
        }
    }

    fun removeFragment(fragmentTag: String) {
        retainedSources.remove(fragmentTag)
        retainedSources.entries.removeAll { it.value == fragmentTag }
        restoreRetainedViewLayout(fragmentTag)
        checkAndCreateTransaction()

        when (currentTransitionAnimationType) {
            TransitionAnimationType.LEFT_TO_RIGHT -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_left)
            TransitionAnimationType.RIGHT_TO_LEFT -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_right)
            TransitionAnimationType.BOTTOM_TO_TOP -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_bottom)
            TransitionAnimationType.TOP_TO_BOTTOM -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_top)
            TransitionAnimationType.FADE_IN_OUT -> setCustomAnimations(R.anim.empty_animation, R.anim.fade_out)
            null -> { /* no-op */ }
        }

        currentTransaction?.remove(getFragment(fragmentTag))
        commitAllowingStateLoss()
    }

    fun removeFragments(fragmentTagList: List<String>) {
        checkAndCreateTransaction()

        for (fragmentTag in fragmentTagList) {
            releaseRetainedSource(fragmentTag)
            retainedSources.entries.removeAll { it.value == fragmentTag }
            restoreRetainedViewLayout(fragmentTag)
            getFragment(fragmentTag)?.let { currentTransaction?.remove(it) }
        }
        commitAllowingStateLoss()
    }

    fun addFragment(fragmentData: FragmentData) {
        checkAndCreateTransaction()
        stageFragment(fragmentData)
        currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)
        commitAllowingStateLoss()
    }

    fun preloadFragment(fragmentData: FragmentData) {
        checkAndCreateTransaction()
        stageFragment(fragmentData)
        currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)?.hide(fragmentData.fragment)
        commitAllowingStateLoss()
    }

    fun showPreloadedFragment(
        currentFragmentTag: String,
        fragmentTag: String,
        fallbackFragment: Fragment?,
    ): PreloadedFragmentResult {
        val fragment = getFragmentWithExecutingPendingTransactionsIfNeeded(fragmentTag) ?: fallbackFragment
        if (fragment == null) return PreloadedFragmentResult.NotFound

        disableFragment(currentFragmentTag)

        return if (fragment != fallbackFragment) {
            enableFragment(fragmentTag)
            PreloadedFragmentResult.Success
        } else {
            addFragment(FragmentData(fallbackFragment, fragmentTag))
            PreloadedFragmentResult.FallbackSuccess
        }
    }

    fun disableAndStartFragment(disableFragmentTag: String, vararg fragmentDataArgs: FragmentData) {
        val disabledFragment = getFragmentWithExecutingPendingTransactionsIfNeeded(disableFragmentTag)

        checkAndCreateTransaction()

        for (fragmentData in fragmentDataArgs) {
            currentTransitionAnimationType = fragmentData.transitionAnimation
            when (currentTransitionAnimationType) {
                TransitionAnimationType.LEFT_TO_RIGHT -> setCustomAnimations(R.anim.enter_from_left, R.anim.empty_animation)
                TransitionAnimationType.RIGHT_TO_LEFT -> setCustomAnimations(R.anim.enter_from_right, R.anim.empty_animation)
                TransitionAnimationType.BOTTOM_TO_TOP -> setCustomAnimations(R.anim.enter_from_bottom, R.anim.empty_animation)
                TransitionAnimationType.TOP_TO_BOTTOM -> setCustomAnimations(R.anim.enter_from_top, R.anim.empty_animation)
                TransitionAnimationType.FADE_IN_OUT -> setCustomAnimations(R.anim.fade_in, R.anim.empty_animation)
                null -> { /* no-op */ }
            }
            stageFragment(fragmentData)
            currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)
        }

        val transitionOwner = fragmentDataArgs.firstOrNull { fragmentData ->
            val fragment = fragmentData.fragment
            fragment is Navigator.KeepPreviousFragmentVisible && fragment.keepPreviousFragmentVisible()
        }
        if (transitionOwner != null) {
            releaseRetainedSource(disableFragmentTag)
            disabledFragment?.let { fragment ->
                retainedSources[transitionOwner.fragmentTag] = disableFragmentTag
                retainViewLayout(disableFragmentTag, fragment)
                (fragment as? Navigator.OnRetainedAsBackgroundListener)?.onRetainedAsBackground(true)
                currentTransaction?.setMaxLifecycle(fragment, Lifecycle.State.STARTED)
            }
        } else {
            restoreRetainedViewLayout(disableFragmentTag)
            val disabledFragmentNavigatorTransaction = getFragmentNavigatorTransaction(disableFragmentTag)
            when (disabledFragmentNavigatorTransaction.transactionType) {
                TransactionType.SHOW_HIDE -> currentTransaction?.hide(disabledFragment)
                TransactionType.ATTACH_DETACH -> currentTransaction?.detach(disabledFragment)
            }
            retainedSources[disableFragmentTag]?.let { sourceTag ->
                setCustomAnimations(0, 0)
                currentTransaction?.hide(getFragment(sourceTag))
            }
        }

        commitAllowingStateLoss()
    }

    private fun restoreRetainedFragmentState(fragmentTag: String) {
        retainedSources[fragmentTag]?.let { sourceTag ->
            currentTransaction?.show(getFragment(sourceTag))
        }
        getFragment(fragmentTag)?.let { fragment ->
            (fragment as? Navigator.OnRetainedAsBackgroundListener)?.onRetainedAsBackground(false)
            currentTransaction?.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
        }
        if (retainedViewLayouts.containsKey(fragmentTag)) {
            currentTransaction?.runOnCommit { restoreRetainedViewLayout(fragmentTag) }
        }
    }

    private fun retainViewLayout(fragmentTag: String, fragment: Fragment) {
        if (retainedViewLayouts.containsKey(fragmentTag)) return
        val view = fragment.view?.takeIf { it.height > 0 } ?: return
        if (view.layoutParams.height == view.height) return
        retainedViewLayouts[fragmentTag] = RetainedViewLayout(WeakReference(view), view.layoutParams.height)
        view.layoutParams = view.layoutParams.apply { height = view.height }
    }

    private fun releaseRetainedSource(ownerTag: String) {
        val sourceTag = retainedSources.remove(ownerTag) ?: return
        releaseRetainedSource(sourceTag)
        restoreRetainedViewLayout(sourceTag)
        checkAndCreateTransaction()
        when (getFragmentNavigatorTransaction(sourceTag).transactionType) {
            TransactionType.SHOW_HIDE -> currentTransaction?.hide(getFragment(sourceTag))
            TransactionType.ATTACH_DETACH -> currentTransaction?.detach(getFragment(sourceTag))
        }
    }

    private fun restoreRetainedViewLayout(fragmentTag: String) {
        val retained = retainedViewLayouts.remove(fragmentTag) ?: return
        retained.view.get()?.let { view ->
            if (view.layoutParams.height != retained.height) {
                view.layoutParams = view.layoutParams.apply { height = retained.height }
            }
        }
    }

    private fun setCustomAnimations(@AnimatorRes @AnimRes enter: Int, @AnimatorRes @AnimRes exit: Int) {
        currentTransaction?.setCustomAnimations(enter, exit)
    }

    private fun getFragmentWithExecutingPendingTransactionsIfNeeded(fragmentTag: String): Fragment? {
        var fragment = getCurrentOrStagedFragment(fragmentTag)
        if (fragment == null && fragmentManager.executePendingTransactions()) {
            fragment = getFragment(fragmentTag)
        }
        return fragment
    }

    fun getFragment(fragmentTag: String): Fragment? {
        return fragmentManager.findFragmentByTag(fragmentTag)
    }

    fun getCurrentOrStagedFragment(fragmentTag: String): Fragment? {
        return fragmentManager.findFragmentByTag(fragmentTag) ?: stagedFragmentHolder.getStagedFragment(fragmentTag)
    }

    private fun getFragmentNavigatorTransaction(fragmentTag: String): NavigatorTransaction {
        var navigatorTransaction = navigatorTransaction

        getFragment(fragmentTag)?.let {
            if (it is Navigator.OnNavigatorTransactionListener) {
                navigatorTransaction = it.getNavigatorTransaction()
            }
        }

        return navigatorTransaction
    }

    fun findFragmentByTagAndRemove(fragmentTag: String) {
        releaseRetainedSource(fragmentTag)
        retainedSources.entries.removeAll { it.value == fragmentTag }
        restoreRetainedViewLayout(fragmentTag)
        checkAndCreateTransaction()

        getFragmentWithExecutingPendingTransactionsIfNeeded(fragmentTag)?.let { currentTransaction?.remove(it) }
    }

    private fun commitShow(fragmentTag: String) {
        checkAndCreateTransaction()

        currentTransaction?.show(getFragment(fragmentTag))
        commitAllowingStateLoss()
    }

    private fun commitAttach(fragmentTag: String) {
        checkAndCreateTransaction()

        getFragment(fragmentTag)?.let { fragment ->
            currentTransaction?.attach(fragment)
            if (fragment.isHidden) currentTransaction?.show(fragment)
        }
        commitAllowingStateLoss()
    }

    private fun commitHide(fragmentTag: String) {
        checkAndCreateTransaction()

        currentTransaction?.hide(getFragmentWithExecutingPendingTransactionsIfNeeded(fragmentTag))
        commitAllowingStateLoss()
    }

    private fun commitDetach(fragmentTag: String) {
        checkAndCreateTransaction()

        currentTransaction?.detach(getFragmentWithExecutingPendingTransactionsIfNeeded(fragmentTag))
        commitAllowingStateLoss()
    }

    fun commitAllowingStateLoss() {
        currentTransaction?.commitAllowingStateLoss()
        currentTransaction = null
    }

    private fun checkAndCreateTransaction() {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }
    }

    private fun stageFragment(fragmentData: FragmentData) {
        stagedFragmentHolder.stageFragmentForCommit(
            fragmentData.fragmentTag,
            fragmentData.fragment,
        )
    }
}
