package com.mobilejazz.sample.core.data.model

import com.harmony.kotlin.data.validator.vastra.strategies.timestamp.TimestampValidationStrategy
import java.util.*
import java.util.concurrent.TimeUnit

data class ItemEntity(
  val id: Int,
  val by: String?,
  val title: String?,
  val text: String?,
  val type: String,
  val time: Int,
  val url: String?,
  val kids: List<Int>?,
  private val lu: Date = Date(),
  private val et: Long = TimeUnit.MINUTES.toMillis(1)
)
