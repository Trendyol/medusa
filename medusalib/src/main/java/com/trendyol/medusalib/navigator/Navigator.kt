package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.trendyol.medusalib.extensions.*
import java.util.*

class Navigator(private val supportFragmentManager: FragmentManager,
                private val containerId: Int,
                private val rootFragments: List<Fragment>,
                private val defaultTabIndex: Int = 0,
                private val navigatorListener: NavigatorListener? = null) {

    private val fragmentTagStack: MutableList<Stack<String>> = ArrayList()

    private val currentTabIndexStack: Stack<Int> = Stack()

    private var currentIndex = defaultTabIndex

    private var totalFragmentCount = 0

    private var currentFragment: Fragment? = null

    init {
        initializeStackWithRootFragments()
    }

    fun start(fragment: Fragment) {
        currentFragment?.let { supportFragmentManager.commitHide(it) }
        val createdTag = createTag(fragment)
        fragmentTagStack[currentIndex].push(createdTag)
        supportFragmentManager.commitAdd(containerId, fragment, createdTag)
        currentFragment = fragment
    }

    fun switchTab(tabIndex: Int) {
        if (tabIndex == currentIndex) return

        navigatorListener?.let { it.onTabChanged(tabIndex) }
        currentFragment?.let { supportFragmentManager.commitHide(it) }


        if (currentTabIndexStack.contains(tabIndex).not()) {
            currentFragment = getRootFragment(tabIndex)
            currentTabIndexStack.push(tabIndex)
            commitRootFragment(tabIndex)
        } else {
            currentFragment = findUpperFragmentByIndex(tabIndex)
            currentTabIndexStack.removeAndPush(tabIndex)
            supportFragmentManager.commitShow(fragmentTagStack[tabIndex].peek())
        }

        currentIndex = tabIndex
    }

    fun back() {

    }

    private fun initializeStackWithRootFragments() {
        for (i in 0 until rootFragments.size) {
            val stack: Stack<String> = Stack()
            val createdTag = createTag(rootFragments[i])
            stack.push(createdTag)
            fragmentTagStack.add(stack)
        }


        commitRootFragment(defaultTabIndex)
        currentTabIndexStack.push(defaultTabIndex)
        currentFragment = findUpperFragmentByIndex(defaultTabIndex)
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

    private fun createTag(fragment: Fragment): String {
        return fragment.javaClass.name + ++totalFragmentCount
    }

}