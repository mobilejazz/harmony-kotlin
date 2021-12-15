package com.mobilejazz.kmmsample.core.feature.hackerposts.data.entity

import com.harmony.kotlin.common.date.Millis
import com.harmony.kotlin.common.date.Seconds
import com.harmony.kotlin.data.validator.vastra.strategies.timestamp.TimestampValidationEntity
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
internal data class HackerNewsPostEntity(
  val by: String,
  val descendants: Long,
  val id: Long,
  val kids: List<Long>? = null,
  val score: Long,
  val time: Long,
  val title: String? = null,
  val type: String? = null,
  val url: String? = null,
  override val lastUpdatedAt: Millis = Clock.System.now().toEpochMilliseconds(),
) : TimestampValidationEntity {
  override val expireIn: Seconds = 60L
}
