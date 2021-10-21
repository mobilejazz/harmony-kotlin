package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.domain.interactor.DeleteInteractor
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class DeletePasswordTokenInteractor(private val coroutineContext: CoroutineContext, private val deleteToken: DeleteInteractor) {
  suspend operator fun invoke(id: String) {
    withContext(coroutineContext) {
      deleteToken(KeyQuery(id))
    }
  }
}




