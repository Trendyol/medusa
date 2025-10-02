package com.trendyol.medusalib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

private const val KEY_TITLE = "title"

class TestChildFragment : Fragment() {

    var onFragmentVisibleAgain: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return TextView(requireContext()).apply {
            text = requireArguments().getString(KEY_TITLE)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden.not() && view != null) {
            onFragmentVisibleAgain?.invoke()
        }
    }

    fun requireArgumentTitle() = requireArguments().getString(KEY_TITLE)!!

    companion object {
        fun newInstance(title: String): TestChildFragment {
            return TestChildFragment().apply {
                arguments = Bundle().apply { putString(KEY_TITLE, title) }
            }
        }
    }
}
