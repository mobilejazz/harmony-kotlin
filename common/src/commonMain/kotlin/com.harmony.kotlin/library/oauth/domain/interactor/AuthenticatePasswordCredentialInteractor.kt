package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.domain.interactor.PutInteractor
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class AuthenticatePasswordCredentialInteractor(private val scope: CoroutineScope, private val putToken: PutInteractor<OAuthToken>) {

  suspend operator fun invoke(id: String?, username: String, password: String, scope: CoroutineScope = this.scope): OAuthToken {
    return withContext(scope.coroutineContext) {
      val identifier = id?.let { it } ?: username
      putToken(null, OAuthQuery.Password(identifier, username, password))
    }
  }
}