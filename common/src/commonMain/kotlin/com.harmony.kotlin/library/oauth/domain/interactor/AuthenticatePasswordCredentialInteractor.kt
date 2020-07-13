package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.domain.interactor.PutInteractor
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AuthenticatePasswordCredentialInteractor(private val coroutineContext: CoroutineContext, private val putToken: PutInteractor<OAuthToken>) {

  suspend operator fun invoke(id: String?, username: String, password: String, coroutineContext: CoroutineContext = this.coroutineContext): OAuthToken {
    return withContext(this.coroutineContext) {
      val identifier = id?.let { it } ?: username
      putToken(null, OAuthQuery.Password(identifier, username, password))
    }
  }
}