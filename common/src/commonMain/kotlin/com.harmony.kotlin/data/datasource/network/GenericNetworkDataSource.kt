package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.error.QueryNotSupportedException
import com.harmony.kotlin.data.query.Query
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

open class GetNetworkDataSource<T>(
    private val url: String,
    private val httpClient: HttpClient,
    private val serializer: KSerializer<T>,
    private val json: Json,
    private val globalHeaders: List<Pair<String, String>> = emptyList()
) : GetDataSource<T> {

  override suspend fun get(query: Query): T {
    val response: String = validateQuery(query).let { networkQuery ->
      httpClient.request(networkQuery.executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders))
    }

    return json.decodeFromString(serializer, response)
  }

  override suspend fun getAll(query: Query): List<T> {
    val response: String = validateQuery(query).let { networkQuery ->
      httpClient.request(networkQuery.executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders))
    }

    return json.decodeFromString(ListSerializer(serializer), response)
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

open class PutNetworkDataSource<T>(
    private val url: String,
    private val httpClient: HttpClient,
    private val serializer: KSerializer<T>,
    private val json: Json,
    private val globalHeaders: List<Pair<String, String>> = emptyList()
) : PutDataSource<T> {

  override suspend fun put(query: Query, value: T?): T {
    val response: String = validateQuery(query).let { networkQuery ->
      val contentType = networkQuery.method.contentType
      // Checking that the arguments are consistent (if both contentType and value are providing the caller must be notified about the issue)
      if (contentType != null && value != null) {
        throw IllegalArgumentException("Conflicting arguments to be used as request body:\n" +
            "query.method.contentType=${contentType}\n" +
            "value=${value}\n" +
            "You must only provide one of them")
      }
      // Updating query if value is passed as separated argument from the query
      if (contentType == null && value != null) {
        networkQuery.method.contentType = NetworkQuery.ContentType.Json(entity = value)
      }
      httpClient.request(networkQuery.executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders))
    }
    return json.decodeFromString(serializer, response)
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

  override suspend fun putAll(query: Query, value: List<T>?): List<T> = throw NotImplementedError()

}

class DeleteNetworkDataSource(
    private val url: String,
    private val httpClient: HttpClient,
    private val globalHeaders: List<Pair<String, String>> = emptyList()
) : DeleteDataSource {

  override suspend fun delete(query: Query) {
    return validateQuery(query).let { networkQuery ->
      networkQuery.executeKtorRequest(httpClient = httpClient, baseUrl = url, globalHeaders = globalHeaders)
    }
  }

  private fun validateQuery(query: Query): NetworkQuery {
    if (query !is NetworkQuery) {
      throw QueryNotSupportedException("GetNetworkDataSource only supports NetworkQuery")
    }

    if (query.method !is NetworkQuery.Method.Delete) {
      throw QueryNotSupportedException("NetworkQuery method is ${query.method} instead of DELETE")
    }

    return query
  }

  override suspend fun deleteAll(query: Query) = throw NotImplementedError()
}
