package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomLong
import com.harmony.kotlin.common.randomPairList
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.common.removeSpaces
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.MockMapper
import com.harmony.kotlin.data.query.anyQuery
import com.harmony.kotlin.error.QueryNotSupportedException
import com.harmony.kotlin.library.oauth.domain.interactor.GetPasswordTokenInteractor
import com.harmony.kotlin.library.oauth.domain.interactor.MockGetPasswordTokenInteractor
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.util.flattenEntries
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.content
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@UsesMocks(Mapper::class, GetPasswordTokenInteractor::class)
class GetNetworkDataSourceTests : BaseTest() {

  private val mocker = Mocker()

  @BeforeTest
  fun beforeTest() {
    mocker.reset()
  }

  @Test
  fun `should throw QueryNotSupportedException if query is invalid`() = runTest {
    val datasource = GetNetworkDataSource(anyUrl(), mockHttpClient(), anySerializer(), anyJson())

    assertFailsWith<QueryNotSupportedException> {
      datasource.get(anyQuery())
    }
  }

  @Test
  fun `should throw QueryNotSupportedException if NetworkQuery is not Method Get`() = runTest {
    val datasource = GetNetworkDataSource(anyUrl(), mockHttpClient(), anySerializer(), anyJson())

    val invalidQuery = NetworkQuery(NetworkQuery.Method.Delete, randomString())

    assertFailsWith<QueryNotSupportedException> {
      datasource.get(invalidQuery)
    }
  }

  @Test
  fun `should propagate the same exception as the mapper when an exception is thrown`() = runTest {
    val mockMapper = MockMapper<Exception, Exception>(mocker)
    val datasource = GetNetworkDataSource(anyUrl(), mockHttpClient(), anySerializer(), anyJson(), exceptionMapper = mockMapper)
    val invalidQuery = anyQuery()

    assertFailsWith<AnyException> {
      mocker.every { mockMapper.map(isAny()) } returns AnyException()
      datasource.get(invalidQuery)
    }
  }

  @Test
  fun `should successfully execute a get network request`() = runTest {
    val expectedGlobalHeaders = anyHeaders()
    val expectedHeaders = anyHeaders()
    val expectedParams = randomPairList()
    val expectedPath = randomString().removeSpaces()
    val expectedUrl = randomString().removeSpaces()
    val content = DummyHttpResponse()

    val mockEngine = MockEngine { request ->
      assertTrue { request.url.fullPath.contains(expectedUrl) }
      assertTrue { request.url.fullPath.contains(expectedPath) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedHeaders) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedGlobalHeaders) }
      assertTrue { request.url.parameters.flattenEntries().containsAll(expectedParams) }

