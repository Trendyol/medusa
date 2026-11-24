package com.trendyol.medusalib.navigator

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
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

    @Test
    fun `given a retained destination accessing its fragment manager when started then keeps the previous fragment attached`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val rootFragment = TestChildFragment.newInstance("root 1")
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ rootFragment }),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()

            val nextFragment = KeepPreviousVisibleChildFragment()

            // When
            sut.start(nextFragment)
            fragment.childFragmentManager.executePendingTransactions()

            // Then
            assertThat(sut.getCurrentFragment()).isSameInstanceAs(nextFragment)
            assertThat(nextFragment.parentFragmentManager).isSameInstanceAs(fragment.childFragmentManager)
            assertThat(rootFragment.isDetached).isFalse()
            assertThat(rootFragment.isAdded).isTrue()
            assertThat(rootFragment.isHidden).isFalse()
            assertThat(rootFragment.lifecycle.currentState).isEqualTo(Lifecycle.State.STARTED)
        }
    }

    @Test
    fun `given retained previous fragment when navigating back then resumes with the same view`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val rootFragment = TestChildFragment.newInstance("root 1")
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ rootFragment }),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()
            val rootView = rootFragment.requireView()
            sut.start(KeepPreviousVisibleChildFragment())
            fragment.childFragmentManager.executePendingTransactions()

            // When
            sut.goBack()
            fragment.childFragmentManager.executePendingTransactions()

            // Then
            assertThat(rootFragment.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
            assertThat(rootFragment.view).isSameInstanceAs(rootView)
            assertThat(sut.getCurrentFragment()).isSameInstanceAs(rootFragment)
        }
    }

    @Test
    fun `given a retained source when the container resizes then its height stays stable until navigation returns`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val rootFragment = TestChildFragment.newInstance("root 1")
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ rootFragment }),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()
            val container = fragment.requireView()
            layoutContainer(container, 1_640)
            val rootView = rootFragment.requireView()
            val originalLayoutHeight = rootView.layoutParams.height
            sut.start(KeepPreviousVisibleChildFragment())
            fragment.childFragmentManager.executePendingTransactions()

            // When: hiding navigation chrome grows the container behind the overlay.
            layoutContainer(container, 1_920)

            // Then: the source keeps its pre-navigation geometry.
            assertThat(rootView.height).isEqualTo(1_640)
            sut.goBack()
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(rootView.layoutParams.height).isEqualTo(originalLayoutHeight)
            layoutContainer(container, 1_800)
            assertThat(rootView.height).isEqualTo(1_800)
        }
    }

    private fun layoutContainer(view: View, height: Int) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(1_080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
        )
        view.layout(0, 0, 1_080, height)
    }

    @Test
    fun `given a retained source when its owner opens an inner screen then the source survives navigation back`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val root = KeepPreviousVisibleChildFragment()
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ root }),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()
            val rootView = root.requireView()
            val first = KeepPreviousVisibleChildFragment()
            sut.start(first)
            fragment.childFragmentManager.executePendingTransactions()
            val firstView = first.requireView()

            // When
            sut.start(TestChildFragment.newInstance("reviews"))
            fragment.childFragmentManager.executePendingTransactions()

            // Then
            assertThat(root.isDetached).isFalse()
            assertThat(root.isHidden).isTrue()
            assertThat(root.view).isSameInstanceAs(rootView)
            assertThat(rootView.visibility).isEqualTo(View.GONE)
            assertThat(root.lifecycle.currentState).isEqualTo(Lifecycle.State.STARTED)
            assertThat(first.isDetached).isTrue()
            sut.goBack()
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(first.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
            assertThat(first.view).isNotSameInstanceAs(firstView)
            assertThat(root.view).isSameInstanceAs(rootView)
            assertThat(root.isHidden).isFalse()
            assertThat(rootView.visibility).isEqualTo(View.VISIBLE)
            sut.start(TestChildFragment.newInstance("questions"))
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(root.isHidden).isTrue()
            sut.goBack()
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(root.view).isSameInstanceAs(rootView)
            assertThat(root.isHidden).isFalse()
            sut.goBack()
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(root.isHidden).isFalse()
            assertThat(root.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
            assertThat(root.retentionStates).containsExactly(true, false).inOrder()
        }
    }

    @Test
    fun `given a retained source hidden by an inner screen when resetting the tab then the source is visible`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val root = TestChildFragment.newInstance("root 1")
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ root }),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()
            sut.start(KeepPreviousVisibleChildFragment())
            fragment.childFragmentManager.executePendingTransactions()
            sut.start(TestChildFragment.newInstance("reviews"))
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(root.isHidden).isTrue()

            // When
            sut.resetCurrentTab()
            fragment.childFragmentManager.executePendingTransactions()

            // Then
            assertThat(sut.getCurrentFragment()).isSameInstanceAs(root)
            assertThat(root.isDetached).isFalse()
            assertThat(root.isHidden).isFalse()
            assertThat(root.requireView().visibility).isEqualTo(View.VISIBLE)
            assertThat(root.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
        }
    }

    @Test
    fun `given a retained source when opening another retained destination then only the immediate source stays attached`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val root = KeepPreviousVisibleChildFragment()
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf({ root }),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()
            val first = KeepPreviousVisibleChildFragment()
            sut.start(first)
            fragment.childFragmentManager.executePendingTransactions()
            val firstView = first.requireView()
            val second = KeepPreviousVisibleChildFragment()

            // When
            sut.start(second)
            fragment.childFragmentManager.executePendingTransactions()

            // Then
            assertThat(root.isDetached).isTrue()
            assertThat(root.view).isNull()
            assertThat(first.isDetached).isFalse()
            assertThat(first.lifecycle.currentState).isEqualTo(Lifecycle.State.STARTED)
            assertThat(second.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
            assertThat(first.retentionStates).containsExactly(true)
            sut.goBack()
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(first.view).isSameInstanceAs(firstView)
            assertThat(first.retentionStates).containsExactly(true, false).inOrder()
            assertThat(root.isDetached).isTrue()
        }
    }

    @Test
    fun `given a retained source when switching tabs then its view is released`() {
        launchFragmentInContainer<TestParentFragment>().onFragment { fragment ->
            // Given
            val root = KeepPreviousVisibleChildFragment()
            val sut = MultipleStackNavigator(
                fragmentManager = fragment.childFragmentManager,
                containerId = TestParentFragment.CONTAINER_ID,
                rootFragmentProvider = listOf(
                    { root },
                    { TestChildFragment.newInstance("root 2") },
                ),
            )
            sut.initialize(null)
            fragment.childFragmentManager.executePendingTransactions()
            val destination = KeepPreviousVisibleChildFragment()
            sut.start(destination)
            fragment.childFragmentManager.executePendingTransactions()

            // When
            sut.switchTab(1)
            fragment.childFragmentManager.executePendingTransactions()

            // Then
            assertThat(root.isDetached).isTrue()
            assertThat(root.view).isNull()
            assertThat(destination.isDetached).isTrue()
            sut.switchTab(0)
            fragment.childFragmentManager.executePendingTransactions()
            assertThat(destination.lifecycle.currentState).isEqualTo(Lifecycle.State.RESUMED)
            assertThat(root.isDetached).isTrue()
        }
    }

    class KeepPreviousVisibleChildFragment : Fragment(), Navigator.KeepPreviousFragmentVisible,
        Navigator.OnRetainedAsBackgroundListener {

        val retentionStates = mutableListOf<Boolean>()

        override fun onCreateView(
            inflater: android.view.LayoutInflater,
            container: android.view.ViewGroup?,
            savedInstanceState: android.os.Bundle?,
        ): android.view.View = android.view.View(requireContext())

        override fun keepPreviousFragmentVisible(): Boolean = parentFragmentManager.fragments.isNotEmpty()

        override fun onRetainedAsBackground(retained: Boolean) {
            retentionStates.add(retained)
        }
    }
}
