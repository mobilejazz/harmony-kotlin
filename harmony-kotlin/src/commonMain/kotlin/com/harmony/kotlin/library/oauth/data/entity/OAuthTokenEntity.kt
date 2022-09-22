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

  companion object {
    const val MILLIS_IN_SECOND = 1_000
    const val EXPIRATION_MARGIN_IN_SECONDS = 120 // 2 minutes
  }

  fun isValid(): Boolean {
    val now = Clock.System.now().toEpochMilliseconds()

    val diff = now - createdAt
    val diffSeconds = diff / MILLIS_IN_SECOND

    return diffSeconds < (expiresIn - EXPIRATION_MARGIN_IN_SECONDS)
  }
}
