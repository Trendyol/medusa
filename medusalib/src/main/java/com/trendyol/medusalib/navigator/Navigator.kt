package com.trendyol.medusalib.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.trendyol.medusalib.navigator.controller.PreloadedFragmentResult
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
     * Preloads a fragment into the current navigation stack without immediately displaying it.
     *
     * This method attaches the given fragment to the fragment manager and prepares it in a hidden.
     * The fragment will not be visible to the user until it is started later using
     * [startPreloadedFragment].
     *
     * @param fragment The fragment instance to preload.
     * @param fragmentTag The unique tag of fragment.
     */
    fun preloadFragment(fragment: Fragment, fragmentTag: String)

    /**
     * Starts a fragment that was previously preloaded, making it visible.
     *
     * This method takes a previously preloaded fragment identified by its [fragmentTag] and brings it
     * to the foreground. If the fragment was successfully preloaded, it will be shown immediately. If
     * the fragment was not found or not preloaded, the optionally provided [fallbackFragment] will be added
     * and displayed as a fallback.
     *
     * @param fallbackFragment Fragment instance to display if the preloaded fragment cannot be found.
     * @param fragmentTag The unique tag of the previously preloaded fragment to start.
     *
     * @return [PreloadedFragmentResult]
     * - [PreloadedFragmentResult.Success] if the preloaded fragment was found and displayed.
     * - [PreloadedFragmentResult.FallbackSuccess] if the fallback fragment was used instead.
     * - [PreloadedFragmentResult.NotFound] if no suitable fragment was found and no fallback was provided.
     */
    fun startPreloadedFragment(fallbackFragment: Fragment?, fragmentTag: String): PreloadedFragmentResult

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
     * @return pending or current visible fragment
     */
    fun getPendingOrCurrentFragment(): Fragment?

    /**
     * Puts current fragment stack state to given bundle inorder to retain
     * state across activity recreation and process death.
     * @param outState outState parameter of onSaveInstanceState method in
     * your fragments or activities
     */
    fun onSaveInstanceState(outState: Bundle)

    /**
     * Initializes fragment stack state and adds related root fragments to your
     * container if savedState is null. Otherwise reads and deserialize
     * fragment stack state from given bundle.
     *
     * @param savedState savedInstanceState parameter of onCreate method in
     * your fragments or activities
     */
    fun initialize(savedState: Bundle?)

    /**
     * Listeners
     */

    /**
     * Observes any changes made in fragment back stack with the given lifecycle.
     * All implementation of Navigator interface must guarantee following points:
     *
     * - View lifecycle of the fragments that is observed by the listener must be at least in
     * STARTED state.
     *
     * - destinationChangedListener must be removed when the given lifecycle owner is reached
     * DESTROYED state
     */
    fun observeDestinationChanges(
        lifecycleOwner: LifecycleOwner,
        destinationChangedListener: (Fragment) -> Unit,
    )

    /**
     * Observes fragment transaction events within the Navigator and notifies the given listener
     * whenever a transaction occurs that changes the currently displayed fragment.
     *
     * Implementations must ensure the following:
     *
     * - The [transactionListener] will only be triggered when both the current and the next
     *   [Fragment] instances are at least in the [androidx.lifecycle.Lifecycle.State.STARTED] state.
     *
     * - The listener will automatically be removed when the given [lifecycleOwner] reaches the
     *   [androidx.lifecycle.Lifecycle.State.DESTROYED] state, preventing memory leaks.
     *
     * This is useful for observing navigation transitions and reacting to them, such as for
     * analytics, UI updates, or custom navigation handling.
     *
     * @param lifecycleOwner The [LifecycleOwner] whose lifecycle will control the observer.
     * @param transactionListener A callback invoked with the current fragment (being replaced)
     * and the next fragment (being displayed) whenever a fragment transaction occurs.
     */
    fun observeFragmentTransaction(
        lifecycleOwner: LifecycleOwner,
        transactionListener: (currentFragment: Fragment, nextFragment: Fragment) -> Unit,
    )

    /**
     * Retrieves the index of a [Fragment] within the fragment stack based on the specified tag.
     * If the tag is null or empty, returns -1.
     * Iterates through the fragment stack to find the specified tag.
     * Returns the index of the [Fragment] relative to the top of its stack if found; otherwise,
     * returns -1.
     *
     * @param tag The tag of the [Fragment] to search for within the stack.
     * @return The index of the [Fragment] within its stack if found; otherwise, -1.
     */
    fun getFragmentIndexInStackBySameType(tag: String?): Int

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
         * @see <a href="https://github.com/Trendyol/medusa/wiki/Fragment-Lifecycle">Fragment Lifecycle</a>
         */
        fun getNavigatorTransaction(): NavigatorTransaction
    }
}
