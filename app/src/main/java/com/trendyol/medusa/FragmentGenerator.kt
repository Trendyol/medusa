package com.trendyol.medusa

import androidx.fragment.app.Fragment


class FragmentGenerator {

    companion object {
        var fragmentNumber = 0

        @JvmStatic
        fun generateNewFragment(): Fragment {
            fragmentNumber++
            return SampleFragment.newInstance("fragment $fragmentNumber")
        }

        @JvmStatic
        fun generateBrandNewFragments(): Fragment {
            fragmentNumber++
            return SampleFragment.newInstance("brand new fragment $fragmentNumber")
        }
    }
}