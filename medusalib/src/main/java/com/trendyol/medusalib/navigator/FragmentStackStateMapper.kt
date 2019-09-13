package com.trendyol.medusalib.navigator

import android.os.Bundle
import android.os.Parcelable
import com.trendyol.medusalib.navigator.data.StackItem
import java.util.ArrayList
import java.util.Stack

class FragmentStackStateMapper {
    fun toBundle(fragmentStackState: FragmentStackState): Bundle {
        val bundle = Bundle()
        val stack = convertTagStackToArrayListOfParcelables(fragmentStackState.fragmentTagStack)
        bundle.putParcelableArrayList(MEDUSA_STACK, stack)

        val tabIndex = convertTabIndexToArrayListOfParcelables(fragmentStackState.tabIndexStack)
        bundle.putIntegerArrayList(MEDUSA_TAB_INDEX, tabIndex)

        return bundle
    }

    private fun convertTagStackToArrayListOfParcelables(fragmentTagStack: MutableList<Stack<StackItem>>): ArrayList<out Parcelable>? {
        return fragmentTagStack
            .mapTo(ArrayList()) {
                Bundle().apply {
                    val listOfStackItems = it.mapTo(ArrayList()) { it }
                    putParcelableArrayList(MEDUSA_STACK_ITEMS, listOfStackItems)
                }
            }
    }

    private fun convertTabIndexToArrayListOfParcelables(tabIndexStack: Stack<Int>): ArrayList<Int> {
        return tabIndexStack.mapTo(ArrayList()) { it }
    }

    fun fromBundle(bundle: Bundle?) : FragmentStackState {
        if (bundle == null) return FragmentStackState()

        val tabIndex = getTabIndexStack(bundle)
        val tagStack = getTagStack(bundle)

        return FragmentStackState(tagStack, tabIndex)
    }

    private fun getTabIndexStack(bundle: Bundle): Stack<Int> {
        return bundle.getIntegerArrayList(MEDUSA_TAB_INDEX)?.mapTo(Stack()) { it } ?: Stack()
    }

    private fun getTagStack(bundle: Bundle): MutableList<Stack<StackItem>> {
        return bundle.getParcelableArrayList<Bundle>(MEDUSA_STACK)
            ?.mapNotNullTo(mutableListOf()) {
                it.getParcelableArrayList<StackItem>(MEDUSA_STACK_ITEMS)?.mapTo(Stack()) { it }
            } ?: mutableListOf()
    }

    companion object {
        internal const val MEDUSA_TAB_INDEX = "tabIndex"
        internal const val MEDUSA_STACK = "stack"
        internal const val MEDUSA_STACK_ITEMS = "stackItems"
    }
}