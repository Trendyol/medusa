package com.trendyol.medusalib.navigator

import com.trendyol.medusalib.navigator.data.StackItem
import java.util.*


fun buildFragmentStackState(stackItems: MutableList<Stack<StackItem>> = buildStackItems()): FragmentStackState {
    return FragmentStackState(
        stackItems,
        Stack<Int>().apply {
            push(2)
            push(1)
            push(0)
        }
    )
}

fun buildStackItems() = mutableListOf(
    Stack<StackItem>().apply { push(StackItem("TAG1", "GROUP")) },
    Stack<StackItem>().apply {
        push(StackItem("TAG2", "GROUP"))
        push(StackItem("TAG3", "GROUP"))
    },
    Stack<StackItem>().apply {
        push(StackItem("TAG4", "GROUP"))
        push(StackItem("TAG5", "GROUP"))
        push(StackItem("TAG6", "GROUP"))
    }
)