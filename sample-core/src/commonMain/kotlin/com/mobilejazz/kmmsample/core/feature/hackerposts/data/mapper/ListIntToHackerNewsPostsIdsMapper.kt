package com.mobilejazz.kmmsample.core.feature.hackerposts.data.mapper

import com.harmony.kotlin.data.mapper.Mapper
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPostsIds

internal class ListIntToHackerNewsPostsIdsMapper : Mapper<List<Long>, HackerNewsPostsIds> {
  override fun map(from: List<Long>): HackerNewsPostsIds = HackerNewsPostsIds(from.toList())
}
