package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.*
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

open class GetNetworkDataSource<T>(
    private val url: String,
    private val httpClient: HttpClient,
    private val serializer: KSerializer<T>,
    private val json: Json,
    private val globalHeaders: List<Pair<String, String>> = emptyList()
) : GetDataSource<T> {

  override suspend fun get(query: Query): T {
    val response = when (query) {
      is OAuthClientQuery -> {
        when (query) {
          is OAuthPasswordPaginationOffsetLimitQuery -> {
            httpClient.get<String>(url) {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
              paginationOffsetLimitParams(query.offset, query.limit)
              headers(globalHeaders)
            }
          }
          is OAuthPasswordIntegerIdQuery -> {
            val url = "${url}/${query.id}"
            httpClient.get<String>(url) {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
              headers(globalHeaders)
            }
          }
          is OAuthPasswordIdQuery<*> -> {
            val url = "${url}/${query.identifier}"
            httpClient.get<String>(url) {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
              headers(globalHeaders)
            }
          }
          is DefaultOAuthClientQuery -> {
            httpClient.get<String>(url) {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
              headers(globalHeaders)
            }
          }
          else -> notSupportedQuery()
        }
      }
      is PaginationOffsetLimitQuery -> {
        httpClient.get<String>(url) {
          paginationOffsetLimitParams(query.offset, query.limit)
          headers(globalHeaders)
        }
      }
      is IntegerIdQuery -> {
        val url = "${url}/${query.id}"
        httpClient.get<String>(url) {
          headers(globalHeaders)
        }
      }
      is IdQuery<*> -> {
        val url = "${url}/${query.identifier}"
        httpClient.get<String>(url) {
          headers(globalHeaders)
        }
      }
      else -> {
        httpClient.get<String>(url) {
          headers(globalHeaders)
        }
      }
    }

    return json.parse(serializer, response)
  }

  override suspend fun getAll(query: Query): List<T> {
    val raw = httpClient.get<String>(url)
    return json.parse(serializer.list, raw)
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
    val response = when (query) {
      is OAuthClientQuery -> {
        when (query) {
          is OAuthPasswordObjectQuery<*> -> {
            query.value?.let {
              httpClient.post<String>(url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                contentType(ContentType.Application.Json)
                headers(globalHeaders)
                body = it
              }
            } ?: throw IllegalArgumentException("ObjectQuery value is null")
          }
          is OAuthPasswordIntegerIdQuery -> {
            value?.let {
              httpClient.put<String>("${url}/${query.id}") {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                contentType(ContentType.Application.Json)
                headers(globalHeaders)
                body = value
              }
            } ?: throw IllegalArgumentException("value != null")
          }
          else -> notSupportedQuery()
        }
      }
      is ObjectQuery<*> -> {
        query.value?.let {
          httpClient.post<String>(url) {
            contentType(ContentType.Application.Json)
            headers(globalHeaders)
            body = it
          }
        } ?: throw IllegalArgumentException("ObjectQuery value is null")
      }
      is IntegerIdQuery -> {
        value?.let {
          httpClient.put<String>("${url}/${query.id}") {
            contentType(ContentType.Application.Json)
            headers(globalHeaders)
            body = value
          }
        } ?: throw IllegalArgumentException("value != null")
      }
      is IdQuery<*> -> {
        value?.let {
          httpClient.put<String>("${url}/${query.identifier}") {
            contentType(ContentType.Application.Json)
            headers(globalHeaders)
            body = value
          }
        } ?: throw IllegalArgumentException("value != null")
      }
      else -> notSupportedQuery()
    }

    return json.parse(serializer, response)
  }

  override suspend fun putAll(query: Query, value: List<T>?): List<T> = throw NotImplementedError()

}

class DeleteNetworkDataSource(
    private val url: String,
    private val httpClient: HttpClient,
    private val globalHeaders: List<Pair<String, String>> = emptyList()
) : DeleteDataSource {

  override suspend fun delete(query: Query) {
    when (query) {
      is OAuthClientQuery -> {
        when (query) {
          is OAuthPasswordIntegerIdQuery -> {
            httpClient.delete<Unit>("${url}/${query.id}") {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
              headers(globalHeaders)
            }
          }
          is OAuthApplicationIntegerIdQuery -> {
            httpClient.delete<Unit>("${url}/${query.id}") {
              oauthApplicationCredentialHeader(query.getApplicationTokenInteractor)
              headers(globalHeaders)
            }
          }
        }
      }
      is IntegerIdQuery -> {
        httpClient.delete<Unit>("${url}/${query.id}") {
          headers(globalHeaders)
        }
      }
      is IdQuery<*> -> {
        httpClient.delete<Unit>("${url}/${query.identifier}") {
          headers(globalHeaders)
        }
      }
      else -> {
        httpClient.delete<Unit>(url) {
          headers(globalHeaders)
        }
      }
    }
  }

  override suspend fun deleteAll(query: Query) = throw NotImplementedError()
}
