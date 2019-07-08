package com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.reachability

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategy
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyDataSource
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.ValidationStrategyResult

class ReachabilityValidationStrategy(val context: Context) : ValidationStrategy {

  @SuppressLint("MissingPermission")
  override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnected = connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected

    return if (isConnected) ValidationStrategyResult.UNKNOWN else ValidationStrategyResult.VALID
  }
}