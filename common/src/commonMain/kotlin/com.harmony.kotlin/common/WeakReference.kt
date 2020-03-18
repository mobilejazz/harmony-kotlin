package com.harmony.kotlin.common

expect class WeakReference<T : Any>(referred: T) {
    fun clear()
    fun get(): T?
}