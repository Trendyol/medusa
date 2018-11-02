package com.trendyol.medusa


class FragmentGenerator {

    companion object {

        fun generateNewFragment(): SampleFragment {
            val randomNumber = (Math.random() * 50).toString()
            return SampleFragment.newInstance("fragment $randomNumber")
        }
    }
}