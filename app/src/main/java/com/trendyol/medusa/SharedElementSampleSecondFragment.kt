package com.trendyol.medusa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SharedElementSampleSecondFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_second, container, false)!!

        return view
    }

    companion object {
        fun newInstance(): SharedElementSampleSecondFragment {
            return SharedElementSampleSecondFragment()
        }
    }
}
