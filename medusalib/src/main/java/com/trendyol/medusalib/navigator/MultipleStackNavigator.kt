package com.trendyol.medusalib.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.trendyol.medusalib.navigator.controller.FragmentManagerController
import com.trendyol.medusalib.navigator.data.FragmentData
import com.trendyol.medusalib.navigator.data.StackItem
import com.trendyol.medusalib.navigator.tag.TagCreator
import com.trendyol.medusalib.navigator.tag.UniqueTagCreator
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType

open class MultipleStackNavigator(
    fragmentManager: FragmentManager,
    containerId: Int,
    private val rootFragmentProvider: List<() -> Fragment>,
    private var navigatorListener: Navigator.NavigatorListener? = null,
    private val navigatorConfiguration: NavigatorConfiguration = NavigatorConfiguration(),
    private val transitionAnimationType: TransitionAnimationType? = null
) : Navigator {

    private val tagCreator: TagCreator = UniqueTagCreator()

    private val fragmentManagerController = FragmentManagerController(
        fragmentManager,
        containerId,
        navigatorConfiguration.defaultNavigatorTransaction
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

    override fun start(fragment: Fragment, fragmentGroupName: String, transitionAnimation: TransitionAnimationType?) {

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
        fragmentStackState.addStackItemToSelectedTab(
            StackItem(
                fragmentTag = createdTag,
                groupName = fragmentGroupName
            )
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

        if (resetRootFragment) {
            val rootFragment = getRootFragment(tabIndex)
            val createdTag = tagCreator.create(rootFragment)
            fragmentStackState.addStackItem(tabIndex, StackItem(createdTag))
        }
    }

    override fun resetCurrentTab(resetRootFragment: Boolean) {
        val currentTabIndex = fragmentStackState.getSelectedTabIndex()
        clearAllFragments(currentTabIndex, resetRootFragment)

        if (resetRootFragment) {
            val rootFragment = getRootFragment(currentTabIndex)
            val createdTag = tagCreator.create(rootFragment)
            val rootFragmentData = FragmentData(rootFragment, createdTag)
            fragmentStackState.addStackItemToSelectedTab(StackItem(fragmentTag = createdTag))
            fragmentManagerController.addFragment(rootFragmentData)
        } else {
            val upperFragmentTag = fragmentStackState.peekItemFromSelectedTab().fragmentTag
            fragmentManagerController.enableFragment(upperFragmentTag)
        }
    }

    override fun reset() {
        clearAllFragments()
        fragmentStackState.clear()
        initializeStackState()
    }

    override fun clearGroup(fragmentGroupName: String) {
        if (fragmentGroupName == DEFAULT_GROUP_NAME) {
            throw IllegalArgumentException("Fragment group name can not be empty.")
        }
        val poppedFragmentTags = fragmentStackState
            .popItems(fragmentGroupName)
            .map { it.fragmentTag }

        if (poppedFragmentTags.isNotEmpty()) {
            fragmentManagerController.removeFragments(poppedFragmentTags)
            showUpperFragment()
        }
    }

    override fun hasOnlyRoot(tabIndex: Int): Boolean {
        return fragmentStackState.hasOnlyRoot(tabIndex)
    }

    override fun getCurrentFragment(): Fragment? {
        val visibleFragmentTag = fragmentStackState.peekItemFromSelectedTab().fragmentTag
        return fragmentManagerController.getFragment(visibleFragmentTag)
    }

    override fun initialize(savedState: Bundle?) {
        if (savedState == null) {
            initializeStackState()
        }
        else {
            loadStackStateFromSavedState(savedState)
        }
    }

    private fun initializeStackState() {
        val providedRootFragments = rootFragmentProvider.map { it.invoke() }
        addRootFragmentsToStackState(providedRootFragments)

        val initialTabIndex = navigatorConfiguration.initialTabIndex
        fragmentStackState.switchTab(initialTabIndex)


        val rootFragment = getRootFragment(initialTabIndex)
        val rootFragmentTag = fragmentStackState.peekItem(initialTabIndex).fragmentTag
        val rootFragmentData = FragmentData(rootFragment, rootFragmentTag)
        fragmentManagerController.addFragment(rootFragmentData)
        navigatorListener?.onTabChanged(navigatorConfiguration.initialTabIndex)
    }

    private fun addRootFragmentsToStackState(rootFragments: List<Fragment>) {
        rootFragments.forEachIndexed { index: Int, fragment: Fragment ->
            val createdTag = tagCreator.create(fragment)
            val stackItem = StackItem(fragmentTag = createdTag)
            fragmentStackState.addStackItem(index, stackItem)
        }
    }

    private fun loadStackStateFromSavedState(savedState: Bundle) {
        val stackState = fragmentStackStateMapper.fromBundle(savedState.getBundle(MEDUSA_STACK_STATE_KEY))
        fragmentStackState.setStackState(stackState)
        navigatorListener?.onTabChanged(fragmentStackState.getSelectedTabIndex())
    }

    private fun getRootFragment(tabIndex: Int): Fragment =
        fragmentManagerController.getFragment(fragmentStackState.peekItem(tabIndex).fragmentTag)
        ?: rootFragmentProvider.get(tabIndex).invoke()

    private fun showUpperFragment() {
        val upperFragmentTag = fragmentStackState.peekItemFromSelectedTab().fragmentTag
        if (fragmentManagerController.isFragmentNull(upperFragmentTag)) {
            val rootFragment = getRootFragment(fragmentStackState.getSelectedTabIndex())
            val rootFragmentData = FragmentData(rootFragment, upperFragmentTag)
            fragmentManagerController.addFragment(rootFragmentData)
        } else {
            fragmentManagerController.enableFragment(upperFragmentTag)
        }
    }

    private fun getCurrentFragmentTag() = fragmentStackState.peekItemFromSelectedTab().fragmentTag

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(MEDUSA_STACK_STATE_KEY, fragmentStackStateMapper.toBundle(fragmentStackState))
    }

    companion object {
        const val DEFAULT_GROUP_NAME = ""

        internal const val MEDUSA_STACK_STATE_KEY = "MEDUSA_STACK_STATE_KEY"
    }
}
