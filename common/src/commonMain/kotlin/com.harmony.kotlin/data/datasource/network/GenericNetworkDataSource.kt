package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.thread.network
import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.Query
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
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
    return network {
      val response = when (query) {

        is GenericNetworkQuery -> {
          httpClient.get<String> {
            createHttpRequestFromGenericNetworkQuery(query)
          }
        }

        else -> {
          httpClient.get<String>(url) {
            headers(globalHeaders)
          }
        }
      }

      return@network json.decodeFromString(serializer, response)
    }
  }

  override suspend fun getAll(query: Query): List<T> {
    return network {
      val response = when (query) {

        is GenericNetworkQuery -> {
          httpClient.get {
            if (query is GenericOAuthQuery) {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)

            }
            createHttpRequestFromGenericNetworkQuery(query)
          }
        }

        else -> {
          httpClient.get<String>(url)
        }
      }

      val a =  ListSerializer(serializer)
      return@network json.decodeFromString(a, response)
    }
  }

  private suspend fun HttpRequestBuilder.createHttpRequestFromGenericNetworkQuery(query: GenericNetworkQuery) {
    url(generateUrl(url = this@GetNetworkDataSource.url, path = query.path, params = query.params))
    addOAuthHeaderIfNeeded(query)
    headers(globalHeaders)
    headers(query.mergeHeaders())
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
    return network {
      val response = when (query) {

        is GenericIdNetworkQuery<*> -> {
          // We need to check if it's Int or String to simplify how we generate the url. In case it isn't we should create a representation of the type as a 
          // String.
          if (query.id !is Int || query.id !is String) {
            throw IllegalArgumentException("We only accept Int or String for now")
          }

          httpClient.put {
            createHttpRequestFromGenericNetworkQuery(query, value)
          }
        }

        is GenericObjectNetworkQuery<*> -> {
          httpClient.post<String> {
            createHttpRequestFromGenericNetworkQuery(query, query.value)
          }
        }
        else -> notSupportedQuery()
      }

      return@network json.decodeFromString(serializer, response)
    }
  }

  private suspend fun <V> HttpRequestBuilder.createHttpRequestFromGenericNetworkQuery(query: GenericNetworkQuery, value: V?) {
    value?.let {
      url(generateUrl(url = this@PutNetworkDataSource.url, path = query.path, params = query.params))
      contentType(ContentType.Application.Json)
      addOAuthHeaderIfNeeded(query)
      headers(globalHeaders)
      headers(query.mergeHeaders())
      body = it as Any
    } ?: throw IllegalArgumentException("Value cannot be null")

  }

  override suspend fun putAll(query: Query, value: List<T>?): List<T> = throw NotImplementedError()

}

class DeleteNetworkDataSource(
    private val url: String,
    private val httpClient: HttpClient,
    private val globalHeaders: List<Pair<String, String>> = emptyList()
) : DeleteDataSource {

  override suspend fun delete(query: Query) {
    return network {
      when (query) {

        is GenericNetworkQuery -> {
          httpClient.delete {
            createHttpRequestFromGenericNetworkQuery(query)
          }
        }

        else -> {
          httpClient.delete<Unit>(url) {
            headers(globalHeaders)
          }
        }
      }
    }
  }

  private suspend fun HttpRequestBuilder.createHttpRequestFromGenericNetworkQuery(query: GenericNetworkQuery) {
    url(generateUrl(url = this@DeleteNetworkDataSource.url, path = query.path, params = query.params))
    addOAuthHeaderIfNeeded(query)
    headers(globalHeaders)
    headers(query.mergeHeaders())
  }

  override suspend fun deleteAll(query: Query) = throw NotImplementedError()
}

private suspend fun HttpRequestBuilder.addOAuthHeaderIfNeeded(query: Query) {
  if (query is GenericOAuthQuery) {
    oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
  }
}