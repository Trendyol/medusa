package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.trendyol.medusalib.common.extensions.*
import com.trendyol.medusalib.navigator.controller.FragmentManagerController
import com.trendyol.medusalib.navigator.data.FragmentData
import com.trendyol.medusalib.navigator.data.StackItem
import com.trendyol.medusalib.navigator.tag.TagCreator
import com.trendyol.medusalib.navigator.tag.UniqueTagCreator
import java.lang.IllegalStateException
import java.util.*

open class MultipleStackNavigator(private val fragmentManager: FragmentManager,
                             private val containerId: Int,
                             private val rootFragments: List<Fragment>,
                             private var navigatorListener: Navigator.NavigatorListener? = null,
                             private val navigatorConfiguration: NavigatorConfiguration = NavigatorConfiguration()) : Navigator {

    private val tagCreator: TagCreator = UniqueTagCreator()

    private val fragmentManagerController = FragmentManagerController(fragmentManager, containerId, navigatorConfiguration.defaultNavigatorTransaction)

    private val fragmentTagStack: MutableList<Stack<StackItem>> = ArrayList()

    private val currentTabIndexStack: Stack<Int> = Stack()

    init {
        initializeStackWithRootFragments()
    }

    override fun start(fragment: Fragment) {
        start(fragment, DEFAULT_GROUP_NAME)
    }

    override fun start(fragment: Fragment, tabIndex: Int) {
        start(fragment, tabIndex, DEFAULT_GROUP_NAME)
    }

    override fun start(fragment: Fragment, tabIndex: Int, fragmentGroupName: String) {
        switchTab(tabIndex)
        start(fragment, fragmentGroupName)
        navigatorListener?.let { it.onTabChanged(tabIndex) }
    }

    override fun start(fragment: Fragment, fragmentGroupName: String) {
        val createdTag = tagCreator.create(fragment)
        val currentTabIndex = currentTabIndexStack.peek()
        val fragmentData = FragmentData(fragment, createdTag)

        if (fragmentTagStack[currentTabIndex].isEmpty()) {
            val rootFragment = rootFragments[currentTabIndex]
            val rootFragmentTag = tagCreator.create(rootFragment)
            val rootFragmentData = FragmentData(rootFragment, rootFragmentTag)
            fragmentManagerController.disableAndStartFragment(getCurrentFragmentTag(), rootFragmentData, fragmentData)
        } else {
            fragmentManagerController.disableAndStartFragment(getCurrentFragmentTag(), fragmentData)
        }

        fragmentTagStack[currentTabIndex].push(StackItem(fragmentTag = createdTag, groupName = fragmentGroupName))
    }

    override fun goBack() {
        if (canGoBack().not()) {
            throw IllegalStateException("Can not call goBack() method because stack is empty.")
        }

        if (canFragmentGoBack().not()) return

        if (shouldExit() && shouldGoBackToInitialIndex()) {
            currentTabIndexStack.insertToBottom(navigatorConfiguration.initialTabIndex)
        }

        val currentTabIndex = currentTabIndexStack.peek()

        if (fragmentTagStack[currentTabIndex].size == 1) {
            fragmentManagerController.disableFragment(getCurrentFragmentTag())
            currentTabIndexStack.pop()
            navigatorListener?.let { it.onTabChanged(currentTabIndexStack.peek()) }
        } else {
            val currentFragmentTag = fragmentTagStack[currentTabIndex].pop().fragmentTag
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
        if (tabIndex == currentTabIndexStack.peek()) return

        fragmentManagerController.disableFragment(getCurrentFragmentTag())

        if (currentTabIndexStack.contains(tabIndex).not()) {
            currentTabIndexStack.push(tabIndex)
        } else {
            currentTabIndexStack.moveToTop(tabIndex)
        }

        showUpperFragment()

        navigatorListener?.onTabChanged(tabIndex)
    }

    override fun reset(tabIndex: Int, resetRootFragment: Boolean) {
        val currentTabIndex = currentTabIndexStack.peek()
        if (tabIndex == currentTabIndex) {
            resetCurrentTab(resetRootFragment)
            return
        }

        clearAllFragments(tabIndex, resetRootFragment)

        if (resetRootFragment) {
            val rootFragment = getRootFragment(tabIndex)
            val createdTag = tagCreator.create(rootFragment)
            fragmentTagStack[tabIndex].push(StackItem(fragmentTag = createdTag))
        }
    }

    override fun resetCurrentTab(resetRootFragment: Boolean) {
        val currentTabIndex = currentTabIndexStack.peek()
        clearAllFragments(currentTabIndex, resetRootFragment)

        if (resetRootFragment) {
            val rootFragment = getRootFragment(currentTabIndex)
            val createdTag = tagCreator.create(rootFragment)
            val rootFragmentData = FragmentData(rootFragment, createdTag)
            fragmentTagStack[currentTabIndex].push(StackItem(fragmentTag = createdTag))
            fragmentManagerController.addFragment(rootFragmentData)
        } else {
            val upperFragmentTag = fragmentTagStack[currentTabIndex].peek().fragmentTag
            fragmentManagerController.enableFragment(upperFragmentTag)
        }
        fragmentManagerController.executePendings()
    }

    override fun reset() {
        clearAllFragments()
        currentTabIndexStack.clear()
        fragmentTagStack.clear()
        initializeStackWithRootFragments()
    }

    override fun clearGroup(fragmentGroupName: String) {
        if (fragmentGroupName == DEFAULT_GROUP_NAME) {
            throw IllegalArgumentException("Fragment group name can not be empty.")
        }

        val currentTabIndex = currentTabIndexStack.peek()
        val currentTabStack = fragmentTagStack[currentTabIndex]

        val updatedTabStack = Stack<StackItem>()
        updatedTabStack.push(currentTabStack[0])

        val deletedStackItems = arrayListOf<StackItem>()

        for (i in 1 until currentTabStack.size) {
            val stackItem = currentTabStack[i]
            if (fragmentGroupName == stackItem.groupName) {
                deletedStackItems.add(stackItem)
            } else {
                updatedTabStack.push(stackItem)
            }
        }

        if (deletedStackItems.isEmpty().not()) {
            fragmentTagStack[currentTabIndex] = updatedTabStack
            fragmentManagerController.removeFragments(deletedStackItems.map { it.fragmentTag }.toList())
            showUpperFragment()
        }
    }

    override fun hasOnlyRoot(tabIndex: Int): Boolean {
        return fragmentTagStack[tabIndex].size <= 1
    }

    override fun getCurrentFragment(): Fragment? {
        val currentTabIndex = currentTabIndexStack.peek()
        val visibleFragmentTag = fragmentTagStack[currentTabIndex].peek().fragmentTag
        return fragmentManagerController.getFragment(visibleFragmentTag)
    }

    private fun initializeStackWithRootFragments() {
        for (i in 0 until rootFragments.size) {
            val stack: Stack<StackItem> = Stack()
            val createdTag = tagCreator.create(rootFragments[i])
            val stackItem = StackItem(fragmentTag = createdTag)
            stack.push(stackItem)
            fragmentTagStack.add(stack)
        }

        val initialTabIndex = navigatorConfiguration.initialTabIndex
        val rootFragmentTag = fragmentTagStack[initialTabIndex].peek().fragmentTag
        val rootFragment = getRootFragment(initialTabIndex)
        val rootFragmentData = FragmentData(rootFragment, rootFragmentTag)
        currentTabIndexStack.push(initialTabIndex)
        with(fragmentManagerController) {
            addFragment(rootFragmentData)
            executePendings()
        }
        navigatorListener?.let { it.onTabChanged(navigatorConfiguration.initialTabIndex) }
    }

    private fun getRootFragment(tabIndex: Int): Fragment = rootFragments[tabIndex]

    private fun showUpperFragment() {
        val upperFragmentTag = fragmentTagStack[currentTabIndexStack.peek()].peek().fragmentTag
        if (fragmentManagerController.isFragmentNull(upperFragmentTag)) {
            val rootFragment = getRootFragment(currentTabIndexStack.peek())
            val rootFragmentData = FragmentData(rootFragment, upperFragmentTag)
            fragmentManagerController.addFragment(rootFragmentData)
        } else {
            fragmentManagerController.enableFragment(upperFragmentTag)
        }
        fragmentManagerController.executePendings()
    }

    private fun getCurrentFragmentTag(): String {
        val currentTabIndex = currentTabIndexStack.peek()
        return fragmentTagStack[currentTabIndex].peek().fragmentTag
    }

    private fun shouldExit(): Boolean {
        return currentTabIndexStack.size == 1 && fragmentTagStack[currentTabIndexStack.peek()].size == 1
    }

    private fun shouldGoBackToInitialIndex(): Boolean {
        return currentTabIndexStack.peek() != navigatorConfiguration.initialTabIndex && navigatorConfiguration.alwaysExitFromInitial
    }

    private fun clearAllFragments() {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        for (tagStack in fragmentTagStack) {
            while (tagStack.isEmpty().not()) {
                val currentFragment = fragmentManager.findFragmentByTag(tagStack.pop().fragmentTag)
                currentFragment?.let { fragmentTransaction.remove(it) }
            }
        }
        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    private fun clearAllFragments(tabIndex: Int, resetRootFragment: Boolean) {
        if (fragmentTagStack[tabIndex].empty()) {
            return
        }

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        while (fragmentTagStack[tabIndex].empty().not()) {
            if (fragmentTagStack[tabIndex].size == 1 && resetRootFragment.not()) {
                break
            }

            val fragmentTagToBeRemoved = fragmentTagStack[tabIndex].pop().fragmentTag
            val fragmentToBeRemoved = fragmentManager.findFragmentByTag(fragmentTagToBeRemoved)
            fragmentToBeRemoved?.let { fragmentTransaction.remove(fragmentToBeRemoved) }
        }

        fragmentTransaction.commitAllowingStateLoss()
        fragmentManager.executePendingTransactions()
    }

    private fun canFragmentGoBack(): Boolean {
        if (getCurrentFragment() is Navigator.OnGoBackListener) {
            return (getCurrentFragment() as Navigator.OnGoBackListener).onGoBack()
        }
        return true
    }

    companion object {
        const val DEFAULT_GROUP_NAME = ""
    }
}