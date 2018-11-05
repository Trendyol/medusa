package com.trendyol.medusalib.navigator

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