package com.trendyol.medusalib.navigator

import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.common.truth.Truth.assertThat
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MultipleStackNavigatorResetTest {

    @Test
    fun `given MultipleStackNavigator without an upper fragment, when resetting with tab index, should reset to root`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("instance 1") },
                    { TestChildFragment.newInstance("instance 2") },
                    { TestChildFragment.newInstance("instance 3") },
                ),
            )
            sut.initialize(null)

            sut.reset(tabIndex = 0, resetRootFragment = false)
            val executedTransactions = fragment.childFragmentManager.executePendingTransactions()

            assertThat(sut.hasOnlyRoot(0)).isTrue()
            assertThat(executedTransactions).isTrue()
        }
    }

    @Test
    fun `given MultipleStackNavigator without an upper fragment, when resetting root with tab index, should reset to root`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("instance 1") },
                    { TestChildFragment.newInstance("instance 2") },
                    { TestChildFragment.newInstance("instance 3") },
                ),
            )
            sut.initialize(null)

            sut.reset(tabIndex = 0, resetRootFragment = true)
            val executedTransactions = fragment.childFragmentManager.executePendingTransactions()

            assertThat(sut.hasOnlyRoot(0)).isTrue()
            assertThat(executedTransactions).isTrue()
        }
    }

    @Test
    fun `given MultipleStackNavigator without an upper fragment, when resetting current tab, should reset to root`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("instance 1") },
                    { TestChildFragment.newInstance("instance 2") },
                    { TestChildFragment.newInstance("instance 3") },
                ),
            )
            sut.initialize(null)

            sut.resetCurrentTab(resetRootFragment = false)
            val executedTransactions = fragment.childFragmentManager.executePendingTransactions()

            assertThat(sut.hasOnlyRoot(0)).isTrue()
            assertThat(executedTransactions).isTrue()
        }
    }

    @Test
    fun `given MultipleStackNavigator without an upper fragment, when resetting current tab to root, should reset to root`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("instance 1") },
                    { TestChildFragment.newInstance("instance 2") },
                    { TestChildFragment.newInstance("instance 3") },
                ),
            )
            sut.initialize(null)

            sut.resetCurrentTab(resetRootFragment = true)
            val executedTransactions = fragment.childFragmentManager.executePendingTransactions()

            assertThat(sut.hasOnlyRoot(0)).isTrue()
            assertThat(executedTransactions).isTrue()
        }
    }
}
