package com.trendyol.medusalib.navigator

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.trendyol.medusalib.navigator.MultipleStackNavigator.Companion.DEFAULT_GROUP_NAME
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType

open class SingleStackNavigator(
    fragmentManager: FragmentManager,
    containerId: Int,
    rootFragmentProvider: () -> Fragment,
    navigatorConfiguration: NavigatorConfiguration = NavigatorConfiguration(),
    transitionAnimationType: TransitionAnimationType? = null
) {

    private val multiStackNavigator: MultipleStackNavigator =
        MultipleStackNavigator(
            fragmentManager = fragmentManager,
            containerId = containerId,
            rootFragmentProvider = listOf(rootFragmentProvider),
            navigatorConfiguration = navigatorConfiguration,
            transitionAnimationType = transitionAnimationType
        )

    fun start(fragment: Fragment) {
        multiStackNavigator.start(fragment, DEFAULT_GROUP_NAME)
    }

    fun start(fragment: Fragment, fragmentGroupName: String) {
        multiStackNavigator.start(fragment, fragmentGroupName)
    }

    fun start(fragment: Fragment, fragmentGroupName: String, transitionAnimation: TransitionAnimationType?) {
        multiStackNavigator.start(fragment, fragmentGroupName, transitionAnimation)
    }

    fun start(fragment: Fragment, transitionAnimation: TransitionAnimationType) {
        multiStackNavigator.start(fragment, DEFAULT_GROUP_NAME, transitionAnimation)
    }

    fun goBack() {
        multiStackNavigator.goBack()
    }

    fun canGoBack(): Boolean {
        return multiStackNavigator.canGoBack()
    }

    fun reset() {
        multiStackNavigator.reset()
    }

    fun resetCurrentTab(resetRootFragment: Boolean) {
        multiStackNavigator.reset(tabIndex = 0, resetRootFragment = resetRootFragment)
    }

    fun clearGroup(fragmentGroupName: String) {
        multiStackNavigator.clearGroup(fragmentGroupName)
    }

    fun getCurrentFragment(): Fragment? {
        return multiStackNavigator.getCurrentFragment()
    }

    fun onSaveInstanceState(outState: Bundle) {
        multiStackNavigator.onSaveInstanceState(outState)
    }

    fun initialize(savedState: Bundle?) {
        multiStackNavigator.initialize(savedState)
    }

}