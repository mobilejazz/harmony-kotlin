package com.harmony.kotlin.data.datasource.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal suspend fun NetworkQuery.executeKtorRequest(httpClient: HttpClient, baseUrl: String, globalHeaders: List<Pair<String, String>>): String {
  val query = this
  return httpClient.request {
    method = query.method.mapToKtorMethod()

    url(query.generateKtorUrl(baseUrl))

    // headers
    if (query is GenericOAuthQuery) {
      oauthPasswordHeader(getPasswordTokenInteractor = query.getPasswordTokenInteractor)
    }
    headers(globalHeaders)
    headers(query.mergeHeaders())

    // body
    query.method.contentType?.let { contentType ->
      body = when (contentType) {
        is NetworkQuery.ContentType.FormUrlEncoded -> {
          FormDataContent(
            Parameters.build {
              contentType.params.forEach {
                append(it.first, it.second)
              }
            }
          )
        }
        is NetworkQuery.ContentType.Json<*> -> {
          contentType.entity!!
        }
      }
    }
  }
}

/**
 * Creates a url using Ktor URLBuilder with baseUrl + path + url params
 */
private fun NetworkQuery.generateKtorUrl(baseUrl: String): Url {

  val sanitizedBaseUrl = baseUrl.removeSuffix("/")
  val sanitizedPaths = this.path.split("/").filter { it.isNotEmpty() }.joinToString(separator = "/", prefix = "/")

  val urlBuilder = URLBuilder(sanitizedBaseUrl + sanitizedPaths)

  if (this.urlParams.isNotEmpty()) {
    urlBuilder.parameters.also {
      it.appendAll(generateKtorUrlParams(this.urlParams))
    }
  }

  return urlBuilder.build()
}

private fun generateKtorUrlParams(params: List<Pair<String, String>>): Parameters {
  val parametersBuilder = ParametersBuilder(params.size)
  params.forEach {
    parametersBuilder.append(it.first, it.second)
  }
  return parametersBuilder.build()
}

/**
 * Transforms query method to Ktor method
 */
private fun NetworkQuery.Method.mapToKtorMethod(): HttpMethod {
  return when (this) {
    is NetworkQuery.Method.Get -> HttpMethod.Get
    is NetworkQuery.Method.Post -> HttpMethod.Post
    is NetworkQuery.Method.Put -> HttpMethod.Put
    is NetworkQuery.Method.Delete -> HttpMethod.Delete
  }
}

/**
 * Transforms query content type to Ktor content type
 */
private fun NetworkQuery.ContentType.mapToKtorContentType(): ContentType {
  return when (this) {
    is NetworkQuery.ContentType.FormUrlEncoded -> ContentType.Application.FormUrlEncoded
    is NetworkQuery.ContentType.Json<*> -> ContentType.Application.Json
  }
}
