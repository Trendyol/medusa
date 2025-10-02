package com.trendyol.medusalib.navigator

import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.common.truth.Truth.assertThat
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.EmptyStackException

@RunWith(RobolectricTestRunner::class)
class MultipleStackNavigatorGetPendingOrCurrentFragmentTest {

    @Test
    fun `given MultipleStackNavigator with current fragment, when calling getPendingOrCurrentFragment, should return current fragment`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val rootFragment = TestChildFragment.newInstance("root")
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { rootFragment },
                ),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()

            val currentFragment = sut.getPendingOrCurrentFragment()

            assertThat(currentFragment).isEqualTo(rootFragment)
        }
    }

    @Test
    fun `given MultipleStackNavigator with started fragment, when calling getPendingOrCurrentFragment, should return started fragment`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val rootFragment = TestChildFragment.newInstance("root")
            val startedFragment = TestChildFragment.newInstance("started")
            
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { rootFragment },
                ),
            )
            sut.initialize(null)
            sut.start(startedFragment)
            fragment.childFragmentManager.executePendingTransactions()

            val currentFragment = sut.getPendingOrCurrentFragment()

            assertThat(currentFragment).isEqualTo(startedFragment)
        }
    }

    @Test
    fun `given MultipleStackNavigator with multiple tabs, when calling getPendingOrCurrentFragment, should return current tab fragment`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val rootFragment1 = TestChildFragment.newInstance("root 1")
            val rootFragment2 = TestChildFragment.newInstance("root 2")
            
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { rootFragment1 },
                    { rootFragment2 },
                ),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()

            // Initially should return first tab fragment
            val initialFragment = sut.getPendingOrCurrentFragment()
            assertThat(initialFragment).isEqualTo(rootFragment1)

            // Switch to second tab
            sut.switchTab(1)
            fragment.childFragmentManager.executePendingTransactions()

            val currentFragment = sut.getPendingOrCurrentFragment()
            assertThat(currentFragment).isEqualTo(rootFragment2)
        }
    }

    @Test
    fun `given MultipleStackNavigator without initialization, when calling getPendingOrCurrentFragment, should throw exception`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("root") },
                ),
            )
            // Don't initialize

            try {
                sut.getPendingOrCurrentFragment()
                assertThat(false).isTrue() // Should not reach here
            } catch (e: Exception) {
                assertThat(e).isInstanceOf(EmptyStackException::class.java)
            }
        }
    }

    @Test
    fun `given MultipleStackNavigator with pending transaction, when calling getPendingOrCurrentFragment, should return pending fragment`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val rootFragment = TestChildFragment.newInstance("root")
            val pendingFragment = TestChildFragment.newInstance("pending")
            
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { rootFragment },
                ),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()

            // Start a fragment but don't execute pending transactions
            sut.start(pendingFragment)
            // Don't execute pending transactions to keep it as pending

            val currentOrPendingFragment = sut.getPendingOrCurrentFragment()

            // Should return the pending fragment even though transactions haven't been executed
            assertThat(currentOrPendingFragment).isEqualTo(pendingFragment)
        }
    }

    @Test
    fun `given MultipleStackNavigator after going back, when calling getPendingOrCurrentFragment, should return correct fragment`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val rootFragment = TestChildFragment.newInstance("root")
            val startedFragment = TestChildFragment.newInstance("started")
            
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { rootFragment },
                ),
            )
            sut.initialize(null)
            sut.start(startedFragment)
            fragment.childFragmentManager.executePendingTransactions()

            // Verify started fragment is current
            val currentFragment = sut.getPendingOrCurrentFragment()
            assertThat(currentFragment).isEqualTo(startedFragment)

            // Go back to root
            sut.goBack()
            fragment.childFragmentManager.executePendingTransactions()

            val fragmentAfterGoBack = sut.getPendingOrCurrentFragment()
            assertThat(fragmentAfterGoBack).isEqualTo(rootFragment)
        }
    }

    @Test
    fun `given MultipleStackNavigator after reset, when calling getPendingOrCurrentFragment, should return root fragment`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val rootFragment = TestChildFragment.newInstance("root")
            val startedFragment = TestChildFragment.newInstance("started")
            
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { rootFragment },
                ),
            )
            sut.initialize(null)
            sut.start(startedFragment)
            fragment.childFragmentManager.executePendingTransactions()

            // Reset current tab
            sut.resetCurrentTab(resetRootFragment = false)
            fragment.childFragmentManager.executePendingTransactions()

            val fragmentAfterReset = sut.getPendingOrCurrentFragment()
            assertThat(fragmentAfterReset).isEqualTo(rootFragment)
        }
    }
}
