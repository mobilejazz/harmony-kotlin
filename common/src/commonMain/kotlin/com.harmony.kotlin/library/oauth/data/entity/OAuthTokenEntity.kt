package com.harmony.kotlin.library.oauth.data.entity

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OAuthTokenEntity(
  @SerialName("access_token") val accessToken: String,
  @SerialName("token_type") val tokenType: String,
  @SerialName("expires_in") val expiresIn: Long,
  @SerialName("refresh_token") val refreshToken: String? = null,
  @SerialName("scope") val scopes: List<String>,
  val createdAt: Long = Clock.System.now().toEpochMilliseconds()
) {

  fun isValid(): Boolean {
    val now = Clock.System.now().toEpochMilliseconds()

    val diff = now - createdAt
    val diffSeconds = diff / 1000

    return diffSeconds < (expiresIn - 120 /* 2 minutes */)
  }
}
