package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.IntegerIdQuery
import com.harmony.kotlin.data.query.ObjectQuery
import com.harmony.kotlin.data.query.PaginationOffsetLimitQuery
import com.harmony.kotlin.data.query.Query
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

open class GetNetworkDataSource<T>(
    private val url: String,
    private val httpClient: HttpClient,
    private val serializer: KSerializer<T>,
    private val json: Json
) : GetDataSource<T> {

  override suspend fun get(query: Query): T {
    val response = when (query) {
      is OAuthClientQuery -> {
        when (query) {
          is OAuthPasswordPaginationOffsetLimitQuery -> {
            httpClient.get<String>(url) {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
              paginationOffsetLimitParams(query.offset, query.limit)
            }
          }
          is OAuthPasswordIntegerIdQuery -> {
            val url = "${url}/${query.id}"
            httpClient.get<String>(url) {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
            }
          }
          else -> notSupportedQuery()
        }
      }
      is PaginationOffsetLimitQuery -> {
        httpClient.get<String>(url) {
          paginationOffsetLimitParams(query.offset, query.limit)
        }
      }
      is IntegerIdQuery -> {
        val url = "${url}/${query.id}"
        httpClient.get<String>(url)
      }
      else -> {
        httpClient.get<String>(url)
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
    private val json: Json
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
                body = it
              }
            } ?: throw IllegalArgumentException("ObjectQuery value is null")
          }
          is OAuthPasswordIntegerIdQuery -> {
            value?.let {
              httpClient.put<String>("${url}/${query.id}") {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                contentType(ContentType.Application.Json)
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
            body = it
          }
        } ?: throw IllegalArgumentException("ObjectQuery value is null")
      }
      is IntegerIdQuery -> {
        value?.let {
          httpClient.post<String>("${url}/${query.id}") {
            contentType(ContentType.Application.Json)
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
    private val httpClient: HttpClient
) : DeleteDataSource {

  override suspend fun delete(query: Query) {
    when (query) {
      is OAuthClientQuery -> {
        when (query) {
          is OAuthPasswordIntegerIdQuery -> {
            httpClient.delete<Unit>("${url}/${query.id}") {
              oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
            }
          }
          is OAuthApplicationIntegerIdQuery -> {
            httpClient.delete<Unit>("${url}/${query.id}") {
              oauthApplicationCredentialHeader(query.getApplicationTokenInteractor)
            }
          }
        }
      }
      is IntegerIdQuery -> {
        httpClient.delete<Unit>("${url}/${query.id}")
      }
    }
  }

  override suspend fun deleteAll(query: Query) = throw NotImplementedError()
}
