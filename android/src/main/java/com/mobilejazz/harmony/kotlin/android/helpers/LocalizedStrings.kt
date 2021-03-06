package com.mobilejazz.harmony.kotlin.android.helpers

import android.content.Context
import android.content.res.Resources
import com.mobilejazz.harmony.kotlin.android.ext.WeakRef
import com.mobilejazz.harmony.kotlin.core.helpers.Localized
import javax.inject.Inject


class LocalizedStrings
@Inject constructor(context: Context) : Localized<Int> {

  private val ctx = context.applicationContext

  override fun get(key: Int): String = ctx.getString(key)

  override fun get(key: Int, vararg formatArgs: Any) = ctx.getString(key, *formatArgs)

  override fun getPlural(key: Int, amount: Int): String = ctx.resources.getQuantityString(key, amount)


}