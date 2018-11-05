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
     * @param switchToResetTab If true, after removes the history of given tabIndex,
     * switch to the that tab by using tabIndex. False, If you don't want to switch tab
     * and only reset the tab.
     */
    fun reset(tabIndex: Int, switchToResetTab: Boolean)

    /**
     * Resets all tabs and Navigator history.
     * Turn back to initial state and initial tab Index.
     * Destroys all fragments and restart the initial one.
     */
    fun reset()

}