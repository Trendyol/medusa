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
    fun `popItems removes all items matching given group names from selected tab`() {
        val stackState = buildFragmentStackState(
            buildTabStack(0),
            buildStack(
                StackItem("ROOT"),
                StackItem("TAG1", "GROUP_1"),
                StackItem("TAG2", "GROUP_2"),
                StackItem("TAG3", "GROUP_1"),
                StackItem("TAG4", "GROUP_3"),
            ),
        )

        val actualPoppedItems = stackState.popItems(setOf("GROUP_1", "GROUP_3"))

        Truth.assertThat(actualPoppedItems).containsExactly(
            StackItem("TAG1", "GROUP_1"),
            StackItem("TAG3", "GROUP_1"),
            StackItem("TAG4", "GROUP_3"),
        ).inOrder()
        Truth.assertThat(stackState.fragmentTagStack[0]).containsExactly(
            StackItem("ROOT"),
            StackItem("TAG2", "GROUP_2"),
        ).inOrder()
    }
}