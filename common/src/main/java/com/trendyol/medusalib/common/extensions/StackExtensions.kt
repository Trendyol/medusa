package com.trendyol.medusalib.common.extensions

import java.util.*

fun <T> Stack<T>.moveToTop(data: T) {
    if (contains(data)) {
        remove(data)
        push(data)
    }
}

fun <T> Stack<T>.insertToBottom(data: T) {
    insertElementAt(data, 0)
}