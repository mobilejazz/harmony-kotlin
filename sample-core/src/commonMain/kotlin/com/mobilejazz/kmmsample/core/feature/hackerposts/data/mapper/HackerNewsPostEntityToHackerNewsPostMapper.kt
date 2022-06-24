package com.mobilejazz.kmmsample.core.feature.hackerposts.data.mapper

import com.harmony.kotlin.data.mapper.Mapper
import com.mobilejazz.kmmsample.core.feature.hackerposts.data.entity.HackerNewsPostEntity
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class HackerNewsPostEntityToHackerNewsPostMapper :
  Mapper<HackerNewsPostEntity, HackerNewsPost> {
  override fun map(from: HackerNewsPostEntity): HackerNewsPost {
    return HackerNewsPost(
      from.id,
      from.by,
      from.descendants,
      from.kids,
      from.score,
      Instant.fromEpochSeconds(from.time)
        .toLocalDateTime(TimeZone.currentSystemDefault()),
      from.title ?: "",
      from.type,
      from.url
    )
  }
}
