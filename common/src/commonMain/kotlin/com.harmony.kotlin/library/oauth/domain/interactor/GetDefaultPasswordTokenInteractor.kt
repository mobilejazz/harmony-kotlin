package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

interface GetPasswordTokenInteractor {
  suspend operator fun invoke(id: String): OAuthToken
}

class GetDefaultPasswordTokenInteractor(private val scope: CoroutineScope,
                                        private val getToken: GetInteractor<OAuthToken>): GetPasswordTokenInteractor {

  override suspend operator fun invoke(id: String): OAuthToken {
    return withContext(scope.coroutineContext) {
      getToken(KeyQuery(id))
    }
  }
}
