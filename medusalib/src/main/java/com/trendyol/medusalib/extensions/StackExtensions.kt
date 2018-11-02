package com.trendyol.medusalib.extensions

import java.util.*

fun <T> Stack<T>.moveToTop(data: T) {
    if (contains(data)) {
        remove(data)
        push(data)
    }
}