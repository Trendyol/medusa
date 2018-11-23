package com.trendyol.medusalib.common.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction


inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) = beginTransaction().func().commitAllowingStateLoss()

fun FragmentManager.commitAdd(containerId: Int, fragment: Fragment, fragmentTag: String) =
    inTransaction {
        add(containerId, fragment, fragmentTag)
    }

fun FragmentManager.commitRemove(fragmentTag: String) =
    findFragmentByTag(fragmentTag)?.let {
        inTransaction {
            remove(it)
        }
    }

fun FragmentManager.commitAttach(fragmentTag: String) =
    findFragmentByTag(fragmentTag)?.let {
        inTransaction {
            attach(it)
        }
    }

fun FragmentManager.commitDetach(fragmentTag: String) =
    findFragmentByTag(fragmentTag)?.let {
        inTransaction {
            detach(it)
        }
    }

fun FragmentManager.commitHide(fragmentTag: String) {
    findFragmentByTag(fragmentTag)?.let {
        inTransaction {
            hide(it)
        }
    }
}

fun FragmentManager.commitShow(fragmentTag: String) {
    findFragmentByTag(fragmentTag)?.let {
        inTransaction {
            show(it)
        }
    }
}
