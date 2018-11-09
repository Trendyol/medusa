package com.trendyol.medusalib.common.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
        beginTransaction().func().commitAllowingStateLoss()

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

fun FragmentManager.commitDetach(fragment: Fragment) =
        inTransaction {
            detach(fragment)
        }

