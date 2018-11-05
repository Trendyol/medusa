package com.trendyol.medusa


class FragmentGenerator {

    companion object {

        fun generateNewFragment(): SampleFragment {
            return SampleFragment.newInstance("fragment " + Math.random() * 50)
        }
    }
}