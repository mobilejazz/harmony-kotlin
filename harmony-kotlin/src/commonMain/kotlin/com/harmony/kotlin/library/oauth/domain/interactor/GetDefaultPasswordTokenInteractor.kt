package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface GetPasswordTokenInteractor {
  suspend operator fun invoke(id: String): OAuthToken
}

class GetDefaultPasswordTokenInteractor(
  private val coroutineContext: CoroutineContext,
  private val getToken: GetInteractor<OAuthToken>
) : GetPasswordTokenInteractor {

  override suspend operator fun invoke(id: String): OAuthToken {
    return withContext(coroutineContext) {
      getToken(KeyQuery(id))
    }
  }
}
