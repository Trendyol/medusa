package com.trendyol.medusalib.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

fun FragmentManager.commitAdd(containerId: Int, fragment: Fragment, fragmentTag: String): Fragment? {
    val foundFragment = findFragmentByTag(fragmentTag)
    if (foundFragment == null) {
        beginTransaction()
                .add(containerId, fragment, fragmentTag)
                .commit()
    }
    return fragment
}


fun FragmentManager.commitRemove(fragmentTag: String) {
    val foundFragment = findFragmentByTag(fragmentTag)
    foundFragment?.let {
        beginTransaction()
                .remove(foundFragment)
                .commit()
    }
}


fun FragmentManager.commitShow(fragmentTag: String): Fragment? {
    val foundFragment = findFragmentByTag(fragmentTag)
    foundFragment?.let {
        beginTransaction()
                .show(foundFragment)
                .commit()
    }
    return foundFragment
}

fun FragmentManager.commitHide(fragmentTag: String) {
    val foundFragment = findFragmentByTag(fragmentTag)
    foundFragment?.let {
        beginTransaction()
                .hide(foundFragment)
                .commit()
    }
}

fun FragmentManager.commitHide(fragment: Fragment) {
    beginTransaction()
            .hide(fragment)
            .commit()
}
