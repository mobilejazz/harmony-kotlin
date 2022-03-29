package com.harmony.kotlin.common.presenter

/**
 * Holds a view for a Presenter. Intended to avoid memory leaks and handle view lifecycle (when applies)
 */
expect class PresenterViewHolder<V : Any>(view: V) {
  fun get(): V?
}
