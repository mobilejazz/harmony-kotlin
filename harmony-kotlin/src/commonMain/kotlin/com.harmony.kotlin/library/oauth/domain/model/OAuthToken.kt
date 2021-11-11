package com.harmony.kotlin.library.oauth.domain.model

data class OAuthToken(
  val accessToken: String,
  val tokenType: String,
  val expiresIn: Long,
  val refreshToken: String?,
  val scopes: List<String>
)
