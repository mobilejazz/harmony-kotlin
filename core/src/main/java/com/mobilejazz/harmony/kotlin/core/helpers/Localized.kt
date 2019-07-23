package com.mobilejazz.harmony.kotlin.core.helpers


interface Localized<T> {

  fun get(key: T): String

  fun getPlural(key: T, amount: Int): String
}