package com.mobilejazz.sample.core.data.model

import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.timestamp.TimestampValidationStrategyDataSource
import java.util.*
import java.util.concurrent.TimeUnit

data class ItemEntity(val id: Int,
                      val by: String?,
                      val title: String?,
                      val text: String?,
                      val type: String,
                      val time: Int,
                      val url: String?,
                      val kids: List<Int>?,
                      override var lastUpdate: Date = Date()) : TimestampValidationStrategyDataSource(lastUpdate) {

  override fun expiryTime(): Long {
    return TimeUnit.MINUTES.toMillis(1)
  }
}