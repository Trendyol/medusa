package com.trendyol.medusalib.navigator.controller

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.trendyol.medusalib.R
import com.trendyol.medusalib.common.extensions.attach
import com.trendyol.medusalib.common.extensions.detach
import com.trendyol.medusalib.common.extensions.hide
import com.trendyol.medusalib.common.extensions.remove
import com.trendyol.medusalib.common.extensions.show
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.data.FragmentData
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import com.trendyol.medusalib.navigator.transaction.TransactionType
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType

class FragmentManagerController(private val fragmentManager: FragmentManager,
                                private val containerId: Int,
                                private val navigatorTransaction: NavigatorTransaction) {

    private var currentTransaction: FragmentTransaction? = null

    private var currentTransitionAnimationType: TransitionAnimationType? = null

    fun enableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        when (navigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> commitShow(fragmentTag)
            TransactionType.ATTACH_DETACH -> commitAttach(fragmentTag)
        }
    }

    fun disableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        when (navigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> commitHide(fragmentTag)
            TransactionType.ATTACH_DETACH -> commitDetach(fragmentTag)
        }
    }

    fun removeFragment(fragmentTag: String) {
        checkAndCreateTransaction()

        when (currentTransitionAnimationType) {
            TransitionAnimationType.LEFT_TO_RIGHT -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_left)
            TransitionAnimationType.RIGHT_TO_LEFT -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_right)
            TransitionAnimationType.BOTTOM_TO_TOP -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_bottom)
            TransitionAnimationType.TOP_TO_BOTTOM -> setCustomAnimations(R.anim.empty_animation, R.anim.exit_to_top)
            TransitionAnimationType.FADE_IN_OUT -> setCustomAnimations(R.anim.empty_animation, R.anim.fade_out)
        }

        currentTransaction?.remove(getFragment(fragmentTag))
        commitAllowingStateLoss()
    }

    fun removeFragments(fragmentTagList: List<String>) {
        checkAndCreateTransaction()

        for (fragmentTag in fragmentTagList) {
            getFragment(fragmentTag)?.let { currentTransaction?.remove(it) }
        }
        commitAllowingStateLoss()
    }

    fun addFragment(fragmentData: FragmentData) {
        checkAndCreateTransaction()

        currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)
        commitAllowingStateLoss()
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
            }
            currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)
        }

        val disabledFragmentNavigatorTransaction = getFragmentNavigatorTransaction(disableFragmentTag)
        when (disabledFragmentNavigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> currentTransaction?.hide(disabledFragment)
            TransactionType.ATTACH_DETACH -> currentTransaction?.detach(disabledFragment)
        }

        commitAllowingStateLoss()
    }

    private fun setCustomAnimations(@AnimatorRes @AnimRes enter: Int, @AnimatorRes @AnimRes exit: Int) {
        currentTransaction?.setCustomAnimations(enter, exit)
    }

    fun isFragmentNull(fragmentTag: String): Boolean {
        return getFragment(fragmentTag) == null
    }

    private fun getFragmentWithExecutingPendingTransactionsIfNeeded(fragmentTag: String): Fragment? {
        var fragment = getFragment(fragmentTag)
        if (fragment == null && fragmentManager.executePendingTransactions()) {
            fragment = getFragment(fragmentTag)
        }
        return fragment
    }

    fun getFragment(fragmentTag: String): Fragment? {
        return fragmentManager.findFragmentByTag(fragmentTag)
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

        currentTransaction?.attach(getFragment(fragmentTag))
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
}