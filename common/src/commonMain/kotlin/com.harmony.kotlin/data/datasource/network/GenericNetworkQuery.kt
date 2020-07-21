package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.query.*
import com.harmony.kotlin.library.oauth.domain.interactor.GetApplicationTokenInteractor
import com.harmony.kotlin.library.oauth.domain.interactor.GetDefaultPasswordTokenInteractor
import com.harmony.kotlin.library.oauth.domain.interactor.GetPasswordTokenInteractor

interface OAuthClientQuery

class DefaultOAuthClientQuery(val getPasswordTokenInteractor: GetPasswordTokenInteractor) : Query(), OAuthClientQuery

// OAuth key query base
class OAuthPasswordKeyQuery(key: String, val getPasswordTokenInteractor: GetPasswordTokenInteractor) : KeyQuery(key), OAuthClientQuery

// OAuth integer id query
class OAuthPasswordIntegerIdQuery(id: Int, val getPasswordTokenInteractor: GetPasswordTokenInteractor) : IntegerIdQuery(id),
    OAuthClientQuery

// OAuth integer id query
open class OAuthPasswordIdQuery<T>(id: T, val getPasswordTokenInteractor: GetPasswordTokenInteractor) : IdQuery<T>(id),
    OAuthClientQuery

class OAuthApplicationIntegerIdQuery(id: Int, val getApplicationTokenInteractor: GetApplicationTokenInteractor) : IntegerIdQuery(id),
    OAuthClientQuery

// OAuth password pagination query
open class OAuthPasswordPaginationOffsetLimitQuery(identifier: String? = null, offset: Int, limit: Int, val getPasswordTokenInteractor:
GetPasswordTokenInteractor) : PaginationOffsetLimitQuery(identifier ?: "$offset-$limit", offset, limit), OAuthClientQuery

// OAuth password object query
class OAuthPasswordObjectQuery<T>(value: T, val getPasswordTokenInteractor: GetPasswordTokenInteractor) : ObjectQuery<T>(value),
    OAuthClientQuery

abstract class GenericNetworkQuery(
    val path: String,
    val params: List<Pair<String, String>>?,
    val headers: List<Pair<String, String>>?,
    cacheKey: String? = null) : KeyQuery(cacheKey ?: path)

abstract class GenericOAuthNetworkQuery(
    val getPasswordTokenInteractor: GetPasswordTokenInteractor,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    cacheKey: String? = null) : GenericNetworkQuery(path, params, headers, cacheKey)

// PUT Create JavaDoc
abstract class GenericIdNetworkQuery(
    val id: Int,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    cacheKey: String? = null) : GenericNetworkQuery(path, params, headers, cacheKey)

abstract class GenericOAuthIdNetworkQuery(
    val id: Int,
    getPasswordTokenInteractor: GetPasswordTokenInteractor,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    cacheKey: String? = null) : GenericOAuthNetworkQuery(getPasswordTokenInteractor, path, params, headers, cacheKey)

// POST Create JavaDoc
abstract class GenericObjectNetworkQuery<T>(
    val value: T,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    cacheKey: String? = null) : GenericNetworkQuery(path, params, headers, cacheKey)

abstract class GenericOAuthObjectNetworkQuery<T>(
    val value: T,
    getPasswordTokenInteractor: GetPasswordTokenInteractor,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    cacheKey: String? = null) : GenericOAuthNetworkQuery(getPasswordTokenInteractor, path, params, headers, cacheKey)


