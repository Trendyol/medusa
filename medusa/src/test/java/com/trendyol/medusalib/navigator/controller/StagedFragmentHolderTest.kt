package com.trendyol.medusalib.navigator.controller

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import com.google.common.truth.Truth
import com.trendyol.medusalib.TestChildFragment
import com.trendyol.medusalib.TestParentFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StagedFragmentHolderTest {

    @Test
    fun `given a fragment for a tag doesnt exist, when getStagedFragment is called, then it must return null`() {
        val sut = StagedFragmentHolder(mutableMapOf())

        val actualFragment = sut.getStagedFragment("missing-fragment")

        Truth.assertThat(actualFragment).isNull()
    }

    @Test
    fun `given a fragment for a tag exists, when getStagedFragment is called, then it must return that fragment`() {
        val sut = StagedFragmentHolder(mutableMapOf())


        launchFragmentInContainer { TestParentFragment() }.withFragment {
            val fragmentToBeStaged = TestChildFragment.newInstance("title")
            childFragmentManager
                .beginTransaction()
                .add(fragmentToBeStaged, "staged-child")
                .commitNow()

            sut.stageFragmentForCommit("staged-child", fragmentToBeStaged)

            val actualFragment = sut.getStagedFragment("staged-child")

            Truth.assertThat(actualFragment).isEqualTo(fragmentToBeStaged)
        }
    }

    @Test
    fun `given a fragment for a tag exists, when it is removed, then it must return null for that tag`() {
        val sut = StagedFragmentHolder(mutableMapOf())


        launchFragmentInContainer { TestParentFragment() }.withFragment {
            val fragmentToBeStaged = TestChildFragment.newInstance("title")
            childFragmentManager
                .beginTransaction()
                .add(fragmentToBeStaged, "staged-child")
                .commitNow()
            sut.stageFragmentForCommit("staged-child", fragmentToBeStaged)

            childFragmentManager
                .beginTransaction()
                .remove(fragmentToBeStaged)
                .commitNow()


            val fragment = sut.getStagedFragment("staged-child")

            Truth.assertThat(fragment).isNull()
        }
    }

}