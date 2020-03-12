package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.domain.interactor.PutInteractor
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class GetApplicationTokenInteractor(private val scope: CoroutineScope,
                                    private val clientId: String,
                                    private val clientSecret: String,
                                    private val putToken: PutInteractor<OAuthToken>) {

  suspend operator fun invoke(clientId: String = this.clientId, clientSecret: String = this.clientSecret): OAuthToken {
    return withContext(scope.coroutineContext) {
      putToken(null, query = OAuthQuery.ClientCredentials(clientId, clientSecret))
    }
  }
}
