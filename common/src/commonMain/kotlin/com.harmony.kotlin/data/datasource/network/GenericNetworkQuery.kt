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

/**
 * Implement the interface and override getPasswordTokenInteractor when you need oAuth authorization.
 */
interface GenericOAuthQuery {
  val getPasswordTokenInteractor: GetPasswordTokenInteractor
}

/**
 * Base class.
 * In case you want to customize the cache key, provide a key string.
 */
open class GenericNetworkQuery(
    open val path: String,
    open val params: List<Pair<String, String>> = emptyList(),
    open val headers: List<Pair<String, String>> = emptyList(),
    key: String? = null) : KeyQuery(key ?: path)

/**
 * Creates a PUT Http Method. Modifies the ID with the value that receive the put method.
 */
open class GenericIdNetworkQuery<T>(
    val id: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    key: String? = null) : GenericNetworkQuery(path, params, headers, key)

class GenericOAuthIdNetworkQuery<T>(
    id: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    key: String? = null,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor) : GenericIdNetworkQuery<T>(id, path, params, headers, key), GenericOAuthQuery

/**
 * Creates a POST Http Method. Creates a new entry with the value in the query object.
 */
open class GenericObjectNetworkQuery<T>(
    val value: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    key: String? = null) : GenericNetworkQuery(path, params, headers, key)

class GenericOAuthObjectNetworkQuery<T>(
    value: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    key: String? = null,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor) : GenericObjectNetworkQuery<T>(value, path, params, headers, key), GenericOAuthQuery


