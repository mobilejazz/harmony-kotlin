package com.harmony.kotlin.library.oauth.domain.interactor

import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.domain.interactor.DeleteInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext


class DeletePasswordTokenInteractor(private val scope: CoroutineScope, private val deleteToken: DeleteInteractor) {
  suspend operator fun invoke(id: String) {
    withContext(scope.coroutineContext) {
      deleteToken(KeyQuery(id))
    }
  }
}




