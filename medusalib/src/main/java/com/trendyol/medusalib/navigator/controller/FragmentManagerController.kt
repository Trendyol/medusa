package com.trendyol.medusalib.navigator.controller

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.trendyol.medusalib.common.extensions.attach
import com.trendyol.medusalib.common.extensions.detach
import com.trendyol.medusalib.common.extensions.hide
import com.trendyol.medusalib.common.extensions.show
import com.trendyol.medusalib.common.extensions.remove
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

        when (navigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> commitShow(fragmentTag)
            TransactionType.ATTACH_DETACH -> commitAttach(fragmentTag)
        }
    }

    fun disableFragment(fragmentTag: String) {
        val navigatorTransaction = getFragmentNavigatorTransaction(fragmentTag)

        when (navigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> commitHide(fragmentTag)
            TransactionType.ATTACH_DETACH -> commitDetach(fragmentTag)
        }
    }

    fun removeFragment(fragmentTag: String) {
        checkAndCreateTransaction()
        currentTransaction?.remove(getFragment(fragmentTag))
        commitNowAllowingStateLoss()
    }

    fun removeFragments(fragmentTagList: List<String>) {
        checkAndCreateTransaction()

        for (fragmentTag in fragmentTagList) {
            getFragment(fragmentTag)?.let { currentTransaction?.remove(it) }
        }
        commitNowAllowingStateLoss()
    }

    fun addFragment(fragmentData: FragmentData) {
        checkAndCreateTransaction()

        currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)
        commitNowAllowingStateLoss()
    }

    fun disableAndStartFragment(disableFragmentTag: String, vararg fragmentDataArgs: FragmentData) {
        val disabledFragment = getFragment(disableFragmentTag)

        checkAndCreateTransaction()

        val disabledFragmentNavigatorTransaction = getFragmentNavigatorTransaction(disableFragmentTag)

        when (disabledFragmentNavigatorTransaction.transactionType) {
            TransactionType.SHOW_HIDE -> currentTransaction?.hide(disabledFragment)
            TransactionType.ATTACH_DETACH -> currentTransaction?.detach(disabledFragment)
        }

        for (fragmentData in fragmentDataArgs) {
            currentTransaction?.add(containerId, fragmentData.fragment, fragmentData.fragmentTag)
        }

        commitNowAllowingStateLoss()
    }

    fun isFragmentNull(fragmentTag: String): Boolean {
        return getFragment(fragmentTag) == null
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
        checkAndCreateTransaction()

        getFragment(fragmentTag)?.let { currentTransaction?.remove(it) }
    }

    private fun commitShow(fragmentTag: String) {
        checkAndCreateTransaction()

        currentTransaction?.show(getFragment(fragmentTag))
        commitNowAllowingStateLoss()
    }

    private fun commitAttach(fragmentTag: String) {
        checkAndCreateTransaction()

        currentTransaction?.attach(getFragment(fragmentTag))
        commitNowAllowingStateLoss()
    }

    private fun commitHide(fragmentTag: String) {
        checkAndCreateTransaction()

        currentTransaction?.hide(getFragment(fragmentTag))
        commitNowAllowingStateLoss()
    }

    private fun commitDetach(fragmentTag: String) {
        checkAndCreateTransaction()

        currentTransaction?.detach(getFragment(fragmentTag))
        commitNowAllowingStateLoss()
    }

    fun commitNowAllowingStateLoss() {
        try {
            currentTransaction?.commitNowAllowingStateLoss()
            currentTransaction = null
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun checkAndCreateTransaction() {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager.beginTransaction()
        }
    }
}