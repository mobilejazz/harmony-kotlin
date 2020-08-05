package com.mobilejazz.harmony.kotlin.core.helpers


interface Localized<T> {

  fun get(key: T): String

  fun get(key: T, vararg formatArgs: Any): String

  fun getPlural(key: T, amount: Int): String
}