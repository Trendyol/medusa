package com.trendyol.medusa

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.trendyol.medusalib.navigator.MultipleStackNavigator

open class BaseFragment : Fragment() {

    var multipleStackNavigator: MultipleStackNavigator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initStackNavigator(context)
    }

    private fun initStackNavigator(context: Context?) {
        if (context is MainActivity && multipleStackNavigator == null) {
            multipleStackNavigator = context.multipleStackNavigator
        } else if (context is MainActivity2 && multipleStackNavigator == null) {
            multipleStackNavigator = context.multipleStackNavigator
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initStackNavigator(context)
    }
}