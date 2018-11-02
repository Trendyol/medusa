package com.trendyol.medusa


class FragmentGenerator {

    companion object {

        fun generateNewFragment(name: String): SampleFragment {
            return SampleFragment.newInstance("fragment $name")
        }
    }
}