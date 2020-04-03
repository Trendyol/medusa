package com.trendyol.medusalib.navigator

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

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

}