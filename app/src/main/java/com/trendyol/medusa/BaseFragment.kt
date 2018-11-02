package com.trendyol.medusa

import android.content.Context
import android.support.v4.app.Fragment
import com.trendyol.medusalib.navigator.MultipleStackNavigator

open class BaseFragment : Fragment() {

    var multipleStackNavigator: MultipleStackNavigator? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity) {
            multipleStackNavigator = context.multipleStackNavigator
        }
    }
}