package com.trendyol.medusalib.navigator.controller

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.trendyol.medusalib.common.extensions.commitAdd
import com.trendyol.medusalib.common.extensions.commitAttach
import com.trendyol.medusalib.common.extensions.commitDetach
import com.trendyol.medusalib.common.extensions.commitHide
import com.trendyol.medusalib.common.extensions.commitRemove
import com.trendyol.medusalib.common.extensions.commitShow
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.data.FragmentData
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import com.trendyol.medusalib.navigator.transaction.TransactionType

class FragmentManagerController(private val fragmentManager: FragmentManager,
                                private val containerId: Int,
                                private val navigatorTransaction: NavigatorTransaction) {

    private var currentTransaction: FragmentTransaction? = null

    fun enableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        with(fragmentManager) {
            when (navigatorTransaction.transactionType) {
                TransactionType.SHOW_HIDE -> commitShow(fragmentTag)
                TransactionType.ATTACH_DETACH -> commitAttach(fragmentTag)
            }
        }
        executePendings()
    }

    fun disableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        with(fragmentManager) {
            when (navigatorTransaction.transactionType) {
                TransactionType.SHOW_HIDE -> commitHide(fragmentTag)
                TransactionType.ATTACH_DETACH -> commitDetach(fragmentTag)
            }
        }
        executePendings()
    }

    fun removeFragment(fragmentTag: String) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }
        currentTransaction?.remove(fragmentManager.findFragmentByTag(fragmentTag))?.commitNowAllowingStateLoss()
    }

    fun removeFragments(fragmentTagList: List<String>) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }
        for (fragmentTag in fragmentTagList) {
            val fragment = fragmentManager.findFragmentByTag(fragmentTag)
            fragment?.let { currentTransaction?.remove(it) }
        }

        currentTransaction?.commitNowAllowingStateLoss()
        executePendings()
    }

    fun addFragment(fragmentData: FragmentData) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }
        currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)?.commitNowAllowingStateLoss()
    }

    fun disableAndStartFragment(disableFragmentTag: String, vararg fragmentDataArgs: FragmentData) {
        val disabledFragment = fragmentManager.findFragmentByTag(disableFragmentTag)

        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }

        val disabledFragmentNavigatorTransaction = getFragmentNavigatorTransaction(disableFragmentTag)

        when (disabledFragmentNavigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> currentTransaction?.hide(disabledFragment)
            TransactionType.ATTACH_DETACH -> currentTransaction?.detach(disabledFragment)
        }

        for (fragmentData in fragmentDataArgs) {
            currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)
        }

        currentTransaction?.commitNowAllowingStateLoss()
        executePendings()
    }

    fun isFragmentNull(fragmentTag: String): Boolean {
        return fragmentManager.findFragmentByTag(fragmentTag) == null
    }

    fun executePendings() {
        //fragmentManager.executePendingTransactions()
    }

    fun getFragment(fragmentTag: String): Fragment? {
        return fragmentManager.findFragmentByTag(fragmentTag)
    }

    private fun getFragmentNavigatorTransaction(fragmentTag: String): NavigatorTransaction {
        var navigatorTransaction = navigatorTransaction

        getFragment(fragmentTag)?.let {
            if (it is Navigator.OnNavigatorTransactionListener) {
                navigatorTransaction = it.getNavigatorTransaction()
            }
        }

        return navigatorTransaction
    }

    fun findFragmentByTagAndRemove(fragmentTag: String) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }

        val fragment = fragmentManager.findFragmentByTag(fragmentTag)
        fragment?.let { currentTransaction?.remove(it) }
    }

    fun commitNowAllowingStateLoss() = currentTransaction?.commitNowAllowingStateLoss()
}