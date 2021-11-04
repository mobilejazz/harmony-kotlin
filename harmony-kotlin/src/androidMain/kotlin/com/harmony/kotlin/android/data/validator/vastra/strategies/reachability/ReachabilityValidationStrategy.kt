package com.harmony.kotlin.android.data.validator.vastra.strategies.reachability

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategy
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyDataSource
import com.harmony.kotlin.data.validator.vastra.strategies.ValidationStrategyResult

class ReachabilityValidationStrategy(val context: Context) : ValidationStrategy {

  @SuppressLint("MissingPermission")
  override fun <T : ValidationStrategyDataSource> isValid(t: T): ValidationStrategyResult {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnected = connectivityManager.activeNetworkInfo?.isConnected == true

    return if (isConnected) ValidationStrategyResult.UNKNOWN else ValidationStrategyResult.VALID
  }
}
