package com.mobilejazz.kotlin.core.sample.data.network.items

import com.mobilejazz.kotlin.core.sample.data.network.model.ItemsNetwork
import com.mobilejazz.kotlin.core.threading.extensions.Future
import retrofit2.http.GET

interface ItemApiService {

  @GET("items")
  fun items(): Future<ItemsNetwork>

}