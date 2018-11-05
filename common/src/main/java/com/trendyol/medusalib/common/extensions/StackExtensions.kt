package com.trendyol.medusalib.common.extensions

import java.util.*

fun <T> Stack<T>.moveToTop(data: T) {
    if (contains(data)) {
        remove(data)
        push(data)
    }
}

fun <T> List<Stack<T>>.has(data: T): Boolean = any { it.contains(data) }