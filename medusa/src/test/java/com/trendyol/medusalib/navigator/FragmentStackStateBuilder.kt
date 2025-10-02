package com.trendyol.medusalib.navigator

import com.trendyol.medusalib.navigator.data.StackItem
import java.util.Stack

fun buildFragmentStackState(
    tabStack: Stack<Int> = buildTabStack(), vararg fragmentStacks: Stack<StackItem> =
        arrayOf(
            buildStack(
                StackItem("TAG1", "GROUP")
            ),
            buildStack(
                StackItem("TAG2", "GROUP"),
                StackItem("TAG3", "GROUP")
            ),
            buildStack(
                StackItem("TAG4", "GROUP"),
                StackItem("TAG5", "GROUP"),
                StackItem("TAG6", "GROUP")
            )
        )
): FragmentStackState {
    return FragmentStackState(
        fragmentStacks.toMutableList(),
        tabStack
    )
}

fun buildStack(vararg stackItems: StackItem): Stack<StackItem> {
    return stackItems
        .fold(Stack()) { stack, stackItem ->
            stack.apply { stack.push(stackItem) }
        }
}

fun buildStackItem(fragmentTag: String = "TAG", groupName: String = "GROUP"): StackItem {
    return StackItem(fragmentTag, groupName)
}

fun buildTabStack(vararg tabIndex: Int = arrayOf(2, 1, 0).toIntArray()): Stack<Int> {
    return tabIndex.mapTo(Stack<Int>()) { it }
}
