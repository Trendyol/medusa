package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment

interface Navigator {

    fun start(fragment: Fragment)

    fun goBack()

    fun canGoBack(): Boolean

    fun switchTab(tabIndex: Int)

    fun reset(tabIndex: Int, switchToResetTab: Boolean)

    fun reset()

}