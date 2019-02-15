package com.trendyol.medusalib.navigator

import androidx.fragment.app.Fragment
import com.trendyol.medusalib.navigator.tag.UniqueTagCreator
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class UniqueTagCreatorTest {

    private lateinit var tagCreator: UniqueTagCreator

    @Before
    fun setUp(){
        tagCreator = UniqueTagCreator()
    }

    @Test
    fun areFragmentTagsDifferentForDifferentInstances() {
        val fragmentTag = tagCreator.create(Fragment())
        val anotherFragmentTag = tagCreator.create(Fragment())

        assertTrue("Fragment Tags Are Same.", fragmentTag != anotherFragmentTag)
    }

    @Test
    fun areFragmentTagsSameForSameInstance() {
        val fragmentFirst = Fragment()
        val fragmentSecond = fragmentFirst

        val fragmentTag = tagCreator.create(fragmentFirst)
        val anotherFragmentTag = tagCreator.create(fragmentSecond)

        assertTrue("Fragment Tags Are Different.", fragmentTag == anotherFragmentTag)
    }

    @Test
    fun isEveryTagUniqueOnThousandTag() {
        val resultSet = mutableSetOf<String>()

        for(i in 0..100000){
            val fragmentA = Fragment()
            val fragmentTagA = tagCreator.create(fragmentA)

            assertFalse("Tags Are Not Unique.", resultSet.contains(fragmentTagA))
            resultSet.add(fragmentTagA)
        }
    }
}