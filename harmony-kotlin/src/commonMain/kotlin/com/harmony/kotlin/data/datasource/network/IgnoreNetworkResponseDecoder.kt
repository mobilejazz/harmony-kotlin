package com.harmony.kotlin.data.datasource.network

import io.ktor.client.statement.HttpResponse

/**
 * Helper class that ignores [HttpResponse] body and just return [Unit].
 * This class is for internal use only, for external use please use [IgnoreNetworkResponseDecoder] that has proper typing to avoid issues.
 */
internal class IgnoreNetworkResponseDecoderOfAnyType<T> : NetworkResponseDecoder<T>() {
  @Suppress("UNCHECKED_CAST")
  override suspend fun decode(httpResponse: HttpResponse): T {
    return Unit as T
  }

  override suspend fun decodeList(httpResponse: HttpResponse): List<T> {
    return emptyList()
  }
}

/**
 * Helper class that ignores [HttpResponse] body and just return [Unit].
 */
class IgnoreNetworkResponseDecoder internal constructor() : NetworkResponseDecoder<Unit>() {
  @Suppress("UNCHECKED_CAST")
  override suspend fun decode(httpResponse: HttpResponse) {
    return
  }

  override suspend fun decodeList(httpResponse: HttpResponse): List<Unit> {
    return emptyList()
  }
}
