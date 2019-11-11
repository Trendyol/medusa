package com.trendyol.medusalib.navigator.data

import androidx.fragment.app.Fragment
import com.trendyol.medusalib.navigator.transitionanimation.TransitionAnimationType

data class FragmentData(val fragment: Fragment, val fragmentTag: String, val transitionAnimation: TransitionAnimationType? = null)