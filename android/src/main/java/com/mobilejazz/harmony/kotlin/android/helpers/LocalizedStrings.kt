package com.mobilejazz.harmony.kotlin.android.helpers

import android.content.Context
import com.harmony.kotlin.application.helper.Localized
import javax.inject.Inject


class LocalizedStrings
@Inject constructor(context: Context) : Localized<Int> {

  private val ctx = context.applicationContext

  override fun get(key: Int): String = ctx.getString(key)

  override fun getPlural(key: Int, amount: Int): String = ctx.resources.getQuantityString(key, amount)


}