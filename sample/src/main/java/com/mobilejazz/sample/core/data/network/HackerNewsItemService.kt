package com.mobilejazz.sample.core.data.network

import com.mobilejazz.sample.core.data.model.ItemEntity
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsItemService {

  @GET("askstories.json")
  suspend fun askStories(): List<Int>

  @GET("item/{id}.json")
  suspend fun newItem(@Path("id") id: Int): ItemEntity
}
