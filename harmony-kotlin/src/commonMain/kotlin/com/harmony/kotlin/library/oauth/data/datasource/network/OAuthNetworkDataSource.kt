package com.harmony.kotlin.library.oauth.data.datasource.network

import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.error.notSupportedQuery
import com.harmony.kotlin.library.oauth.data.datasource.network.model.OAuthBodyRequest
import com.harmony.kotlin.library.oauth.data.entity.OAuthTokenEntity
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class OAuthNetworkDataSource(
  private val httpClient: HttpClient,
  private val apiPath: String,
  private val basicAuthorizationCode: String
) : PutDataSource<OAuthTokenEntity> {

  override suspend fun put(query: Query, value: OAuthTokenEntity?): OAuthTokenEntity {
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
    return httpClient.post(url) {
      header("Authorization", "Basic $basicAuthorizationCode")
      contentType(ContentType.Application.Json)
      setBody(bodyRequest)
    }.body()
  }
}
