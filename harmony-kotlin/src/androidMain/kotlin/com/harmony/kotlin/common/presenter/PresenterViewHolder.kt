package com.harmony.kotlin.common.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

// View holder that removes the view reference as soon as view's onDestroy is called.
actual class PresenterViewHolder<V : Any> actual constructor(view: V) : DefaultLifecycleObserver {

  private val view: WeakReference<V> = WeakReference(view)

  init {
    if (view is LifecycleOwner) {
      view.lifecycle.addObserver(this)
    }
  }

  actual fun get(): V? {
    return view.get()
  }

  override fun onDestroy(owner: LifecycleOwner) {
    super.onDestroy(owner)
    view.clear()
  }
}
