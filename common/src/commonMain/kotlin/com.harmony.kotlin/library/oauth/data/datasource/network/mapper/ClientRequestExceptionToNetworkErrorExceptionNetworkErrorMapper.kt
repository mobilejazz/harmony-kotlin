package com.harmony.kotlin.library.oauth.data.datasource.network.mapper

import com.harmony.kotlin.data.error.NetworkErrorException
import com.harmony.kotlin.data.mapper.Mapper
import io.ktor.client.features.ClientRequestException

object ClientRequestExceptionToNetworkErrorExceptionMapper : Mapper<ClientRequestException, NetworkErrorException> {

  override fun map(from: ClientRequestException): NetworkErrorException {
    return NetworkErrorException(from.response.status.value, from.message, from.cause)
  }
}