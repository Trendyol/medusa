package com.trendyol.medusalib.navigator

import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentFragment
import org.junit.Assert
import org.junit.Test
import org.junit.function.ThrowingRunnable
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MultipleStackNavigatorGoBackTest {

    @Test
    fun `given MultipleStackNavigator with empty stack, when calling canGoBack, should return false`() {
        launchFragmentInContainer<TestParentFragment>().onFragment {
            val sut = MultipleStackNavigator(
                fragmentManager = it.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ TestChildFragment.newInstance("instance 1") }),
            )
            sut.initialize(null)

            val actual = sut.canGoBack()

            assertThat(actual).isFalse()
        }
    }

    @Test
    fun `given MultipleStackNavigator with empty stack, when going back, should throw exception`() {
        launchFragmentInContainer<TestParentFragment>().onFragment {
            val sut = MultipleStackNavigator(
                fragmentManager = it.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ TestChildFragment.newInstance("instance 1") }),
            )
            sut.initialize(null)

            val exception = Assert.assertThrows(IllegalStateException::class.java, ThrowingRunnable { sut.goBack() })

            assertThat(exception).hasMessageThat().contains("Can not call goBack() method because stack is empty.");
        }
    }
}
