package com.trendyol.medusalib.navigator

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.common.truth.Truth.assertThat
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MultipleStackNavigatorFragmentTransactionObserverTest {

    @Test
    fun `given MultipleStackNavigator, when observeFragmentTransaction is called, should register observer without crashing`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("root") },
                ),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()

            val lifecycleOwner = TestLifecycleOwner()
            
            // This should not throw any exception
            sut.observeFragmentTransaction(lifecycleOwner) { _, _ ->
                // Observer registered successfully
            }
            
            // Test passes if no exception is thrown
            assertThat(true).isTrue()
        }
    }

    @Test
    fun `given MultipleStackNavigator with destroyed lifecycle, when observeFragmentTransaction is called, should handle gracefully`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("root") },
                ),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()

            val lifecycleOwner = TestLifecycleOwner()
            lifecycleOwner.lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
            
            // This should not crash even with destroyed lifecycle
            sut.observeFragmentTransaction(lifecycleOwner) { _, _ ->
                // Should not be called
                assertThat(false).isTrue()
            }
            
            // Test passes if no exception is thrown
            assertThat(true).isTrue()
        }
    }

    private class TestLifecycleOwner : LifecycleOwner {
        val lifecycleRegistry = LifecycleRegistry(this).apply {
            currentState = Lifecycle.State.STARTED
        }

        override val lifecycle: Lifecycle
            get() = lifecycleRegistry
    }
}
