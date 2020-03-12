package com.harmony.kotlin.library.oauth.data.mapper

import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.library.oauth.data.entity.OAuthTokenEntity
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken

internal class OAuthTokenEntityToOAuthTokenMapper(): Mapper<OAuthTokenEntity, OAuthToken> {

  override fun map(from: OAuthTokenEntity): OAuthToken = OAuthToken(from.accessToken, from.tokenType, from.expiresIn, from.refreshToken, from.scopes)
}