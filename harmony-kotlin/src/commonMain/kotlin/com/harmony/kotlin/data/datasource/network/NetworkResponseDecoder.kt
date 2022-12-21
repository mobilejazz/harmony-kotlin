package com.harmony.kotlin.data.datasource.network

import io.ktor.client.statement.HttpResponse

/**
 * Helper class to decode a [HttpResponse] into an object.
 */
abstract class NetworkResponseDecoder<T> {
  /**
   * Decodes a [HttpResponse] into a [T] object.
   */
  internal abstract suspend fun decode(httpResponse: HttpResponse): T

  /**
   * Decodes a [HttpResponse] into a [List] of [T] objects.
   */
  internal abstract suspend fun decodeList(httpResponse: HttpResponse): List<T>
}
