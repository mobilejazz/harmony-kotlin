package com.harmony.kotlin.data.datasource.network

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder

fun HttpRequestBuilder.generateUrl(url: String, path: String, params: List<Pair<String, String>>?): String {

  val urlBuilder = URLBuilder("$url/$path")
  params?.also { params ->
    urlBuilder.parameters.also {
      it.appendAll(generateParams(params).build())
    }
  }
  return urlBuilder.buildString()

}

private fun generateParams(params: List<Pair<String, String>>): ParametersBuilder {
  val parametersBuilder = ParametersBuilder(params.size)
  params.forEach {
    parametersBuilder.append(it.first, it.second)
  }
  return parametersBuilder
}
