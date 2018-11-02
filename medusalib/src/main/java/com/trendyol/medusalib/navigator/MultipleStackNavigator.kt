package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.trendyol.medusalib.extensions.*
import java.lang.IllegalStateException
import java.util.*

class MultipleStackNavigator(private val supportFragmentManager: FragmentManager,
                             private val containerId: Int,
                             private val rootFragments: List<Fragment>,
                             private val defaultTabIndex: Int = 0,
                             private val navigatorListener: NavigatorListener? = null) : Navigator {

    private val fragmentTagStack: MutableList<Stack<String>> = ArrayList()

    private val currentTabIndexStack: Stack<Int> = Stack()

    private var totalFragmentCount = 0

    private var currentFragment: Fragment? = null

    init {
        initializeStackWithRootFragments()
    }

    override fun start(fragment: Fragment) {
        currentFragment?.let { supportFragmentManager.commitHide(it) }
        val createdTag = createTag(fragment)
        val currentTabIndex = currentTabIndexStack.peek()
        supportFragmentManager.commitAdd(containerId, fragment, createdTag)
        fragmentTagStack[currentTabIndex].push(createdTag)
        currentFragment = fragment
    }

    override fun goBack() {
        if (canGoBack().not()) {
            throw IllegalStateException("Can not call goBack() method because stack is empty.")
        }

        val currentTabIndex = currentTabIndexStack.peek()

        if (fragmentTagStack[currentTabIndex].size == 1) {
            currentTabIndexStack.pop()
            currentFragment?.let { supportFragmentManager.commitHide(it) }
            navigatorListener?.let { it.onTabChanged(currentTabIndexStack.peek()) }
        } else {
            currentFragment?.let { supportFragmentManager.commitRemove(fragmentTagStack[currentTabIndex].pop()) }
        }

        currentFragment = findUpperFragmentByIndex(currentTabIndexStack.peek())
        showUpperFragmentByIndex(currentTabIndexStack.peek())
        totalFragmentCount--
    }

    override fun canGoBack(): Boolean {
        if (currentTabIndexStack.size == 1 && fragmentTagStack[currentTabIndexStack.peek()].size == 1) {
            return false
        }
        return true
    }

    override fun switchTab(tabIndex: Int) {
        if (tabIndex == currentTabIndexStack.peek()) return

        currentFragment?.let { supportFragmentManager.commitHide(it) }

        if (currentTabIndexStack.contains(tabIndex).not()) {
            currentFragment = getRootFragment(tabIndex)
            currentTabIndexStack.push(tabIndex)
            commitRootFragment(tabIndex)
        } else {
            currentFragment = findUpperFragmentByIndex(tabIndex)
            currentTabIndexStack.moveToTop(tabIndex)
            supportFragmentManager.commitShow(fragmentTagStack[tabIndex].peek())
        }
    }

    override fun reset(tabIndex: Int, switchToResetTab: Boolean) {

    }

    override fun reset() {
        currentFragment?.let { supportFragmentManager.commitHide(it) }
        totalFragmentCount = 0
        clearAllFragments()
        currentTabIndexStack.clear()
        fragmentTagStack.clear()
        initializeStackWithRootFragments()
        navigatorListener?.let { it.onTabChanged(defaultTabIndex) }
    }

    private fun initializeStackWithRootFragments() {
        for (i in 0 until rootFragments.size) {
            val stack: Stack<String> = Stack()
            val createdTag = createTag(rootFragments[i])
            stack.push(createdTag)
            fragmentTagStack.add(stack)
        }


        currentFragment = getRootFragment(defaultTabIndex)
        currentTabIndexStack.push(defaultTabIndex)
        commitRootFragment(defaultTabIndex)
    }

    private fun commitRootFragment(tabIndex: Int) {
        val rootFragment = getRootFragment(tabIndex)
        val rootFragmentTag = fragmentTagStack[tabIndex].peek()
        supportFragmentManager.commitAdd(containerId, rootFragment, rootFragmentTag)
    }

    private fun getRootFragment(tabIndex: Int): Fragment = rootFragments[tabIndex]

    private fun findUpperFragmentByIndex(tabIndex: Int): Fragment? {
        val fragmentTag = fragmentTagStack[tabIndex].peek()
        return findFragment(fragmentTag)
    }

    private fun findFragment(fragmentTag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(fragmentTag)
    }

    private fun showUpperFragmentByIndex(tabIndex: Int) {
        val upperFragmentTag = fragmentTagStack[tabIndex].peek()
        supportFragmentManager.commitShow(upperFragmentTag)
    }

    private fun createTag(fragment: Fragment): String {
        return fragment.javaClass.name + ++totalFragmentCount
    }

    private fun clearAllFragments() {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        for (tagStack in fragmentTagStack) {
            while (tagStack.isEmpty().not()) {
                findFragment(tagStack.pop())?.let { fragmentTransaction.remove(it) }
            }
        }
        fragmentTransaction.commit()
        supportFragmentManager.executePendingTransactions()
    }

    fun getTotalFragmentCount(): Int {
        return totalFragmentCount
    }
}