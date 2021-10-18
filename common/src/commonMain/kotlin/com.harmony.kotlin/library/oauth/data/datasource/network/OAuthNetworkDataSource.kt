package com.harmony.kotlin.library.oauth.data.datasource.network

import com.harmony.kotlin.common.thread.network
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.library.oauth.data.datasource.network.model.OAuthBodyRequest
import com.harmony.kotlin.library.oauth.data.entity.OAuthTokenEntity
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class OAuthNetworkDataSource(
  private val httpClient: HttpClient,
  private val apiPath: String,
  private val basicAuthorizationCode: String
) : PutDataSource<OAuthTokenEntity> {

  override suspend fun put(query: Query, value: OAuthTokenEntity?): OAuthTokenEntity {
    return network {
      val bodyRequest = when (query) {
        is OAuthQuery.Password -> OAuthBodyRequest.Password(query.username, query.password)
        is OAuthQuery.RefreshToken -> OAuthBodyRequest.RefreshToken(query.refreshToken)
        is OAuthQuery.ClientCredentials -> OAuthBodyRequest.ClientCredentials(
          query.clientId,
          query.clientSecret
        )
        else -> notSupportedQuery()
      }

      val url = "$apiPath/auth/token"
      httpClient.post<OAuthTokenEntity>(url) {
        header("Authorization", "Basic $basicAuthorizationCode")
        contentType(ContentType.Application.Json)
        body = bodyRequest
      }
    }
  }

  override suspend fun putAll(query: Query, value: List<OAuthTokenEntity>?): List<OAuthTokenEntity> =
    throw NotImplementedError()
}
