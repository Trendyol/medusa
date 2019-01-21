package com.trendyol.medusalib.common.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction

internal fun FragmentTransaction.detach(fragment: Fragment?): FragmentTransaction {
    fragment?.let {
        return this.detach(it)
    }
}

internal fun FragmentTransaction.hide(fragment: Fragment?): FragmentTransaction {
    fragment?.let {
        return this.hide(it)
    }
}

internal fun FragmentTransaction.attach(fragment: Fragment?): FragmentTransaction {
    fragment?.let {
        return this.attach(it)
    }
}

internal fun FragmentTransaction.show(fragment: Fragment?): FragmentTransaction {
    fragment?.let {
        return this.show(it)
    }
}

internal fun FragmentTransaction.remove(fragment: Fragment?): FragmentTransaction {
    fragment?.let {
        return this.remove(it)
    }
}