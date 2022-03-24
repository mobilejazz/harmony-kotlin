package com.mobilejazz.kmmsample.core.feature.hackerposts.domain

import com.harmony.kotlin.data.query.KeyQuery

sealed class HackerNewsQuery(key: String) : KeyQuery(key) {
  object GetAll : HackerNewsQuery("get-All")
  class GetPost(val postId: Int) : HackerNewsQuery("post-$postId")
}
