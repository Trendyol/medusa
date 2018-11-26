package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment

interface Navigator {

    /**
     * Adds new fragment to the fragment stack
     * Hide currently active fragment and show newly
     * added fragment to the user.
     * @param fragment new fragment
     */
    fun start(fragment: Fragment)

    /**
     * Adds new fragment to the fragment stack with given tab index
     * Hide currently active fragment, switches to given tab Index
     * and show newly added fragment to the user.
     * @param fragment new fragment
     * @param tabIndex fragment will be added to given tabIndex history
     */
    fun start(fragment: Fragment, tabIndex: Int)

    /**
     * Modifies fragment stack. Pops current fragment from
     * fragment stack and detaches it. Peeks from fragment stack
     * and attach it to the fragment transaction.
     */
    fun goBack()

    /**
     * Checks the current fragment stack and decides
     * that user can go back on fragment stack.
     * @return false if user can not go back more, true otherwise
     */
    fun canGoBack(): Boolean

    /**
     * Switches to the another tab. Remembers fragments orders
     * on the last tab before this method is called. If newly switched
     * tab has fragment history, then remembers the history and
     * continue from where user left the tab.
     * If tabIndex param is same with the current one, no action happens.
     * @param tabIndex
     */
    fun switchTab(tabIndex: Int)

    /**
     * Resets the tabIndex history. Detaches and destroy all
     * fragments which are started in passed tabIndex.
     * @param tabIndex reset tabIndex
     * @param resetRootFragment resets root fragment, If false
     * use existing root fragment instance and state
     */
    fun reset(tabIndex: Int, resetRootFragment: Boolean = false)

    /**
     * Resets current tabs and its history.
     * Turn back to root fragment
     * @param resetRootFragment resets root fragment, If false
     * use existing root fragment instance and state
     */
    fun resetCurrentTab(resetRootFragment: Boolean = false)

    /**
     * Resets all tabs and Navigator history.
     * Turns back to initial state and initial tab Index.
     * Destroys all fragments and restart the initial one.
     */
    fun reset()

    /**
     * Checks if stack has only root
     * @param tabIndex
     * @return true if only root fragment exists in the stack
     */
    fun hasOnlyRoot(tabIndex: Int): Boolean

    /**
     * @return current visible fragment
     */
    fun getCurrentFragment(): Fragment?
}