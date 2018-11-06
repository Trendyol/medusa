package com.trendyol.medusalib.navigator

import android.support.v4.app.Fragment


class UniqueTagCreator : TagCreator {

    override fun create(fragment: Fragment): String {
        val tagBuilder = StringBuilder()
        tagBuilder.append(fragment.javaClass.name)
                .append(TAG_DIVIDER)
                .append(fragment.hashCode())

        return tagBuilder.toString()
    }

    companion object {
        private const val TAG_DIVIDER = "-_-"
    }
}