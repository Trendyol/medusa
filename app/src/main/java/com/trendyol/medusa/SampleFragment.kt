package com.trendyol.medusa

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType
import kotlin.random.Random

class SampleFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sample, container, false)!!
        view.findViewById<TextView>(R.id.textview).text = arguments!!.getString(KEY)

        view.findViewById<LinearLayout>(R.id.root).setBackgroundColor(Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)))

        view.findViewById<Button>(R.id.button).setOnClickListener {
            multipleStackNavigator!!.start(FragmentGenerator.generateNewFragment(), TransitionAnimationType.RIGHT_TO_LEFT)
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