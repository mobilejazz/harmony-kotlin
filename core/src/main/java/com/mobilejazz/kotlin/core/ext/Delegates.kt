package com.mobilejazz.kotlin.core.ext

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty


class WeakRef<T>(element: T) {
  private val weak = WeakReference(element)
  operator fun getValue(thisRef: Any, property: KProperty<*>): T? = weak.get()
}

