package com.harmony.kotlin.android.application.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty



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
      is Byte -> getByte(name, default)
      is ByteArray -> getByteArray(name)
      is Char -> getChar(name, default)
      is CharArray -> getCharArray(name)
      else -> getSerializable(name)
    } as Param

    res
  }

}

class Argument<Param>(val name: String, private val default: Param) : ReadOnlyProperty<Fragment, Param> {

  override fun getValue(thisRef: Fragment, property: KProperty<*>): Param {
    return findValue(thisRef.arguments ?: Bundle())
  }

  @Suppress("UNCHECKED_CAST")
  @SuppressLint("NewApi")

  fun findValue(bundle: Bundle): Param = with(bundle) {
    val res: Any = when (default) {
      is Long -> getLong(name, default)
      is String -> getString(name, default)
      is Int -> getInt(name, default)
      is Boolean -> getBoolean(name, default)
      is Float -> getFloat(name, default)
      else -> default as Any
    }

    res as Param
  }

}

class PrefParam<Param>(private val context: Context,
                       private val name: String? = null,
                       private val preferenceRef: String? = null,
                       private val prefMode: Int = Context.MODE_PRIVATE,
                       private val default: Param,
                       private val notifyOnChange: ((Param) -> Unit)? = null) : ReadWriteProperty<Any, Param> {

  private val sharedPreferences: SharedPreferences by lazy {
    preferenceRef?.let { context.getSharedPreferences(preferenceRef, prefMode) } ?: PreferenceManager.getDefaultSharedPreferences(context)
  }

  override fun getValue(thisRef: Any, property: KProperty<*>): Param {
    return findPreference(name ?: property.name, default)
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: Param) {
    putPreference(name ?: property.name, value)
    notifyOnChange?.invoke(value)
  }

  @Suppress("UNCHECKED_CAST")
  private fun <U> findPreference(name: String, default: U): U = with(sharedPreferences) {
    val res: Any = when (default) {
      is Long -> getLong(name, default)
      is String -> getString(name, default)!!
      is Int -> getInt(name, default)
      is Boolean -> getBoolean(name, default)
      is Float -> getFloat(name, default)
      else -> throw java.lang.IllegalArgumentException("This type can be saved into Preferences")
    }

    res as U
  }

  private fun <U> putPreference(name: String, value: U) = with(sharedPreferences.edit()) {
    when (value) {
      is Long -> putLong(name, value)
      is String -> putString(name, value)
      is Int -> putInt(name, value)
      is Boolean -> putBoolean(name, value)
      is Float -> putFloat(name, value)
      else -> throw java.lang.IllegalArgumentException("This type can be saved into Preferences")
    }.apply()
  }

}

class ParcelablePrefParam()
