package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.datasource.network.error.NetworkConnectivityException
import com.harmony.kotlin.data.mapper.Mapper
import io.ktor.utils.io.errors.IOException

open class GenericNetworkExceptionMapper : Mapper<Exception, Exception> {
  override fun map(from: Exception): Exception =
    when (from) {
      is IOException -> NetworkConnectivityException(message = from.message, cause = from)
      else -> from
    }


}