      respond(
        content = ByteReadChannel("""{"name":"${content.name}"}"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val httpClient = HttpClient(engine = mockEngine)
    val datasource = GetNetworkDataSource(expectedUrl, httpClient, DummyHttpResponse.serializer(), anyJson(), expectedGlobalHeaders)
    val query = NetworkQuery(
      method = NetworkQuery.Method.Get,
      path = expectedPath,
      headers = expectedHeaders,
      urlParams = expectedParams
    )

    val response = datasource.get(query)

    assertEquals(content, response)
  }

  @Test
  fun `should successfully execute a get network request with oauth`() = runTest {
    val expectedGlobalHeaders = anyHeaders()
    val expectedHeaders = anyHeaders()
    val expectedParams = randomPairList()
    val expectedPath = randomString().removeSpaces()
    val expectedUrl = randomString().removeSpaces()
    val expectedTokenType = randomString()
    val expectedAccessToken = randomString()
    val fakeToken = OAuthToken(expectedAccessToken, expectedTokenType, randomLong(), randomString(), emptyList())
    val content = DummyHttpResponse()
    val mockGetPasswordTokenInteractor = MockGetPasswordTokenInteractor(mocker)

    val mockEngine = MockEngine { request ->
      assertTrue { request.url.fullPath.contains(expectedUrl) }
      assertTrue { request.url.fullPath.contains(expectedPath) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedHeaders) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedGlobalHeaders) }
      assertTrue { request.url.parameters.flattenEntries().containsAll(expectedParams) }
      assertTrue { request.headers.contains("Authorization", "${fakeToken.tokenType} ${fakeToken.accessToken}") }

      respond(
        content = ByteReadChannel("""{"name":"${content.name}"}"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val httpClient = HttpClient(engine = mockEngine)
    val datasource = GetNetworkDataSource(
      url = expectedUrl,
      httpClient = httpClient,
      serializer = DummyHttpResponse.serializer(),
      json = anyJson(),
      globalHeaders = expectedGlobalHeaders,
    )
    val query = OAuthNetworkQuery(
      mockGetPasswordTokenInteractor,
      method = NetworkQuery.Method.Get,
      path = expectedPath,
      headers = expectedHeaders,
      urlParams = expectedParams
    )

    mocker.everySuspending { mockGetPasswordTokenInteractor.invoke(isAny()) } returns fakeToken
    val response = datasource.get(query)

    assertEquals(content, response)

    mocker.verifyWithSuspend { mockGetPasswordTokenInteractor.invoke(isAny()) }
  }

  @Test
  fun `should throw exception when execute a get network request with oauth`() = runTest {
    val content = DummyHttpResponse()
    val mockGetPasswordTokenInteractor = MockGetPasswordTokenInteractor(mocker)

    val mockEngine = MockEngine {
      respond(
        content = ByteReadChannel("""{"name":"${content.name}"}"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val httpClient = HttpClient(engine = mockEngine)
    val datasource = GetNetworkDataSource(
      url = randomString(),
      httpClient = httpClient,
      serializer = DummyHttpResponse.serializer(),
      json = anyJson(),
      globalHeaders = randomPairList(),
    )
    val query = OAuthNetworkQuery(
      mockGetPasswordTokenInteractor,
      method = NetworkQuery.Method.Get,
      path = randomString(),
      headers = randomPairList(),
      urlParams = randomPairList()
    )

    mocker.everySuspending { mockGetPasswordTokenInteractor.invoke(isAny()) } runs { throw AnyException() }

    assertFailsWith<AnyException> {
      datasource.get(query)
    }
  }

  @Test
  fun `should throw QueryNotSupportedException if query is invalid executing getAll function`() = runTest {
    val datasource = GetNetworkDataSource(anyUrl(), mockHttpClient(), anySerializer(), anyJson())

    assertFailsWith<QueryNotSupportedException> {
      datasource.getAll(anyQuery())
    }
  }

  @Test
  fun `should throw QueryNotSupportedException if NetworkQuery is not Method Get executing getAll function`() = runTest {
    val datasource = GetNetworkDataSource(anyUrl(), mockHttpClient(), anySerializer(), anyJson())

    val invalidQuery = NetworkQuery(NetworkQuery.Method.Delete, randomString())

    assertFailsWith<QueryNotSupportedException> {
      datasource.getAll(invalidQuery)
    }
  }

  @Test
  fun `should propagate the same exception as the mapper when an exception is thrown executing getAll function`() = runTest {
    val mockMapper = MockMapper<Exception, Exception>(mocker)
    val datasource = GetNetworkDataSource(anyUrl(), mockHttpClient(), anySerializer(), anyJson(), exceptionMapper = mockMapper)
    val invalidQuery = anyQuery()

    assertFailsWith<AnyException> {
      mocker.every { mockMapper.map(isAny()) } returns AnyException()
      datasource.getAll(invalidQuery)
    }
  }

  @Test
  fun `should successfully execute a getAll network request`() = runTest {
    val expectedGlobalHeaders = anyHeaders()
    val expectedHeaders = anyHeaders()
    val expectedParams = randomPairList()
    val expectedPath = randomString().removeSpaces()
    val expectedUrl = randomString().removeSpaces()
    val content = listOf(DummyHttpResponse())

    val mockEngine = MockEngine { request ->
      assertTrue { request.url.fullPath.contains(expectedUrl) }
      assertTrue { request.url.fullPath.contains(expectedPath) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedHeaders) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedGlobalHeaders) }
      assertTrue { request.url.parameters.flattenEntries().containsAll(expectedParams) }

      respond(
        content = ByteReadChannel("""[{"name":"${content.first().name}"}]"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val httpClient = HttpClient(engine = mockEngine)
    val datasource = GetNetworkDataSource(expectedUrl, httpClient, DummyHttpResponse.serializer(), anyJson(), expectedGlobalHeaders)
    val query = NetworkQuery(
      method = NetworkQuery.Method.Get,
      path = expectedPath,
      headers = expectedHeaders,
      urlParams = expectedParams
    )

    val response = datasource.getAll(query)

    assertContentEquals(content, response)
  }

  @Test
  fun `should successfully execute a getAll network request with oauth`() = runTest {
    val expectedGlobalHeaders = anyHeaders()
    val expectedHeaders = anyHeaders()
    val expectedParams = randomPairList()
    val expectedPath = randomString().removeSpaces()
    val expectedUrl = randomString().removeSpaces()
    val expectedTokenType = randomString()
    val expectedAccessToken = randomString()
    val fakeToken = OAuthToken(expectedAccessToken, expectedTokenType, randomLong(), randomString(), emptyList())
    val content = listOf(DummyHttpResponse())
    val mockGetPasswordTokenInteractor = MockGetPasswordTokenInteractor(mocker)

    val mockEngine = MockEngine { request ->
      assertTrue { request.url.fullPath.contains(expectedUrl) }
      assertTrue { request.url.fullPath.contains(expectedPath) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedHeaders) }
      assertTrue { request.headers.flattenEntries().containsAll(expectedGlobalHeaders) }
      assertTrue { request.url.parameters.flattenEntries().containsAll(expectedParams) }
      assertTrue { request.headers.contains("Authorization", "${fakeToken.tokenType} ${fakeToken.accessToken}") }

      respond(
        content = ByteReadChannel("""[{"name":"${content.first().name}"}]"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val httpClient = HttpClient(engine = mockEngine)
    val datasource = GetNetworkDataSource(
      url = expectedUrl,
      httpClient = httpClient,
      serializer = DummyHttpResponse.serializer(),
      json = anyJson(),
      globalHeaders = expectedGlobalHeaders,
    )
    val query = OAuthNetworkQuery(
      mockGetPasswordTokenInteractor,
      method = NetworkQuery.Method.Get,
      path = expectedPath,
      headers = expectedHeaders,
      urlParams = expectedParams
    )

    mocker.everySuspending { mockGetPasswordTokenInteractor.invoke(isAny()) } returns fakeToken
    val response = datasource.getAll(query)

    assertContentEquals(content, response)

    mocker.verifyWithSuspend { mockGetPasswordTokenInteractor.invoke(isAny()) }
  }

  @Test
  fun `should throw exception when execute a getAll network request with oauth`() = runTest {
    val mockGetPasswordTokenInteractor = MockGetPasswordTokenInteractor(mocker)

    val mockEngine = MockEngine {
      respond(
        content = ByteReadChannel("""{"name":"${randomString()}"}"""),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
      )
    }

    val httpClient = HttpClient(engine = mockEngine)
    val datasource = GetNetworkDataSource(
      url = randomString(),
      httpClient = httpClient,
      serializer = DummyHttpResponse.serializer(),
      json = anyJson(),
      globalHeaders = randomPairList(),
    )
    val query = OAuthNetworkQuery(
      mockGetPasswordTokenInteractor,
      method = NetworkQuery.Method.Get,
      path = randomString(),
      headers = randomPairList(),
      urlParams = randomPairList()
    )

    mocker.everySuspending { mockGetPasswordTokenInteractor.invoke(isAny()) } runs { throw AnyException() }

    assertFailsWith<AnyException> {
      datasource.getAll(query)
    }
  }

  private fun anyUrl() = randomString()

  private fun anyJson() = Json

  private fun mockHttpClient() = ApiMock().client

  private fun anySerializer() = String.serializer()

  private fun anyHeaders() = randomPairList()

  private class AnyException : Exception()

  @kotlinx.serialization.Serializable
  private data class DummyHttpResponse(val name: String = randomString())
}
