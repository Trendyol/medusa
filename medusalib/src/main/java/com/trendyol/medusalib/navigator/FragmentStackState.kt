package com.trendyol.medusalib.navigator

import com.trendyol.medusalib.common.extensions.insertToBottom
import com.trendyol.medusalib.common.extensions.moveToTop
import com.trendyol.medusalib.navigator.data.StackItem
import java.util.*

data class FragmentStackState constructor(
    val fragmentTagStack: MutableList<Stack<StackItem>> = mutableListOf(),
    val tabIndexStack: Stack<Int> = Stack()
) {

    fun getSelectedTabIndex() = tabIndexStack.peek()

    fun isSelectedTabEmpty() = isTabEmpty(getSelectedTabIndex())

    fun isTabEmpty(index: Int) = fragmentTagStack[index].isEmpty()

    fun notifyStackItemAdd(tabIndex: Int, stackItem: StackItem) {
        val stack = fragmentTagStack.get(tabIndex)
        stack.push(stackItem)
    }

    fun notifyStackItemAddToCurrentTab(stackItem: StackItem) {
        notifyStackItemAdd(getSelectedTabIndex(), stackItem)
    }

    fun setStackCount(size: Int) {
        for (index in 0 until size) {
            fragmentTagStack.add(Stack())
        }
    }

    fun clear() {
        fragmentTagStack.clear()
        tabIndexStack.clear()
    }

    fun switchTab(tabIndex: Int) {
        if (tabIndexStack.contains(tabIndex).not()) {
            tabIndexStack.push(tabIndex)
        } else {
            tabIndexStack.moveToTop(tabIndex)
        }
    }

    fun isSelectedTab(tabIndex: Int) = getSelectedTabIndex() == tabIndex

    fun hasOnlyRoot(tabIndex: Int) = fragmentTagStack[tabIndex].size <= 1

    fun hasSelectedTabOnlyRoot() = fragmentTagStack[getSelectedTabIndex()].size <= 1

    fun peekItemFromSelectedTab() =
        fragmentTagStack[getSelectedTabIndex()].takeIf { it.isNotEmpty() }?.peek()

    fun popItem(tabIndex: Int): StackItem {
        val item = fragmentTagStack.get(tabIndex).pop()
        if (isTabEmpty(tabIndex) && tabIndex == getSelectedTabIndex() && tabIndexStack.size > 1 ) {
            popSelectedTab()
        }
        return item
    }

    fun popItemsFromNonEmptyTabs(): List<StackItem> {
        return fragmentTagStack
            .filter { it.isNotEmpty() }
            .flatMap { stackItem -> stackItem.map { it } }
            .also { fragmentTagStack.clear() }
    }

    fun insertTabToBottom(tabIndex: Int) {
        tabIndexStack.insertToBottom(tabIndex)
    }

    fun popItems(groupName: String): List<StackItem> {
        val currentTabIndex = getSelectedTabIndex()
        val currentTabStack = fragmentTagStack.get(getSelectedTabIndex())

        val updatedTabStack = Stack<StackItem>()
        updatedTabStack.push(currentTabStack[0])

        val deletedStackItems = arrayListOf<StackItem>()

        for (i in 1 until currentTabStack.size) {
            val stackItem = currentTabStack[i]
            if (groupName == stackItem.groupName) {
                deletedStackItems.add(stackItem)
            } else {
                updatedTabStack.push(stackItem)
            }
        }

        if (deletedStackItems.isEmpty().not()) {
            fragmentTagStack[currentTabIndex] = updatedTabStack
        }
        return deletedStackItems
    }

    fun hasTabStack() = tabIndexStack.size == 1

    fun setStackState(stackState: FragmentStackState) {
        fragmentTagStack.addAll(stackState.fragmentTagStack)
        tabIndexStack.addAll(stackState.tabIndexStack)
    }

    fun peekItem(tabIndex: Int) = fragmentTagStack.get(tabIndex).peek()
    fun popItemFromSelectedTab() = popItem(getSelectedTabIndex())
    fun popSelectedTab(): Int {
        return tabIndexStack.pop()
    }
}