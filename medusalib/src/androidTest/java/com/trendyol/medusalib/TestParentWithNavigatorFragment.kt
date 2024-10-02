package com.trendyol.medusalib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.trendyol.medusalib.navigator.MultipleStackNavigator
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.NavigatorConfiguration
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import com.trendyol.medusalib.navigator.transaction.TransactionType

class TestParentWithNavigatorFragment : Fragment() {

    lateinit var navigator: Navigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        navigator = createNavigator(
            { TestChildFragment.newInstance("Root1") },
            { TestChildFragment.newInstance("Root2") },
            { TestChildFragment.newInstance("Root3") }
        )
        navigator.initialize(savedInstanceState)
        return FrameLayout(requireContext()).apply { id = CONTAINER_ID }
    }

    fun createNavigator(
        vararg rootFragments: () -> TestChildFragment
    ): MultipleStackNavigator {
        return MultipleStackNavigator(
            fragmentManager = this.childFragmentManager,
            containerId = TestParentFragment.CONTAINER_ID,
            rootFragmentProvider = rootFragments.toList(),
            navigatorConfiguration = NavigatorConfiguration(
                defaultNavigatorTransaction = NavigatorTransaction(TransactionType.SHOW_HIDE)
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigator.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val CONTAINER_ID = 1_000
    }
}