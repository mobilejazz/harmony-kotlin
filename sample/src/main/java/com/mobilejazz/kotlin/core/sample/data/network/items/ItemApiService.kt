package com.mobilejazz.kotlin.core.sample.data.network.items

import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.threading.Future
import retrofit2.http.GET

interface ItemApiService {

  @GET("items")
  fun items(): Future<Item>

}