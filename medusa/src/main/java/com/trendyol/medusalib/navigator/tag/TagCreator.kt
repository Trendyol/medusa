package com.trendyol.medusalib.navigator.tag

import androidx.fragment.app.Fragment


interface TagCreator {
    /**
     * Creates tag for fragment stack
     * This tag is used to find fragment from
     * fragment manager.
     *
     * @param fragment will be added to stack.
     * @return created tag name
     */
    fun create(fragment: Fragment): String
}