package com.harmony.kotlin.common

actual class WeakReference<T : Any> actual constructor(referred: T) {
  actual fun clear() {
    TODO("Not yet implemented")
  }

  actual fun get(): T? {
    TODO("Not yet implemented")
  }
}