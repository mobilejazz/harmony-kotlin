package com.harmony.kotlin.common

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


object Delegates {

    fun <T : Any> weakReference(value: T):
            ReadOnlyProperty<Any?, T?> = object : ReadOnlyProperty<Any?, T?> {
        private val reference = WeakReference(value)
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? = reference.get()
    }
}