package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.library.oauth.domain.interactor.GetPasswordTokenInteractor

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
 * @param urlParams Query parameters
 * @param suspendHeaders This is a suspend function that will be executed to create headers asynchronously
 * @param key Custom cache key, if not provided the cache key will be formed using path and params
 */
open class NetworkQuery(
    var method: Method,
    val path: String,
    val urlParams: List<Pair<String, String>> = emptyList(),
    val headers: List<Pair<String, String>> = emptyList(),
    val suspendHeaders: suspend () -> List<Pair<String, String>> = { emptyList() },
    key: String? = null) : KeyQuery(key ?: generateNetworkQueryKey(method, path, urlParams)) {


  /**
   * Http method (GET, POST, PUT, DELETE)
   */
  sealed class Method(contentType: ContentType?) {
    // This can be updated from GenericNetworkDataSource
    open var contentType: ContentType? = contentType
      internal set

    internal abstract fun value(): String

    override fun toString(): String {
      return value()
    }

    /**
     * GET Method
     */
    object Get : Method(null) {
      override fun value() = "GET"
    }

    /**
     * POST Method
     * @param contentType - Content type to define the body of the request.
     * If not defined the value provided on the data source put method will be used as application/json
     */
    class Post constructor(contentType: ContentType? = null) : Method(contentType) {
      override fun value() = "POST"

      companion object {
        fun <T> json(entity: T) = Post(contentType = ContentType.Json(entity))
        fun formUrlEncoded(vararg params: Pair<String, String>) = Post(contentType = ContentType.FormUrlEncoded(params.toList()))
      }
    }

    /**
     * PUT Method
     *  @param contentType - Content type to define the body of the request.
     * If not defined the value provided on the data source put method will be used as application/json
     */
    class Put constructor(contentType: ContentType? = null) : Method(contentType) {
      override fun value() = "PUT"

      companion object {
        fun <T> json(entity: T) = Put(contentType = ContentType.Json(entity))
        fun formUrlEncoded(params: List<Pair<String, String>>) = Put(contentType = ContentType.FormUrlEncoded(params))
      }
    }

    /**
     * DELETE Method
     */
    object Delete : Method(null) {
      override fun value() = "DELETE"
    }
  }

  /**
   * Http Content type
   */
  sealed class ContentType {
    /**
     *  application/x-www-form-urlencoded http content type
     *  @param params list of key/value parameters
     */
    class FormUrlEncoded(val params: List<Pair<String, String>>) : ContentType() {
      override fun toString(): String {
        return "application/x-www-form-urlencoded : ${params.joinToString()}"
      }
    }

    /**
     *  application/json http content type
     *  @param entity json serializable object
     */
    class Json<T>(val entity: T) : ContentType() {
      override fun toString(): String {
        return "application/json : $entity"
      }

    }
  }

  /**
   * Merge headers attribute and the results of suspendHeaders function and return it
   */
  suspend fun mergeHeaders() =
      headers.toMutableList().apply {
        addAll(suspendHeaders())
      }
}

/**
 * Generates a key following the same pattern as url with query parameters.
 *
 * E.g: GET/path?key1=value1&key2=value2
 */
private fun generateNetworkQueryKey(method: NetworkQuery.Method, path: String, params: List<Pair<String, String>>): String {
  return "$method/$path" +
      if (params.isEmpty()) {
        ""
      } else {
        "?" + params.joinToString(separator = "&") {
          it.first + "=" + it.second
        }
      }
}


