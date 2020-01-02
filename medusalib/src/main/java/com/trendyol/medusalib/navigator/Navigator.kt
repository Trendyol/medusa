package com.trendyol.medusalib.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType

interface Navigator {

    /**
     * Adds new fragment to the fragment stack
     * Hide currently active fragment and show newly
     * added fragment to the user.
     * @param fragment new fragment
     */
    fun start(fragment: Fragment)

    /**
     * Adds new fragment to the fragment stack with given group name.
     * Group name is used to keep fragments in the same group. So
     * you can easily remove only grouped fragments.
     * Hide currently active fragment and show newly
     * added fragment to the user.
     * @param fragment new fragment
     * @param fragmentGroupName will be used in case of you want to remove
     * all fragments which has the same group name.
     */
    fun start(fragment: Fragment, fragmentGroupName: String)

    /**
     * Adds new fragment to the fragment stack with given tab index
     * Hide currently active fragment, switches to given tab Index
     * and show newly added fragment to the user.
     * @param fragment new fragment
     * @param tabIndex fragment will be added to given tabIndex history
     */
    fun start(fragment: Fragment, tabIndex: Int)

    /**
     * Adds new fragment to the fragment stack with given tab index.
     * Hide currently active fragment, switches to given tab Index
     * and show newly added fragment to the user.
     * @param fragment new fragment
     * @param tabIndex fragment will be added to given tabIndex history
     * @param fragmentGroupName will be used in case of you want to remove
     * all fragments which has the same group name.
     */
    fun start(fragment: Fragment, tabIndex: Int, fragmentGroupName: String)

    /**
     * Adds new fragment to the fragment stack with given tab index.
     * Hide currently active fragment, switches to given tab Index
     * and show newly added fragment to the user.
     * @param fragment new fragment
     * @param fragmentGroupName will be used in case of you want to remove
     * @param transitionAnimation will be used start and remove fragment transition animation
     * all fragments which has the same group name.
     */
    fun start(fragment: Fragment, fragmentGroupName: String, transitionAnimation: TransitionAnimationType?)

    /**
     * Adds new fragment to the fragment stack with given tab index.
     * Hide currently active fragment, switches to given tab Index
     * and show newly added fragment to the user.
     * @param fragment new fragment
     * @param transitionAnimation will be used start and remove fragment transition animation
     * all fragments which has the same group name.
     */
    fun start(fragment: Fragment, transitionAnimation: TransitionAnimationType)

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
     * Resets all tabs and Navigator history and
     * creates a new one with given fragment provider.
     */
    fun resetWithFragmentProvider(rootFragmentProvider: List<() -> Fragment>)

    /**
     * Clears all fragments with given group name in the
     * current tab. This method aims to clear all
     * related/steps fragments from tab.
     * For instance; you can navigate user to 3 fragment step by step,
     * When user success in this navigation, clears 3 steps from history.
     */
    fun clearGroup(fragmentGroupName: String)

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

    /**
     * Puts current fragment stack state to given bundle inorder to retain
     * state across activity recreation and process death.
     * @param outState outState parameter of onSaveInstanceState method in
     * your fragments or activities
     */
    fun onSaveInstanceState(outState: Bundle)

    /*
    * Initializes fragment stack state and adds related root fragments to your
    * container if savedState is null. Otherwise reads and deserialize
    * fragment stack state from given bundle.
    * @param outState savedInstanceState parameter of onCreate method in
     * your fragments or activities
    */
    fun initialize(savedState: Bundle?)

    /**
     * Listeners
     */

    interface NavigatorListener {

        /**
         * Called when user pressed to the back button and
         * fragment stack is empty in current tab index.
         *
         * @param tabIndex is passed from navigator library and client
         * can use tabIndex parameter to update navigation UI
         */
        fun onTabChanged(tabIndex: Int)
    }

    interface OnGoBackListener {
        /**
         * On some fragments, fragment can decide that it
         * can goes back or not. Override this method
         * to use fragment specific logic
         * to let navigator pop it from stack.
         *
         * For example; If you have recyclerview and you want
         * to scroll to top before user goes back, you can use this
         * interface method.
         *
         * @return false if fragment doesn't let the navigator
         * go back, true otherwise.
         */
        fun onGoBack(): Boolean
    }

    interface OnNavigatorTransactionListener {
        /**
         * Decide the way fragment transaction.
         * Fragment can override this method and
         * change the transaction behaviour of the
         * fragment.
         * @return NavigatorTransaction type (ATTACH_DETACH or SHOW_HIDE)
         *
         * @see https://github.com/Trendyol/medusa/wiki/Fragment-Lifecycle
         */
        fun getNavigatorTransaction(): NavigatorTransaction
    }
}



