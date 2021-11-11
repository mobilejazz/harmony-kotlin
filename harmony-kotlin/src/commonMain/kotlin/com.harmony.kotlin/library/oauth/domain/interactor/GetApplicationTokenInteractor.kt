package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.domain.interactor.PutInteractor
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetApplicationTokenInteractor(
  private val coroutineContext: CoroutineContext,
  private val clientId: String,
  private val clientSecret: String,
  private val putToken: PutInteractor<OAuthToken>
) {

  suspend operator fun invoke(clientId: String = this.clientId, clientSecret: String = this.clientSecret): OAuthToken {
    return withContext(coroutineContext) {
      putToken(null, query = OAuthQuery.ClientCredentials(clientId, clientSecret))
    }
  }
}
