package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class GetPasswordTokenInteractor(private val scope: CoroutineScope,
                                 private val getToken: GetInteractor<OAuthToken>) {

  suspend operator fun invoke(id: String, scope: CoroutineScope = this.scope): OAuthToken {
    return withContext(scope.coroutineContext) {
      getToken(KeyQuery(id))
    }
  }
}
