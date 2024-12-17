package com.trendyol.medusalib.navigator

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentWithNavigatorFragment
import com.trendyol.medusalib.navigator.controller.PreloadedFragmentResult
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreloadFragmentTest {

    @Test
    fun testPreloadFragment() {
        val scenario = launchFragmentInContainer<TestParentWithNavigatorFragment>()
        scenario.moveToState(Lifecycle.State.RESUMED)

        scenario.onFragment { parentFragment ->
            val navigator = parentFragment.navigator

            val fragmentTag = "preloaded_test_fragment"
            val testChildFragment = TestChildFragment.newInstance("Preload Fragment")

            navigator.preloadFragment(testChildFragment, fragmentTag)
            parentFragment.childFragmentManager.executePendingTransactions()

            val preloadedFragment = parentFragment.childFragmentManager.findFragmentByTag(fragmentTag)
            Truth.assertThat(preloadedFragment).isNotNull()
            Truth.assertThat(preloadedFragment?.isVisible).isFalse()
        }
    }

    @Test
    fun testStartPreloadedFragment() {
        val scenario = launchFragmentInContainer<TestParentWithNavigatorFragment>()
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { parentFragment ->
            val fragmentTag = "preloaded_test_fragment"
            val navigator = parentFragment.navigator
            val testChildFragment = TestChildFragment.newInstance("Preload Fragment")

            navigator.preloadFragment(testChildFragment, fragmentTag)
            parentFragment.childFragmentManager.executePendingTransactions()

            val result = navigator.startPreloadFragment(null, fragmentTag)
            parentFragment.childFragmentManager.executePendingTransactions()

            Truth.assertThat(result).isEqualTo(PreloadedFragmentResult.Success)
            val startedFragment = parentFragment.childFragmentManager.findFragmentByTag(fragmentTag)
            Truth.assertThat(startedFragment).isNotNull()
            Truth.assertThat(startedFragment?.isVisible).isTrue()
        }
    }

    @Test
    fun testStartPreloadedFragmentWithFallback() {
        val scenario = launchFragmentInContainer<TestParentWithNavigatorFragment>()
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { parentFragment ->
            val fragmentTag = "non_existent_fragment"
            val navigator = parentFragment.navigator
            val fallbackFragment = TestChildFragment.newInstance("Fallback Fragment")

            val result = navigator.startPreloadFragment(fallbackFragment, fragmentTag)
            parentFragment.childFragmentManager.executePendingTransactions()

            val startedFragment = parentFragment.childFragmentManager.findFragmentByTag(fragmentTag)
            Truth.assertThat(result).isEqualTo(PreloadedFragmentResult.FallbackSuccess)
            Truth.assertThat(startedFragment).isNotNull()
            Truth.assertThat(startedFragment?.isVisible).isTrue()
        }
    }

    @Test
    fun testStartPreloadedFragmentNotFound() {
        val scenario = launchFragmentInContainer<TestParentWithNavigatorFragment>()
        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { parentFragment ->
            val fragmentTag = "non_existent_fragment"
            val navigator = parentFragment.navigator

            val result = navigator.startPreloadFragment(null, fragmentTag)
            parentFragment.childFragmentManager.executePendingTransactions()

            val fragment = parentFragment.childFragmentManager.findFragmentByTag(fragmentTag)
            Truth.assertThat(result).isEqualTo(PreloadedFragmentResult.NotFound)
            Truth.assertThat(fragment).isNull()
        }
    }
}