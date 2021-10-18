package com.harmony.kotlin.application.helper

interface Localized<T> {

  fun get(key: T): String

  fun getPlural(key: T, amount: Int): String
}
