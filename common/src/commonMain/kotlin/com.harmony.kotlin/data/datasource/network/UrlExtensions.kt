package com.harmony.kotlin.data.datasource.network

import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder

fun generateUrl(url: String, path: String, params: List<Pair<String, String>> = emptyList()): String {

  val urlBuilder = URLBuilder("$url/$path")

  if (params.isNotEmpty()) {
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
