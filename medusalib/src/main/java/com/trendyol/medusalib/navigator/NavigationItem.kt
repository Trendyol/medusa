package com.trendyol.medusalib.navigator

interface NavigationItem {

    /**
     * Provides fragment tag. Navigator uses this
     * marker interface to provide fragment tag
     * @return fragment tag
     */
    fun getItemTag(): String
}