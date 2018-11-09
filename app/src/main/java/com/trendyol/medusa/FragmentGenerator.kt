package com.trendyol.medusa

import android.support.v4.app.Fragment


class FragmentGenerator {

    companion object {

        fun generateNewFragment(): Fragment {
            val generatedNumber: Int = (Math.random() * 500).toInt()
            return if (generatedNumber < 250) {
                SampleFragment.newInstance("fragment $generatedNumber")
            } else {
                LifecycleAttachedSampleFragment.newInstance("fragment $generatedNumber")
            }
        }
    }
}