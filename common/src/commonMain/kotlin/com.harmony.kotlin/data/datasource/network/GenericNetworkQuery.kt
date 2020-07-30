package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.query.*
import com.harmony.kotlin.library.oauth.domain.interactor.GetApplicationTokenInteractor
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


interface GenericOAuthQuery {
  val getPasswordTokenInteractor: GetPasswordTokenInteractor
}

open class GenericNetworkQuery(
    val path: String,
    val params: List<Pair<String, String>>? = emptyList(),
    val headers: List<Pair<String, String>>? = emptyList(),
    key: String? = null) : KeyQuery(key ?: path)

// PUT Create JavaDoc
open class GenericIdNetworkQuery<T>(
    val id: T,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    key: String? = null) : GenericNetworkQuery(path, params, headers, key)

class GenericOAuthIdNetworkQuery<T>(
    id: T,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    key: String? = null,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor) : GenericIdNetworkQuery<T>(id, path, params, headers, key), GenericOAuthQuery

// POST Create JavaDoc
open class GenericObjectNetworkQuery<T>(
    val value: T,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    key: String? = null) : GenericNetworkQuery(path, params, headers, key)

class GenericOAuthObjectNetworkQuery<T>(
    value: T,
    path: String,
    params: List<Pair<String, String>>? = emptyList(),
    headers: List<Pair<String, String>>? = emptyList(),
    key: String? = null,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor) : GenericObjectNetworkQuery<T>(value, path, params, headers, key), GenericOAuthQuery


