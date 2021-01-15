package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.library.oauth.domain.interactor.GetPasswordTokenInteractor

interface OAuthClientQuery: GenericOAuthQuery

class DefaultOAuthClientQuery(override val getPasswordTokenInteractor: GetPasswordTokenInteractor) : GenericNetworkQuery(""), OAuthClientQuery

// OAuth key query base
class OAuthPasswordKeyQuery(
    key: String,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor
) : GenericNetworkQuery("", key = key), OAuthClientQuery

// OAuth integer id query
class OAuthPasswordIntegerIdQuery(
    val id: Int,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor
) : GenericNetworkQuery(path = id.toString(), key = id.toString()),
    OAuthClientQuery

// OAuth integer id query
open class OAuthPasswordIdQuery<T>(
    id: T,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor
) : GenericOAuthIdNetworkQuery<T>(id, key = id.toString(), getPasswordTokenInteractor = getPasswordTokenInteractor, path = id.toString()),
    OAuthClientQuery

// OAuth password pagination query
open class OAuthPasswordPaginationOffsetLimitQuery(
    identifier: String? = null,
    val offset: Int,
    val limit: Int,
    override val getPasswordTokenInteractor:
    GetPasswordTokenInteractor
) : GenericNetworkQuery(
    path = "",
    listOf(Pair("offset", offset.toString()), Pair("limit", limit.toString())),
    key = "$identifier-$offset-$limit"
), OAuthClientQuery

// OAuth password object query
class OAuthPasswordObjectQuery<T>(
    value: T,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor
) : GenericOAuthObjectNetworkQuery<T>(value, "", getPasswordTokenInteractor = getPasswordTokenInteractor),
    OAuthClientQuery

/**
 * Implement the interface and override getPasswordTokenInteractor when you need oAuth authorization.
 */
interface GenericOAuthQuery {
  val getPasswordTokenInteractor: GetPasswordTokenInteractor
}

/**
 * Base Query to be used by the Generic Network DataSources.
 *
 * @param path Relative path to the endpoint
 * @param params Query parameters
 * @param suspendHeaders This is a suspend function that will be executed to create headers asynchronously
 * @param key Custom cache key, if not provided the cache key will be formed using path and params
 */
open class GenericNetworkQuery(
    val path: String,
    open val params: List<Pair<String, String>> = emptyList(),
    open val headers: List<Pair<String, String>> = emptyList(),
    open val suspendHeaders: suspend () -> List<Pair<String, String>> = { emptyList() },
    key: String? = null) : KeyQuery(key ?: generateNetworkQueryKey(path, params)) {

  /**
   * Merge headers attribute and the results of suspendHeaders function and return it
   */
  suspend fun mergeHeaders() =
      headers.toMutableList().apply {
        addAll(suspendHeaders())
      }
}

/**
 * Generates a key following the same pattern as a GET url.
 *
 * E.g: path?key1=value1&key2=value2
 */
private fun generateNetworkQueryKey(path: String, params: List<Pair<String, String>>): String {
  return path +
      if (params.isEmpty()) {
        ""
      } else {
        "?" + params.joinToString(separator = "&") {
          it.first + "=" + it.second
        }
      }
}

/**
 * Creates a PUT Http Method. Modifies the ID with the value that receive the put method.
 */
open class GenericIdNetworkQuery<T>(
    val id: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    suspendHeaders: suspend () -> List<Pair<String, String>> = { emptyList() }, key: String? = null
) : GenericNetworkQuery(path = path, params = params, headers = headers, suspendHeaders = suspendHeaders, key = key)

open class GenericOAuthIdNetworkQuery<T>(
    id: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    suspendHeaders: suspend () -> List<Pair<String, String>> = { emptyList() },
    key: String? = null,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor
) : GenericIdNetworkQuery<T>(id = id, path = path, params = params, headers = headers, suspendHeaders = suspendHeaders, key = key), GenericOAuthQuery

/**
 * Creates a POST Http Method. Creates a new entry with the value in the query object.
 */
open class GenericObjectNetworkQuery<T>(
    val value: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    suspendHeaders: suspend () -> List<Pair<String, String>> = { emptyList() },
    key: String? = null) : GenericNetworkQuery(path = path, params = params, headers = headers, suspendHeaders = suspendHeaders, key = key)

open class GenericOAuthObjectNetworkQuery<T>(
    value: T,
    path: String,
    params: List<Pair<String, String>> = emptyList(),
    headers: List<Pair<String, String>> = emptyList(),
    suspendHeaders: suspend () -> List<Pair<String, String>> = { emptyList() },
    key: String? = null,
    override val getPasswordTokenInteractor: GetPasswordTokenInteractor
) : GenericObjectNetworkQuery<T>(value = value, path = path, params = params, headers = headers, suspendHeaders = suspendHeaders, key = key), GenericOAuthQuery


