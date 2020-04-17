package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.query.*
import com.harmony.kotlin.library.oauth.domain.interactor.GetApplicationTokenInteractor
import com.harmony.kotlin.library.oauth.domain.interactor.GetDefaultPasswordTokenInteractor
import com.harmony.kotlin.library.oauth.domain.interactor.GetPasswordTokenInteractor

interface OAuthClientQuery

class DefaultOAuthClientQuery(val getPasswordTokenInteractor: GetPasswordTokenInteractor) : Query(), OAuthClientQuery

// OAuth integer id query
class OAuthPasswordIntegerIdQuery(id: Int, val getPasswordTokenInteractor: GetPasswordTokenInteractor) : IntegerIdQuery(id),
    OAuthClientQuery

// OAuth integer id query
open class OAuthPasswordIdQuery<T>(id: T, val getPasswordTokenInteractor: GetPasswordTokenInteractor) : IdQuery<T>(id),
    OAuthClientQuery

class OAuthApplicationIntegerIdQuery(id: Int, val getApplicationTokenInteractor: GetApplicationTokenInteractor) : IntegerIdQuery(id),
    OAuthClientQuery

// OAuth password pagination query
open class OAuthPasswordPaginationOffsetLimitQuery(offset: Int, limit: Int, val getPasswordTokenInteractor: GetPasswordTokenInteractor) :
    PaginationOffsetLimitQuery(offset, limit),
    OAuthClientQuery

// OAuth password object query
class OAuthPasswordObjectQuery<T>(value: T, val getPasswordTokenInteractor: GetPasswordTokenInteractor) : ObjectQuery<T>(value),
    OAuthClientQuery

