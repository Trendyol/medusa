package com.trendyol.medusalib.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.common.truth.Truth.assertThat
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MultipleStackNavigatorBackstackOrderTest {

    @Test
    fun `given MultipleStackNavigator with empty stack and null as tag, when calling getFragmentIndexInStackBySameType, should return -1`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ TestChildFragment.newInstance("root 1") }),
            )
            sut.initialize(null)

            // When
            val actual = sut.getFragmentIndexInStackBySameType(null)

            // Then
            assertThat(actual).isEqualTo(-1)
        }
    }

    @Test
    fun `given MultipleStackNavigator with empty stack and nonnull tag, when calling getFragmentIndexInStackBySameType, should return -1`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ TestChildFragment.newInstance("root 1") }),
            )
            sut.initialize(null)

            // When
            val actual = sut.getFragmentIndexInStackBySameType("random-tag")

            // Then
            assertThat(actual).isEqualTo(-1)
        }
    }

    @Test
    fun `given MultipleStackNavigator with stack with single fragment and nonnull tag, when calling getFragmentIndexInStackBySameType for current fragment, should return 0`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ TestChildFragment.newInstance("root 1") }),
            )
            sut.initialize(null)

            sut.start(TestChildFragment.newInstance("child fragment"))

            fragment.childFragmentManager.executePendingTransactions()

            // When
            val actual = sut.getFragmentIndexInStackBySameType(sut.getCurrentFragment()?.tag)

            // Then
            assertThat(actual).isEqualTo(0)
        }
    }

    @Test
    fun `given MultipleStackNavigator with stack with multiple fragment and nonnull tag, when calling getFragmentIndexInStackBySameType for first child fragment, should return 2`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ TestChildFragment.newInstance("root 1") }),
            )
            sut.initialize(null)

            val fragments = mutableListOf<Fragment>()
            sut.observeDestinationChanges(fragment.viewLifecycleOwner) {
                fragments.add(it)
            }

            sut.start(TestChildFragment.newInstance("child fragment 1"))
            sut.start(TestChildFragment.newInstance("child fragment 2"))
            sut.start(TestChildFragment.newInstance("child fragment 3"))
            fragment.childFragmentManager.executePendingTransactions()

            // When
            val actual = sut.getFragmentIndexInStackBySameType(fragments[1].tag)

            // Then
            assertThat(actual).isEqualTo(2)
        }
    }

    @Test
    fun `given MultipleStackNavigator with stack with multiple fragment and nonnull tag, when calling getFragmentIndexInStackBySameType for last child fragment, should return 0`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ TestChildFragment.newInstance("root 1") }),
            )
            sut.initialize(null)

            val fragments = mutableListOf<Fragment>()
            sut.observeDestinationChanges(fragment.viewLifecycleOwner) {
                fragments.add(it)
            }

            sut.start(TestChildFragment.newInstance("child fragment 1"))
            sut.start(TestChildFragment.newInstance("child fragment 2"))
            fragment.childFragmentManager.executePendingTransactions()

            // When
            val actual = sut.getFragmentIndexInStackBySameType(fragments[2].tag)

            // Then
            assertThat(actual).isEqualTo(0)
        }
    }

    @Test
    fun `given MultipleStackNavigator with stack with multiple root fragments and nonnull tag and switch tab, when calling getFragmentIndexInStackBySameType for first child in switched tab, should return 1`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { TestChildFragment.newInstance("root 1") },
                    { TestChildFragment.newInstance("root 2") },
                ),
            )
            sut.initialize(null)

            val fragments = mutableListOf<Fragment>()
            sut.observeDestinationChanges(fragment.viewLifecycleOwner) {
                fragments.add(it)
            }

            sut.start(TestChildFragment.newInstance("child fragment 1"))
            sut.start(TestChildFragment.newInstance("child fragment 2"))
            sut.switchTab(1)
            sut.start(TestChildFragment.newInstance("child fragment 1"))
            sut.start(TestChildFragment.newInstance("child fragment 1"))

            fragment.childFragmentManager.executePendingTransactions()

            // When
            val actual = sut.getFragmentIndexInStackBySameType(fragments[4].tag)

            // Then
            assertThat(actual).isEqualTo(1)
        }
    }
}
