package com.mobilejazz.kotlin.core.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


class WeakRef<T>(element: T) {
  private val weak = WeakReference(element)
  operator fun getValue(thisRef: Any, property: KProperty<*>): T? = weak.get()
}

class BundleParam<out Param>(val name: String, private val default: Param? = null) : ReadOnlyProperty<Activity, Param> {

  override fun getValue(thisRef: Activity, property: KProperty<*>): Param {
    return findValue(thisRef.intent.extras ?: Bundle())
  }

  @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
  @SuppressLint("NewApi")
  private fun findValue(bundle: Bundle): Param = with(bundle) {
    val res: Param = when (default) {
      is Long -> getLong(name, default)
      is String -> getString(name, default)
      is Int -> getInt(name, default)
      is Boolean -> getBoolean(name, default)
      is Float -> getFloat(name, default)
      else -> getSerializable(name)
    } as Param

    res
  }

}

