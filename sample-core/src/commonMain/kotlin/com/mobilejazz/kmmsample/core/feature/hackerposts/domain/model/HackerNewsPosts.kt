package com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model

import kotlinx.datetime.LocalDateTime

typealias HackerNewsPosts = List<HackerNewsPost>

data class HackerNewsPost(
  val id: Long,
  val by: String,
  val descendants: Long,
  val kids: List<Long>?,
  val score: Long,
  val time: LocalDateTime,
  val title: String,
  val type: String?,
  val url: String?
)
