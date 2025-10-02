package com.trendyol.medusalib.navigator.data

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

data class MedusaRequestedFragmentTransaction(
    val currentFragment: WeakReference<Fragment>,
    val nextFragment: WeakReference<Fragment>,
)
