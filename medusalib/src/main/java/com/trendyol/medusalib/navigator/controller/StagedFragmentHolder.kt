package com.trendyol.medusalib.navigator.controller

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class StagedFragmentHolder constructor(
    private val fragmentsByTags: MutableMap<String, Fragment>
) {

    fun stageFragmentForCommit(tag: String, fragment: Fragment) {
        fragmentsByTags.put(tag, fragment)
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onDestroy(owner: LifecycleOwner) {
                fragmentsByTags.remove(tag)
                fragment.lifecycle.removeObserver(this)
                super.onDestroy(owner)
            }
        })
    }

    fun getStagedFragment(tag: String): Fragment? {
        return fragmentsByTags.get(tag)
    }
}