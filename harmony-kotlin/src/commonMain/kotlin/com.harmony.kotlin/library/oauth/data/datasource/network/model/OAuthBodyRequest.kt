package com.harmony.kotlin.library.oauth.data.datasource.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed class OAuthBodyRequest(@SerialName("grant_type") private val grantType: String) {

  @Serializable
  class Password(val username: String, val password: String) : OAuthBodyRequest("password")

  @Serializable
  class RefreshToken(
    @SerialName("refresh_token") val refreshToken: String
  ) : OAuthBodyRequest("refresh_token")

  @Serializable
  class ClientCredentials(
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String
  ) :
    OAuthBodyRequest("client_credentials")
}
