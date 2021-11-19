package com.mobilejazz.kmmsample.core.feature.hackerposts.data.mapper

import com.harmony.kotlin.data.mapper.Mapper
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPostsIds

internal class ListIntToHackerNewsPostsIdsMapper : Mapper<List<Int>, HackerNewsPostsIds> {
  override fun map(from: List<Int>): HackerNewsPostsIds = HackerNewsPostsIds(from.toList())
}
