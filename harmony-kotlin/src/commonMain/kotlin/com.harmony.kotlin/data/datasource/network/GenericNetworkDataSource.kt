package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.error.QueryNotSupportedException
import com.harmony.kotlin.data.mapper.IdentityMapper
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.query.Query
import io.ktor.client.HttpClient
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class GetNetworkDataSource<T>(
  private val url: String,
  private val httpClient: HttpClient,
  private val serializer: KSerializer<T>,
  private val json: Json,
  private val globalHeaders: List<Pair<String, String>> = emptyList(),
  private val exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()
) : GetDataSource<T> {

  /**
   * GET request returning an object
   */
  override suspend fun get(query: Query): T {
    val response: String = tryOrThrow(exceptionMapper) {
      executeGetRequest(query)
    }
    return json.decodeFromString(serializer, response)

  }

  /**
   * GET request returning a list of objects
   */
  override suspend fun getAll(query: Query): List<T> {
    val response: String = tryOrThrow(exceptionMapper) {
      executeGetRequest(query)
    }

    return json.decodeFromString(ListSerializer(serializer), response)

  }

  private suspend fun executeGetRequest(query: Query): String =
    validateQuery(query).executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders)

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
  private val serializer: KSerializer<T>,
  private val json: Json,
  private val globalHeaders: List<Pair<String, String>> = emptyList(),
  private val exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()

) : PutDataSource<T> {

  /**
   * POST or PUT request returning an object
   * @throws IllegalArgumentException if both value and content-type of the query method are defined
   */
  override suspend fun put(query: Query, value: T?): T {
    val response: String = tryOrThrow(exceptionMapper) {
      validateQuery(query)
        .sanitizeContentType(value)
        .executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders)
    }

    return if (serializer.descriptor != Unit.serializer().descriptor) {
      json.decodeFromString(serializer, response)
    } else { // If Unit.serializer() is used is because we want to ignore the response and just return Unit
      Unit as T
    }
  }

  /**
   * POST or PUT request returning a list of objects
   * @throws IllegalArgumentException if both value and content-type of the query method are defined
   */
  override suspend fun putAll(query: Query, value: List<T>?): List<T> {
    val response: String = tryOrThrow(exceptionMapper) {
      validateQuery(query)
        .sanitizeContentType(value)
        .executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders)
    }
    return if (serializer.descriptor != Unit.serializer().descriptor) {
      json.decodeFromString(ListSerializer(serializer), response)
    } else { // If Unit.serializer() is used is because we want to ignore the response and just return an empty list of Unit
      emptyList<Unit>() as List<T>
    }
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
