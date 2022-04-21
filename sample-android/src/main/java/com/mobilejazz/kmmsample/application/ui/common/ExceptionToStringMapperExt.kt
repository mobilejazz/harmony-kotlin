package com.mobilejazz.kmmsample.application.ui.common

import android.content.Context
import com.harmony.kotlin.error.NetworkConnectivityException
import com.mobilejazz.kmmsample.application.R

fun Throwable.toLocalizedErrorMessage(context: Context) =
  context.getString(
    when (this) {
      is NetworkConnectivityException -> R.string.ls_generic_connectivity_error_message
      else -> R.string.ls_generic_error_message
    }
  )
