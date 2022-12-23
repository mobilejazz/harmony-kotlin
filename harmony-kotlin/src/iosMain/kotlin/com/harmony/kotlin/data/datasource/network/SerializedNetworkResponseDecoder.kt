package com.harmony.kotlin.data.datasource.network

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.ListSerializer

actual class SerializedNetworkResponseDecoder<T> actual constructor(private val stringFormat: StringFormat, private val serializer: KSerializer<T>) :
  NetworkResponseDecoder<T>() {
  actual override suspend fun decode(httpResponse: HttpResponse): T {
    return decode(httpResponse, serializer)
  }

  actual override suspend fun decodeList(httpResponse: HttpResponse): List<T> {
    return decode(httpResponse, ListSerializer(serializer))
  }

  @Suppress("UNCHECKED_CAST")
  private suspend fun <V> decode(httpResponse: HttpResponse, serializer: KSerializer<V>): V {
    return stringFormat.decodeFromString(serializer, httpResponse.bodyAsText())
  }
}
