package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment
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
    fun areFragmentTagsForDifferentInstance() {
        val fragmentTag = tagCreator.create(Fragment())
        val anotherFragmentTag = tagCreator.create(Fragment())

        assertTrue("Fragment Tagleri Aynı.", fragmentTag != anotherFragmentTag)
    }

    @Test
    fun areFragmentTagsSameForSameInstance() {
        val fragmentFirst = Fragment()
        val fragmentSecond = fragmentFirst

        val fragmentTag = tagCreator.create(fragmentFirst)
        val anotherFragmentTag = tagCreator.create(fragmentSecond)

        assertTrue("Fragment Tagleri Aynı Değil.", fragmentTag == anotherFragmentTag)
    }

    @Test
    fun isEveryTagUniqueOnThousandTag() {
        val resultSet = mutableSetOf<String>()

        for(i in 0..100000){
            val fragmentA = Fragment()
            val fragmentTagA = tagCreator.create(fragmentA)

            assertFalse("Bir Tag'den birden fazla kere var.", resultSet.contains(fragmentTagA))
            resultSet.add(fragmentTagA)
        }
    }
}