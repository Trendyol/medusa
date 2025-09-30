package com.trendyol.medusalib.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.trendyol.medusalib.navigator.controller.FragmentManagerController
import com.trendyol.medusalib.navigator.controller.PreloadedFragmentResult
import com.trendyol.medusalib.navigator.controller.StagedFragmentHolder
import com.trendyol.medusalib.navigator.data.FragmentData
import com.trendyol.medusalib.navigator.data.MedusaRequestedFragmentTransaction
import com.trendyol.medusalib.navigator.data.StackItem
import com.trendyol.medusalib.navigator.tag.TagCreator
import com.trendyol.medusalib.navigator.tag.UniqueTagCreator
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType
import java.lang.ref.WeakReference

open class MultipleStackNavigator(
    fragmentManager: FragmentManager,
    containerId: Int,
    private var rootFragmentProvider: List<() -> Fragment>,
    private var navigatorListener: Navigator.NavigatorListener? = null,
    private val navigatorConfiguration: NavigatorConfiguration = NavigatorConfiguration(),
    private val transitionAnimationType: TransitionAnimationType? = null
) : Navigator {

    private var destinationChangeLiveData = MutableLiveData<Fragment?>()
    private var requestedFragmentTransactionLiveData = MutableLiveData<MedusaRequestedFragmentTransaction?>()
    private val tagCreator: TagCreator = UniqueTagCreator()

    private val fragmentManagerController = FragmentManagerController(
        fragmentManager,
        containerId,
        navigatorConfiguration.defaultNavigatorTransaction,
        StagedFragmentHolder(mutableMapOf())
    )

    private val fragmentStackStateMapper = FragmentStackStateMapper()
    private var fragmentStackState = FragmentStackState()

    override fun start(fragment: Fragment) {
        start(fragment, DEFAULT_GROUP_NAME)
    }

    override fun start(fragment: Fragment, tabIndex: Int) {
        start(fragment, tabIndex, DEFAULT_GROUP_NAME)
    }

    override fun start(fragment: Fragment, tabIndex: Int, fragmentGroupName: String) {
        switchTab(tabIndex)
        start(fragment, fragmentGroupName)
        navigatorListener?.onTabChanged(tabIndex)
    }

    override fun start(fragment: Fragment, fragmentGroupName: String) {
        start(fragment, fragmentGroupName, transitionAnimationType)
    }

    override fun start(fragment: Fragment, transitionAnimation: TransitionAnimationType) {
        start(fragment, DEFAULT_GROUP_NAME, transitionAnimation)
    }

    override fun preloadFragment(fragment: Fragment, fragmentTag: String) {
        fragmentManagerController.preloadFragment(FragmentData(fragment, fragmentTag))
    }

    override fun startPreloadedFragment(fallbackFragment: Fragment?, fragmentTag: String): PreloadedFragmentResult {
        val currentFragmentTag = getCurrentFragmentTag()
        val result = fragmentManagerController.showPreloadedFragment(currentFragmentTag, fragmentTag, fallbackFragment)
        if (result !is PreloadedFragmentResult.NotFound) {
            fragmentStackState.notifyStackItemAddToCurrentTab(StackItem(fragmentTag = fragmentTag))
        }
        return result
    }

    override fun start(fragment: Fragment, fragmentGroupName: String, transitionAnimation: TransitionAnimationType?) {
        publishFragmentTransaction(nextFragment = fragment)
        val createdTag = tagCreator.create(fragment)
        val currentTabIndex = fragmentStackState.getSelectedTabIndex()
        val fragmentData = FragmentData(fragment, createdTag, transitionAnimation)

        if (fragmentStackState.isSelectedTabEmpty()) {
            val rootFragment = getRootFragment(currentTabIndex)
            val rootFragmentTag = tagCreator.create(rootFragment)
            val rootFragmentData = FragmentData(rootFragment, rootFragmentTag, transitionAnimation)
            fragmentManagerController.disableAndStartFragment(
                getCurrentFragmentTag(),
                rootFragmentData,
                fragmentData
            )
        } else {
            fragmentManagerController.disableAndStartFragment(getCurrentFragmentTag(), fragmentData)
        }
        fragmentStackState.notifyStackItemAddToCurrentTab(
            StackItem(
                fragmentTag = createdTag,
                groupName = fragmentGroupName,
            ),
        )
        fragment.observeFragmentLifecycle(
            ::onFragmentViewCreated,
            ::onFragmentDestroy
        )
    }

    override fun goBack() {
        if (canGoBack().not()) {
            throw IllegalStateException("Can not call goBack() method because stack is empty.")
        }

        if (canFragmentGoBack().not()) return

        if (shouldExit() && shouldGoBackToInitialIndex()) {
            fragmentStackState.insertTabToBottom(navigatorConfiguration.initialTabIndex)
        }


        if (fragmentStackState.hasSelectedTabOnlyRoot()) {
            fragmentManagerController.disableFragment(getCurrentFragmentTag())
            fragmentStackState.popSelectedTab()
            navigatorListener?.onTabChanged(fragmentStackState.getSelectedTabIndex())
        } else {
            val currentFragmentTag = fragmentStackState.popItemFromSelectedTab().fragmentTag
            fragmentManagerController.removeFragment(currentFragmentTag)
        }

        showUpperFragment()
    }

    override fun canGoBack(): Boolean {
        if (shouldExit() && shouldGoBackToInitialIndex().not()) {
            return false
        }
        return true
    }

    override fun switchTab(tabIndex: Int) {
        if (fragmentStackState.isSelectedTab(tabIndex)) return

        fragmentManagerController.disableFragment(getCurrentFragmentTag())

        fragmentStackState.switchTab(tabIndex)

        showUpperFragment()

        navigatorListener?.onTabChanged(tabIndex)
    }

    override fun reset(tabIndex: Int, resetRootFragment: Boolean) {
        if (fragmentStackState.isSelectedTab(tabIndex)) {
            resetCurrentTab(resetRootFragment)
            return
        }

        clearAllFragments(tabIndex, resetRootFragment)
    }

    override fun resetCurrentTab(resetRootFragment: Boolean) {
        val currentTabIndex = fragmentStackState.getSelectedTabIndex()
        clearAllFragments(currentTabIndex, resetRootFragment)

        if (resetRootFragment) {
            val rootFragment = getRootFragment(currentTabIndex)
            val createdTag = tagCreator.create(rootFragment)
            val rootFragmentData = FragmentData(rootFragment, createdTag)
            fragmentStackState.switchTab(currentTabIndex)
            fragmentStackState.notifyStackItemAdd(
                currentTabIndex,
                StackItem(fragmentTag = createdTag),
            )
            publishFragmentTransaction(nextFragment = rootFragment)
            fragmentManagerController.addFragment(rootFragmentData)

            rootFragment.observeFragmentLifecycle(
                onViewCreated = ::onFragmentViewCreated,
                onFragmentDestroy = ::onFragmentDestroy
            )
        } else {
            val upperFragmentTag: String = getCurrentFragmentTag()
            val upperFragment: Fragment? = fragmentManagerController.getFragment(upperFragmentTag)

            val newDestination: Fragment = upperFragment ?: getRootFragment(currentTabIndex)
            val newDestinationTag: String = newDestination.tag ?: tagCreator.create(newDestination)

            newDestination.observeFragmentLifecycle(
                onViewCreated = ::onFragmentViewCreated,
                onFragmentDestroy = ::onFragmentDestroy
            )
            publishFragmentTransaction(nextFragment = newDestination)
            fragmentManagerController.enableFragment(newDestinationTag)
        }
    }

    override fun reset() {
        clearAllFragments()
        fragmentStackState.clear()
        initializeStackState()

    }

    override fun resetWithFragmentProvider(rootFragmentProvider: List<() -> Fragment>) {
        this.rootFragmentProvider = rootFragmentProvider
        reset()
    }

    override fun clearGroup(fragmentGroupName: String) {
        if (fragmentGroupName == DEFAULT_GROUP_NAME) {
            throw IllegalArgumentException("Fragment group name can not be empty.")
        }
        val upperFragmentTag = fragmentStackState.peekItemFromSelectedTab()?.fragmentTag

        val poppedFragmentTags = fragmentStackState
            .popItems(fragmentGroupName)
            .map { it.fragmentTag }

        if (poppedFragmentTags.isNotEmpty()) {
            fragmentManagerController.removeFragments(poppedFragmentTags)
            val poppedUpperFragment = poppedFragmentTags.contains(upperFragmentTag)
            if (poppedUpperFragment) {
                showUpperFragment()
            }
        }
    }

    override fun hasOnlyRoot(tabIndex: Int): Boolean {
        return fragmentStackState.hasOnlyRoot(tabIndex)
    }

    override fun getCurrentFragment(): Fragment? {
        val visibleFragmentTag = getCurrentFragmentTag()
        return fragmentManagerController.getFragment(visibleFragmentTag)
    }

    override fun getPendingOrCurrentFragment(): Fragment? {
        val visibleFragmentTag = getCurrentFragmentTag()
        return fragmentManagerController.getCurrentOrStagedFragment(visibleFragmentTag)
    }

    override fun initialize(savedState: Bundle?) {
        if (savedState == null) {
            initializeStackState()
        }
        else {
            loadStackStateFromSavedState(savedState)
        }
    }

    override fun observeDestinationChanges(
        lifecycleOwner: LifecycleOwner,
        destinationChangedListener: (Fragment) -> Unit
    ) {
        destinationChangeLiveData.observe(lifecycleOwner) { fragment ->
            if (fragment != null) {
                destinationChangedListener(fragment)
            }
        }
    }

    override fun observeFragmentTransaction(
        lifecycleOwner: LifecycleOwner,
        transactionListener: (previousFragment: Fragment?, nextFragment: Fragment?) -> Unit
    ) {
        requestedFragmentTransactionLiveData.observe(lifecycleOwner) { fragmentTransactionInfo ->
            if (fragmentTransactionInfo != null) {
                transactionListener(
                    fragmentTransactionInfo.currentFragment.get(),
                    fragmentTransactionInfo.nextFragment.get(),
                )
            }
            requestedFragmentTransactionLiveData.value = null
        }
    }

    override fun getFragmentIndexInStackBySameType(tag: String?): Int {
        if (tag.isNullOrEmpty()) return -1
        fragmentStackState.fragmentTagStack.forEach { stack ->
            stack.forEachIndexed { index, stackItem ->
                if (stackItem.fragmentTag == tag) {
                    return stack.size - index - 1
                }
            }
        }
        return -1
    }

    private fun initializeStackState() {
        val initialTabIndex = navigatorConfiguration.initialTabIndex
        val rootFragment = rootFragmentProvider.get(initialTabIndex).invoke()
        val createdTag = tagCreator.create(rootFragment)
        val stackItem = StackItem(fragmentTag = createdTag)

        fragmentStackState.setStackCount(rootFragmentProvider.size)
        fragmentStackState.notifyStackItemAdd(
            tabIndex = initialTabIndex,
            stackItem = stackItem,
        )
        fragmentStackState.switchTab(initialTabIndex)

        val rootFragmentTag = fragmentStackState.peekItem(initialTabIndex).fragmentTag
        val rootFragmentData = FragmentData(rootFragment, rootFragmentTag)
        fragmentManagerController.addFragment(rootFragmentData)
        navigatorListener?.onTabChanged(navigatorConfiguration.initialTabIndex)
        rootFragment.observeFragmentLifecycle(
            ::onFragmentViewCreated,
            ::onFragmentDestroy
        )
        publishFragmentTransaction(nextFragment = rootFragment)
    }

    private fun loadStackStateFromSavedState(savedState: Bundle) {
        val stackState = fragmentStackStateMapper.fromBundle(savedState.getBundle(MEDUSA_STACK_STATE_KEY))
        fragmentStackState.setStackState(stackState)
        if (stackState.tabIndexStack.isNotEmpty()) {
            navigatorListener?.onTabChanged(fragmentStackState.getSelectedTabIndex())
        }
    }

    private fun getRootFragment(tabIndex: Int): Fragment {
        return tabIndex
            .takeUnless { fragmentStackState.isTabEmpty(it) }
            ?.let { fragmentManagerController.getFragment(fragmentStackState.peekItem(it).fragmentTag) }
            ?: rootFragmentProvider[tabIndex].invoke()
    }

    private fun showUpperFragment() {
        val upperFragmentTag = fragmentStackState.peekItemFromSelectedTab()?.fragmentTag
        val upperFragment = upperFragmentTag?.let { fragmentManagerController.getFragment(it) }
        if (upperFragmentTag == null || upperFragment == null) {
            val rootFragment = getRootFragment(fragmentStackState.getSelectedTabIndex())
            val createdTag = tagCreator.create(rootFragment)
            val rootFragmentData = FragmentData(rootFragment, createdTag)
            fragmentStackState.notifyStackItemAdd(
                fragmentStackState.getSelectedTabIndex(),
                StackItem(createdTag),
            )
            publishFragmentTransaction(nextFragment = rootFragment)
            fragmentManagerController.addFragment(rootFragmentData)
            rootFragment.observeFragmentLifecycle(
                onViewCreated = ::onFragmentViewCreated,
                onFragmentDestroy = ::onFragmentDestroy
            )
        } else {
            publishFragmentTransaction(nextFragment = upperFragment)
            fragmentManagerController.enableFragment(upperFragmentTag)
            upperFragment.observeFragmentLifecycle(
                onViewCreated = ::onFragmentViewCreated,
                onFragmentDestroy = ::onFragmentDestroy
            )
        }
    }

    private fun onFragmentViewCreated(fragment: Fragment) {
        destinationChangeLiveData.value = fragment
    }

    private fun onFragmentDestroy(fragment: Fragment) {
        if (destinationChangeLiveData.value == fragment) {
            destinationChangeLiveData.value = null
        }
    }

    private fun getCurrentFragmentTag() = requireNotNull(fragmentStackState.peekItemFromSelectedTab()).fragmentTag

    private fun shouldExit(): Boolean {
        return fragmentStackState.hasTabStack() && fragmentStackState.hasSelectedTabOnlyRoot()
    }

    private fun shouldGoBackToInitialIndex(): Boolean {
        return fragmentStackState.getSelectedTabIndex() != navigatorConfiguration.initialTabIndex && navigatorConfiguration.alwaysExitFromInitial
    }

    private fun clearAllFragments() {
        fragmentStackState.popItemsFromNonEmptyTabs().forEach {
            fragmentManagerController.findFragmentByTagAndRemove(it.fragmentTag)
        }
        fragmentManagerController.commitAllowingStateLoss()
    }

    private fun clearAllFragments(tabIndex: Int, resetRootFragment: Boolean) {
        if (fragmentStackState.isTabEmpty(tabIndex)) {
            return
        }

        while (fragmentStackState.isTabEmpty(tabIndex).not()) {
            if (fragmentStackState.hasOnlyRoot(tabIndex) && resetRootFragment.not()) {
                break
            }
            val fragmentTagToBeRemoved = fragmentStackState.popItem(tabIndex).fragmentTag
            fragmentManagerController.findFragmentByTagAndRemove(fragmentTagToBeRemoved)
        }

        fragmentManagerController.commitAllowingStateLoss()
    }

    private fun canFragmentGoBack(): Boolean {
        if (getCurrentFragment() is Navigator.OnGoBackListener) {
            return (getCurrentFragment() as Navigator.OnGoBackListener).onGoBack()
        }
        return true
    }

    private fun Fragment.observeFragmentLifecycle(
        onViewCreated: (Fragment) -> Unit,
        onFragmentDestroy: (Fragment) -> Unit
    ) {
        this.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                owner.lifecycle.removeObserver(this)
                val fragment = this@observeFragmentLifecycle
                fragment
                    .viewLifecycleOwner
                    .lifecycle
                    .addObserver(
                        object : DefaultLifecycleObserver {
                            override fun onCreate(owner: LifecycleOwner) {
                                onViewCreated(fragment)
                            }
                            override fun onDestroy(owner: LifecycleOwner) {
                                onFragmentDestroy(fragment)
                                owner.lifecycle.removeObserver(this)
                            }
                        }
                    )
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(MEDUSA_STACK_STATE_KEY, fragmentStackStateMapper.toBundle(fragmentStackState))
    }

    private fun publishFragmentTransaction(nextFragment: Fragment) {
        val currentFragment = destinationChangeLiveData.value ?: return
        requestedFragmentTransactionLiveData.value = MedusaRequestedFragmentTransaction(
            currentFragment = WeakReference(currentFragment),
            nextFragment = WeakReference(nextFragment),
        )
    }

    companion object {
        const val DEFAULT_GROUP_NAME = ""

        internal const val MEDUSA_STACK_STATE_KEY = "MEDUSA_STACK_STATE_KEY"
    }
}
