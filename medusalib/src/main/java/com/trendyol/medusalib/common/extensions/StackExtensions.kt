package com.trendyol.medusalib.common.extensions

import java.util.*

internal fun <T> Stack<T>.moveToTop(data: T) {
    if (contains(data)) {
        remove(data)
        push(data)
    }
}

internal fun <T> Stack<T>.insertToBottom(data: T) {
    insertElementAt(data, 0)
}