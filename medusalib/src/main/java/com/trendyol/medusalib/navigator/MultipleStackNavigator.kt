package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.trendyol.medusalib.common.extensions.*
import java.lang.IllegalStateException
import java.util.*

class MultipleStackNavigator(private val fragmentManager: FragmentManager,
                             private val containerId: Int,
                             private val rootFragments: List<Fragment>,
                             private val defaultTabIndex: Int = 0,
                             private val navigatorListener: NavigatorListener? = null) : Navigator {

    private val tagCreator: TagCreator = UniqueTagCreator()

    private val fragmentTagStack: MutableList<Stack<String>> = ArrayList()

    private val currentTabIndexStack: Stack<Int> = Stack()

    init {
        initializeStackWithRootFragments()
    }

    override fun start(fragment: Fragment) {
        val createdTag = tagCreator.create(fragment)
        val currentTabIndex = currentTabIndexStack.peek()
        fragmentManager.inTransaction {
            detach(getCurrentFragment())
            add(containerId, fragment, createdTag)
        }
        fragmentTagStack[currentTabIndex].push(createdTag)
    }

    override fun goBack() {
        if (canGoBack().not()) {
            throw IllegalStateException("Can not call goBack() method because stack is empty.")
        }

        val currentTabIndex = currentTabIndexStack.peek()

        if (fragmentTagStack[currentTabIndex].size == 1) {
            fragmentManager.commitDetach(getCurrentFragment())
            currentTabIndexStack.pop()
            navigatorListener?.let { it.onTabChanged(currentTabIndexStack.peek()) }
        } else {
            val currentFragmentTag = fragmentTagStack[currentTabIndex].pop()
            fragmentManager.commitRemove(currentFragmentTag)
        }

        showUpperFragmentByIndex(currentTabIndexStack.peek())
    }

    override fun canGoBack(): Boolean {
        if (currentTabIndexStack.size == 1 && fragmentTagStack[currentTabIndexStack.peek()].size == 1) {
            return false
        }
        return true
    }

    override fun switchTab(tabIndex: Int) {
        if (tabIndex == currentTabIndexStack.peek()) return

        fragmentManager.commitDetach(getCurrentFragment())

        if (currentTabIndexStack.contains(tabIndex).not()) {
            val rootFragmentTag = fragmentTagStack[tabIndex].peek()
            currentTabIndexStack.push(tabIndex)
            fragmentManager.commitAdd(containerId, getRootFragment(tabIndex), rootFragmentTag)
        } else {
            currentTabIndexStack.moveToTop(tabIndex)
            fragmentManager.commitAttach(fragmentTagStack[tabIndex].peek())
        }
    }

    override fun reset(tabIndex: Int, switchToResetTab: Boolean) {

    }

    override fun reset() {
        clearAllFragments()
        currentTabIndexStack.clear()
        fragmentTagStack.clear()
        initializeStackWithRootFragments()
        navigatorListener?.let { it.onTabChanged(defaultTabIndex) }
    }

    private fun initializeStackWithRootFragments() {
        for (i in 0 until rootFragments.size) {
            val stack: Stack<String> = Stack()
            val createdTag = tagCreator.create(rootFragments[i])
            stack.push(createdTag)
            fragmentTagStack.add(stack)
        }

        val rootFragmentTag = fragmentTagStack[defaultTabIndex].peek()
        currentTabIndexStack.push(defaultTabIndex)
        fragmentManager.commitAdd(containerId, getRootFragment(defaultTabIndex), rootFragmentTag)
    }

    private fun getRootFragment(tabIndex: Int): Fragment = rootFragments[tabIndex]

    private fun showUpperFragmentByIndex(tabIndex: Int) {
        val upperFragmentTag = fragmentTagStack[tabIndex].peek()
        fragmentManager.commitAttach(upperFragmentTag)
    }

    private fun getCurrentFragment(): Fragment {
        val currentTabIndex = currentTabIndexStack.peek()
        val currentFragmentTag = fragmentTagStack[currentTabIndex].peek()
        return fragmentManager.findFragmentByTag(currentFragmentTag)
    }

    private fun clearAllFragments() {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        for (tagStack in fragmentTagStack) {
            while (tagStack.isEmpty().not()) {
                val currentFragment = fragmentManager.findFragmentByTag(tagStack.pop())
                currentFragment?.let { fragmentTransaction.remove(it) }
            }
        }
        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    companion object {
        private const val TAG_DIVIDER = "-_-"
    }
}