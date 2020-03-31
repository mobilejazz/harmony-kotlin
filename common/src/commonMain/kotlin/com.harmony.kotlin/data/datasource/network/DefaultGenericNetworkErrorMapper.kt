package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.NetworkErrorException
import com.harmony.kotlin.data.mapper.Mapper
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode

object DefaultGenericNetworkErrorMapper : Mapper<ClientRequestException, Exception> {

  override fun map(from: ClientRequestException): Exception {
    when (from.response.status) {
      HttpStatusCode.NotFound -> {
        throw DataNotFoundException(from.message, from.cause)
      }
      else -> throw NetworkErrorException(from.response.status.value, from.message, from.cause)
    }
  }
}