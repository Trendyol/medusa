package com.trendyol.medusalib.navigator

import com.google.common.truth.Truth
import org.junit.Test


class FragmentStackStateTest {

    @Test
    fun `given stackstate with tab index stack of (2, 1, 0), when addStackItem is called with tabIndex 1, then new tab index must be (2, 0, 1)`() {
        val stackState = buildFragmentStackState(tabStack = buildTabStack(2, 1, 0))

        stackState.addStackItem(1, buildStackItem())

        Truth.assertThat(stackState.tabIndexStack).containsExactly(2, 0, 1).inOrder()
    }

    @Test
    fun `given stackstate with tab index stack of (2, 1, 0), when addStackItem is called with tabIndex 0, then new tab index must be (2, 1, 0)`() {
        val stackState = buildFragmentStackState(tabStack = buildTabStack(2, 1, 0))

        stackState.addStackItem(0, buildStackItem())

        Truth.assertThat(stackState.tabIndexStack).containsExactly(2, 1, 0).inOrder()
    }
}