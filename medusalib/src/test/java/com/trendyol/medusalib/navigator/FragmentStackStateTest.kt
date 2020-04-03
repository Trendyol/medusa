package com.trendyol.medusalib.navigator

import com.google.common.truth.Truth
import com.trendyol.medusalib.navigator.data.StackItem
import org.junit.Test
import java.util.*

class FragmentStackStateTest {

    @Test
    fun `popItemsFromNonEmptyTabs returns all of the stack items in given stack`() {
        val stackItems = buildStackItems()
        val stackState = buildFragmentStackState(stackItems)
        val expectedStackItems = mutableListOf<StackItem>()
        stackItems.forEach { it.forEach { expectedStackItems.add(it) } }

        val actualPoppedItems = stackState.popItemsFromNonEmptyTabs()

        Truth.assertThat(actualPoppedItems).containsExactlyElementsIn(expectedStackItems)
    }

    @Test
    fun `popItemsFromNonEmptyTabs clears stack item in given stack`() {
        val stackState = buildFragmentStackState()
        val emptyStack = emptyList<Stack<StackItem>>()

        stackState.popItemsFromNonEmptyTabs()

        Truth.assertThat(stackState.fragmentTagStack).containsExactlyElementsIn(emptyStack)
    }

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