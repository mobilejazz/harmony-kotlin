package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.mapper.IdentityMapper
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.error.QueryNotSupportedException
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class GetNetworkDataSource<T>(
  private val url: String,
  private val httpClient: HttpClient,
  private val networkResponseDecoder: NetworkResponseDecoder<T>,
  private val globalHeaders: List<Pair<String, String>> = emptyList(),
  private val exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()
) : GetDataSource<T> {

  @Deprecated("Use the constructor with NetworkResponseDecoder instead")
  constructor(
    url: String,
    httpClient: HttpClient,
    serializer: KSerializer<T>,
    json: Json,
    globalHeaders: List<Pair<String, String>> = emptyList(),
    exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()
  ) : this(
    url, httpClient,
    if (serializer.descriptor == Unit.serializer().descriptor) {
      IgnoreNetworkResponseDecoderOfAnyType<T>()
    } else {
      SerializedNetworkResponseDecoder<T>(json, serializer)
    },
    globalHeaders, exceptionMapper
  )

  /**
   * GET request returning an object
   */
  override suspend fun get(query: Query): T {
    val response: HttpResponse = tryOrThrow(exceptionMapper) {
      validateQuery(query).executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders)
    }
    return networkResponseDecoder.decode(response)
  }

  private fun validateQuery(query: Query): NetworkQuery {
    if (query !is NetworkQuery) {
      throw QueryNotSupportedException("GetNetworkDataSource only supports NetworkQuery")
    }

    if (query.method !is NetworkQuery.Method.Get) {
      throw QueryNotSupportedException("NetworkQuery method is ${query.method} instead of GET")
    }

    return query
  }
}

class PutNetworkDataSource<T>(
  private val url: String,
  private val httpClient: HttpClient,
  private val networkResponseDecoder: NetworkResponseDecoder<T>,
  private val globalHeaders: List<Pair<String, String>> = emptyList(),
  private val exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()

) : PutDataSource<T> {

  @Deprecated("Use the constructor with NetworkResponseDecoder instead")
  constructor(
    url: String,
    httpClient: HttpClient,
    serializer: KSerializer<T>,
    json: Json,
    globalHeaders: List<Pair<String, String>> = emptyList(),
    exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()
  ) : this(
    url, httpClient,
    if (serializer.descriptor == Unit.serializer().descriptor) {
      IgnoreNetworkResponseDecoderOfAnyType<T>()
    } else {
      SerializedNetworkResponseDecoder<T>(json, serializer)
    },
    globalHeaders, exceptionMapper
  )

  /**
   * POST or PUT request returning an object
   * @throws IllegalArgumentException if both value and content-type of the query method are defined
   */
  override suspend fun put(query: Query, value: T?): T {
    val response: HttpResponse = tryOrThrow(exceptionMapper) {
      validateQuery(query)
        .sanitizeContentType(value)
        .executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders)
    }

    return networkResponseDecoder.decode(response)
  }

  /**
   * Perform checks on content-type and update the NetworkQuery with the provided value if needed
   * @return The NetworkQuery (modified or not)
   * @throws IllegalArgumentException if both value and content-type of the query method are defined
   */
  private fun <V> NetworkQuery.sanitizeContentType(value: V?): NetworkQuery {
    val contentType = this.method.contentType
    // Checking that the arguments are consistent (if both contentType and value are provided the caller must be notified about the issue)
    if (contentType != null && value != null) {
      throw IllegalArgumentException(
        "Conflicting arguments to be used as request body:\n" +
          "query.method.contentType=${contentType}\n" +
          "value=${value}\n" +
          "You must only provide one of them"
      )
    }
    // Updating query if value is passed as separated argument from the query
    if (contentType == null && value != null) {
      this.method.contentType = NetworkQuery.ContentType.Json(entity = value)
    }

    return this
  }

  private fun validateQuery(query: Query): NetworkQuery {
    if (query !is NetworkQuery) {
      throw QueryNotSupportedException("GetNetworkDataSource only supports NetworkQuery")
    }

    if (query.method !is NetworkQuery.Method.Post && query.method !is NetworkQuery.Method.Put) {
      throw QueryNotSupportedException("NetworkQuery method is ${query.method} instead of POST or PUT")
    }

    return query
  }
}

class DeleteNetworkDataSource(
  private val url: String,
  private val httpClient: HttpClient,
  private val globalHeaders: List<Pair<String, String>> = emptyList(),
  private val exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()
) : DeleteDataSource {

  /**
   * DELETE request
   */
  override suspend fun delete(query: Query) {
    tryOrThrow(exceptionMapper) {
      validateQuery(query).executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders)
    }
  }

  private fun validateQuery(query: Query): NetworkQuery {
    if (query !is NetworkQuery) {
      throw QueryNotSupportedException("DeleteNetworkDataSource only supports NetworkQuery")
    }

    if (query.method !is NetworkQuery.Method.Delete) {
      throw QueryNotSupportedException("NetworkQuery method is ${query.method} instead of DELETE")
    }

    return query
  }
}

private suspend fun <T> tryOrThrow(exceptionMapper: Mapper<Exception, Exception>, block: suspend () -> T): T {
  return try {
    block()
  } catch (e: Exception) {
    throw exceptionMapper.map(e)
  }
}
