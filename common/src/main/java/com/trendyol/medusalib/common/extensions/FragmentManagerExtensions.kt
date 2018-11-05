package com.trendyol.medusalib.common.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

fun FragmentManager.commitAdd(containerId: Int, fragment: Fragment, fragmentTag: String) {
    beginTransaction()
            .add(containerId, fragment, fragmentTag)
            .show(fragment)
            .commitAllowingStateLoss()
}

fun FragmentManager.commitRemove(fragmentTag: String) {
    val foundFragment = findFragmentByTag(fragmentTag)
    foundFragment?.let {
        beginTransaction()
                .remove(foundFragment)
                .commitAllowingStateLoss()
    }
}

fun FragmentManager.commitShow(fragmentTag: String) {
    val foundFragment = findFragmentByTag(fragmentTag)
    foundFragment?.let {
        beginTransaction()
                .attach(foundFragment)
                .commitAllowingStateLoss()
    }
}

fun FragmentManager.commitHide(fragment: Fragment) {
    beginTransaction()
            .detach(fragment)
            .commitAllowingStateLoss()
}
