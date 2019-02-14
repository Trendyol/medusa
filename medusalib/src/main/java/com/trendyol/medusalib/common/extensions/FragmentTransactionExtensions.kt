package com.trendyol.medusalib.common.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

internal fun FragmentTransaction.detach(fragment: Fragment?) { fragment?.let { this.detach(it) } }

internal fun FragmentTransaction.hide(fragment: Fragment?) { fragment?.let { this.hide(it) } }

internal fun FragmentTransaction.attach(fragment: Fragment?) { fragment?.let { this.attach(it) } }

internal fun FragmentTransaction.show(fragment: Fragment?) { fragment?.let { this.show(it) } }

internal fun FragmentTransaction.remove(fragment: Fragment?) { fragment?.let { this.remove(it) } }