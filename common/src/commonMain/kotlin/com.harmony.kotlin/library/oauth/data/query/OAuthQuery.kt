package com.harmony.kotlin.library.oauth.data.query

import com.harmony.kotlin.data.query.KeyQuery

internal sealed class OAuthQuery(private val grantType: String, private val id: String) : KeyQuery(id) {

  class Password(id: String, val username: String, val password: String) : OAuthQuery("password", id)

  class RefreshToken(id: String, val refreshToken: String) : OAuthQuery("refresh_token", id)

  class ClientCredentials(val clientId: String, val clientSecret: String) : OAuthQuery("client_credentials", "$clientId:$clientSecret")
}
