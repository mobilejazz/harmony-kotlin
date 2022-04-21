package com.harmony.kotlin.common.presenter

import com.harmony.kotlin.common.WeakReference

// View holder that avoid memory leaks using a WeakReference
actual class PresenterViewHolder<V : Any> actual constructor(view: V) {

  private val view = WeakReference(view)

  actual fun get(): V? {
    return this.view.get()
  }
}
