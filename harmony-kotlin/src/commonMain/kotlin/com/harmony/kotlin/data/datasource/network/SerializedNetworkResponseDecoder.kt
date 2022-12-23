package com.harmony.kotlin.data.datasource.network

import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat

/**
 * Helper class that decodes [HttpResponse] body using [StringFormat] and [KSerializer].
 * @param stringFormat [StringFormat] - the format from which the data is decoded. Json, XML, etc.
 * @param serializer [KSerializer] - serializer that is used to decode the data.
 */
expect class SerializedNetworkResponseDecoder<T> (stringFormat: StringFormat, serializer: KSerializer<T>) : NetworkResponseDecoder<T> {

  override suspend fun decode(httpResponse: HttpResponse): T

  override suspend fun decodeList(httpResponse: HttpResponse): List<T>
}
