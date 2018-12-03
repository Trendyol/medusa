package com.trendyol.medusa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.trendyol.medusalib.navigator.Navigator

class SampleFragment : BaseFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sample, container, false)!!
        view.findViewById<TextView>(R.id.textview).text = arguments!!.getString(KEY)
        view.findViewById<Button>(R.id.button).setOnClickListener {
            multipleStackNavigator!!.start(FragmentGenerator.generateNewFragment())
        }
        return view
    }

    companion object {

        const val KEY = "KEY"

        fun newInstance(fragmentName: String): SampleFragment {
            val bundle = Bundle()
            bundle.putString(KEY, fragmentName)

            val fragment = SampleFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}