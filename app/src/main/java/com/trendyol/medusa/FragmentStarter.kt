package com.trendyol.medusa

import android.support.v4.app.FragmentActivity

class FragmentStarter {

    companion object {

        private const val CONTAINER = R.id.fragmentContainer

        fun start(activity: FragmentActivity, fragment: SampleFragment) {
            activity.supportFragmentManager.beginTransaction()
                    .add(CONTAINER, fragment, fragment.getFragmentTag())
                    .addToBackStack(null)
                    .commit()
        }
    }
}