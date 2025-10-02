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
}