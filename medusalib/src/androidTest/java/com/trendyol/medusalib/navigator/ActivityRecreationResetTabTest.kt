package com.trendyol.medusalib.navigator

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentWithNavigatorFragment
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityRecreationResetTabTest {

    @Test
    fun givenWithMultipleStackNavigatorWhenresetCurrentTabIsCalledAfterActionRecreationThenRootFragmentMustBevVisible() {
        // Given
        launchFragmentInContainer<TestParentWithNavigatorFragment>(
            initialState = Lifecycle.State.INITIALIZED
        )
            .moveToState(Lifecycle.State.RESUMED)
            .onFragment {
                it.navigator.switchTab(2)
                it.childFragmentManager.executePendingTransactions()

                it.navigator.start(TestChildFragment.newInstance("Root3-1"))
                it.childFragmentManager.executePendingTransactions()

                it.navigator.start(TestChildFragment.newInstance("Root3-2"))
                it.childFragmentManager.executePendingTransactions()
            }
            // When
            .recreate()
            // Then
            .onFragment {
                it.navigator.resetCurrentTab(resetRootFragment = false)
                it.childFragmentManager.executePendingTransactions()
                Truth.assertThat(
                    it
                        .childFragmentManager
                        .fragments.first { (it as TestChildFragment).requireArgumentTitle() == "Root3" }
                        .isVisible
                ).isTrue()
            }

    }
}