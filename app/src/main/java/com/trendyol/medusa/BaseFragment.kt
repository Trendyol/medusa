package com.trendyol.medusa

import android.content.Context
import android.support.v4.app.Fragment
import com.trendyol.medusalib.navigator.Navigator

open class BaseFragment : Fragment() {

    lateinit var navigator: Navigator

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity) {
            navigator = context.navigator
        }
    }
}