package com.trendyol.medusa

import androidx.fragment.app.Fragment


class FragmentGenerator {

    companion object {
        var fragmentNumber = 0

        fun generateNewFragment(): Fragment {
            fragmentNumber++
            return SampleFragment.newInstance("fragment $fragmentNumber")
        }
    }
}