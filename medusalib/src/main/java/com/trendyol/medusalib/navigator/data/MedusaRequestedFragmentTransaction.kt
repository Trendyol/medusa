package com.trendyol.medusalib.navigator.data

import androidx.fragment.app.Fragment

data class MedusaRequestedFragmentTransaction(
    val currentFragment: Fragment,
    val nextFragment: Fragment,
)
