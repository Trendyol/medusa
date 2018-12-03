package com.trendyol.medusa

import android.support.v4.app.Fragment


class FragmentGenerator {

    companion object {
        var fragmentNumber = 0

        fun generateNewFragment(): Fragment {
            fragmentNumber++
            return SampleFragment.newInstance("fragment $fragmentNumber")
        }
    }
}