package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.thread.network
import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.Query
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.list
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

        /*

        is OAuthClientQuery -> {
          when (query) {
            is OAuthPasswordPaginationOffsetLimitQuery -> {
              httpClient.get<String>(_url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                paginationOffsetLimitParams(query.offset, query.limit)
                headers(globalHeaders)
              }
            }
            is OAuthPasswordIntegerIdQuery -> {
              val url = "${_url}/${query.id}"
              httpClient.get<String>(url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
            is OAuthPasswordIdQuery<*> -> {
              val url = "${_url}/${query.identifier}"
              httpClient.get<String>(url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
            is DefaultOAuthClientQuery -> {
              httpClient.get<String>(_url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
            is OAuthPasswordKeyQuery -> {
              httpClient.get<String>(_url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
            else -> notSupportedQuery()
          }
        }
        is PaginationOffsetLimitQuery -> {
          httpClient.get<String>(_url) {
            paginationOffsetLimitParams(query.offset, query.limit)
            headers(globalHeaders)
          }
        }
        is IntegerIdQuery -> {
          val url = "${_url}/${query.id}"
          httpClient.get<String>(url) {
            headers(globalHeaders)
          }
        }
        is IdQuery<*> -> {
          val url = "${_url}/${query.identifier}"
          httpClient.get<String>(url) {
            headers(globalHeaders)
          }
        }

         */
      }

      return@network json.parse(serializer, response)
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

        /*

        is OAuthClientQuery -> {
          when (query) {
            is OAuthPasswordPaginationOffsetLimitQuery -> {
              httpClient.get<String>(_url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                paginationOffsetLimitParams(query.offset, query.limit)
                headers(globalHeaders)
              }
            }
            is DefaultOAuthClientQuery -> {
              httpClient.get<String>(_url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }

            is OAuthPasswordIdQuery<*> -> {
              httpClient.get<String>("${_url}/${query.identifier}") {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
            is OAuthPasswordKeyQuery -> {
              httpClient.get<String>(_url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
            else -> notSupportedQuery()
          }
        }
         */
      }
      return@network json.parse(serializer.list, response)
    }
  }

  private suspend fun HttpRequestBuilder.createHttpRequestFromGenericNetworkQuery(query: GenericNetworkQuery) {
    url(generateUrl(url = this@GetNetworkDataSource.url, path = query.path, params = query.params))
    addOAuthorizationHeader(query)
    headers(globalHeaders)
    headers(query.headers)
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
            createHttpRequestFromGenericNetworkQuery(query, value)
          }
        }
        else -> notSupportedQuery()
      }
      /*

      is OAuthClientQuery -> {
        when (query) {
          is OAuthPasswordObjectQuery<*> -> {
            query.value?.let {
              httpClient.post<String>(_url) {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                contentType(ContentType.Application.Json)
                headers(globalHeaders)
                body = it
              }
            } ?: throw IllegalArgumentException("ObjectQuery value is null")
          }
          is OAuthPasswordIntegerIdQuery -> {
            value?.let {
              httpClient.put<String>("${_url}/${query.id}") {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                contentType(ContentType.Application.Json)
                headers(globalHeaders)
                body = value
              }
            } ?: throw IllegalArgumentException("value != null")
          }
          is OAuthPasswordIdQuery<*> -> {
            value?.let {
              httpClient.put<String>("${_url}/${query.identifier}") {
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
          httpClient.post<String>(_url) {
            contentType(ContentType.Application.Json)
            headers(globalHeaders)
            body = it
          }
        } ?: throw IllegalArgumentException("ObjectQuery value is null")
      }
      is IntegerIdQuery -> {
        value?.let {
          httpClient.put<String>("${_url}/${query.id}") {
            contentType(ContentType.Application.Json)
            headers(globalHeaders)
            body = value
          }
        } ?: throw IllegalArgumentException("value != null")
      }
      is IdQuery<*> -> {
        value?.let {
          httpClient.put<String>("${_url}/${query.identifier}") {
            contentType(ContentType.Application.Json)
            headers(globalHeaders)
            body = value
          }
        } ?: throw IllegalArgumentException("value != null")
      }

       */

      return@network json.parse(serializer, response)
    }
  }

  private suspend fun HttpRequestBuilder.createHttpRequestFromGenericNetworkQuery(query: GenericNetworkQuery, value: T?) {
    url(generateUrl(url = this@PutNetworkDataSource.url, path = query.path, params = query.params))
    contentType(ContentType.Application.Json)
    addOAuthorizationHeader(query)
    headers(globalHeaders)
    headers(query.headers)
    body = value ?: throw IllegalArgumentException("Value cannot be null")
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

        /*
        is OAuthClientQuery -> {
          when (query) {
            is OAuthPasswordIntegerIdQuery -> {
              httpClient.delete<Unit>("${_url}/${query.id}") {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
            is OAuthApplicationIntegerIdQuery -> {
              httpClient.delete<Unit>("${_url}/${query.id}") {
                oauthApplicationCredentialHeader(query.getApplicationTokenInteractor)
                headers(globalHeaders)
              }
            }
            is OAuthPasswordIdQuery<*> -> {
              httpClient.delete<Unit>("${_url}/${query.identifier}") {
                oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
                headers(globalHeaders)
              }
            }
          }
        }
        is IntegerIdQuery -> {
          httpClient.delete<Unit>("${_url}/${query.id}") {
            headers(globalHeaders)
          }
        }
        is IdQuery<*> -> {
          httpClient.delete<Unit>("${_url}/${query.identifier}") {
            headers(globalHeaders)
          }
        }
         */
      }
    }
  }

  private suspend fun HttpRequestBuilder.createHttpRequestFromGenericNetworkQuery(query: GenericNetworkQuery) {
    url(generateUrl(url = this@DeleteNetworkDataSource.url, path = query.path, params = query.params))
    addOAuthorizationHeader(query)
    headers(globalHeaders)
    headers(query.headers)
  }

  override suspend fun deleteAll(query: Query) = throw NotImplementedError()
}

private suspend fun HttpRequestBuilder.addOAuthorizationHeader(query: Query) {
  if (query is GenericOAuthQuery) {
    oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
  }
}