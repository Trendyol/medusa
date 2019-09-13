package com.trendyol.medusalib.navigator

import com.trendyol.medusalib.navigator.data.StackItem
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class FragmentStackStateMapperTest {

    @Test
    fun `deserialization after serialization will return fragmentstackstate instance with same state as input`() {
        val fragmentStackState = buildFragmentStackState()
        val mapper = FragmentStackStateMapper()

        val serializableFragmentStackState = mapper.toBundle(fragmentStackState)
        val unserializedFragmentStackState = mapper.fromBundle(serializableFragmentStackState)

        assertEquals(fragmentStackState, unserializedFragmentStackState)
    }

    private fun buildFragmentStackState(): FragmentStackState {
        return FragmentStackState(
            mutableListOf(
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
            ),
            Stack<Int>().apply {
                push(2)
                push(1)
                push(0)
            }
        )
    }
}