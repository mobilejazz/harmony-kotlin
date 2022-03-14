package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.data.datasource.network.ktor.configureExceptionErrorMapping
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json

class ApiMock {
  public val client = HttpClient(ApiMockEngine().get()) {
    install(JsonFeature) {
      serializer = KotlinxSerializer(
        Json {
          isLenient = false
          ignoreUnknownKeys = true
        }
      )
    }

    configureExceptionErrorMapping()
  }

  suspend fun executeRequest(request: MockRequest) {
    request.executeRequest(client)
  }
}

private class ApiMockEngine {
  fun get() = client.engine

  private val client = HttpClient(MockEngine) {
    val mockRequests = listOf(
      UnauthorizedRequest,
      NotFoundRequest,
      BadRequest
    )

    engine {
      addHandler { request ->
        try {
          val mockRequest = mockRequests.first {
            it.url == request.url
          }
          respond(mockRequest.responseBody, mockRequest.statusCode, mockRequest.responseHeaders)
        } catch (_: Exception) {
          error("Unhandled ${request.url.encodedPath}")
        }
      }
    }
  }
}

interface MockRequest {
  val method: HttpMethod
  val url: Url
  val responseHeaders: Headers
    get() {
      return headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
    }
  val responseBody: String
    get() = ""
  val statusCode: HttpStatusCode

  suspend fun executeRequest(client: HttpClient) {
    client.request<String> {
      method = HttpMethod.Get
      url(this@MockRequest.url)
    }
  }
}

object UnauthorizedRequest : MockRequest {
  override val method: HttpMethod = HttpMethod.Get
  override val url = Url("https://mockrequest.com/unauthorized")
  override val statusCode: HttpStatusCode = HttpStatusCode.Unauthorized
}

object NotFoundRequest : MockRequest {
  override val method: HttpMethod = HttpMethod.Get
  override val url = Url("https://mockrequest.com/not_found")
  override val statusCode: HttpStatusCode = HttpStatusCode.NotFound
}

object BadRequest : MockRequest {
  override val method: HttpMethod = HttpMethod.Get
  override val url = Url("https://mockrequest.com/bad_request")
  override val responseBody: String = "bad_request"
  override val statusCode: HttpStatusCode = HttpStatusCode.BadRequest
}

object InvalidJsonResponseRequest : MockRequest {
  override val method: HttpMethod = HttpMethod.Get
  override val url = Url("https://mockrequest.com/invalid_json_response")
  override val responseBody: String = "{ \"fo_o\": \"bar\" }"
  override val statusCode: HttpStatusCode = HttpStatusCode.OK
}
