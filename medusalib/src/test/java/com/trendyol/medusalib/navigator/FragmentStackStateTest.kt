package com.trendyol.medusalib.navigator

import com.google.common.truth.Truth
import com.trendyol.medusalib.navigator.data.StackItem
import org.junit.Assert.*
import org.junit.Test

class FragmentStackStateTest {

    @Test
    fun `popItemsFromNonEmptyTabs returns all of the stack items in given stack`() {
        val stackItems = buildStackItems()
        val stackState = buildFragmentStackState(stackItems)
        val expectedStackItems = mutableListOf<StackItem>()
        stackItems.forEach { it.forEach { expectedStackItems.add(it) } }

        val actualPoppedItems = stackState.popItemsFromNonEmptyTabs()

        Truth.assertThat(actualPoppedItems).containsExactly(expectedStackItems)
    }
}