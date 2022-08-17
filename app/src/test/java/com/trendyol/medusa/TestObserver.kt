package com.trendyol.medusa

class TestObserver<T> constructor() : (T) -> Unit {
    val values = mutableListOf<T>()

    override fun invoke(p1: T) {
        values.add(p1)
    }
}