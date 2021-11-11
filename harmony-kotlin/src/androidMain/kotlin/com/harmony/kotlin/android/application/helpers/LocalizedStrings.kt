package com.harmony.kotlin.android.application.helpers

import android.content.Context
import com.harmony.kotlin.application.helper.Localized

class LocalizedStrings(context: Context) : Localized<Int> {

  private val ctx = context.applicationContext

  override fun get(key: Int): String = ctx.getString(key)

  override fun getPlural(key: Int, amount: Int): String = ctx.resources.getQuantityString(key, amount)
}
