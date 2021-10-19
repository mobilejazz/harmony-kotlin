package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.domain.interactor.PutInteractor
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AuthenticatePasswordCredentialInteractor(
  private val putToken: PutInteractor<OAuthToken>,
  private val coroutineContext: CoroutineContext
) {

  suspend operator fun invoke(id: String?, username: String, password: String): OAuthToken {
    return withContext(coroutineContext) {
      val identifier = id?.let { it } ?: username
      putToken(null, OAuthQuery.Password(identifier, username, password))
    }
  }
}