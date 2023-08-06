package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomLong
import com.harmony.kotlin.common.randomPairList
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.common.removeSpaces
import com.harmony.kotlin.data.mapper.IdentityMapper
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
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.util.flattenEntries
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@UsesMocks(Mapper::class, GetPasswordTokenInteractor::class)
class GetNetworkDataSourceTests : BaseTest() {
  private val baseUrl = randomString().removeSpaces()
  private val content = DummyHttpResponse()
  private val contentList = listOf(DummyHttpResponse(), DummyHttpResponse())
  private var requestSpy: HttpRequestData? = null

  private val mocker = Mocker()
  private lateinit var mockGetPasswordTokenInteractor: GetPasswordTokenInteractor
  private lateinit var mockMapper: MockMapper<Exception, Exception>

  @BeforeTest
  fun beforeTest() {
    mocker.reset()
    mockGetPasswordTokenInteractor = MockGetPasswordTokenInteractor(mocker)
    mockMapper = MockMapper(mocker)
    requestSpy = null
  }

  @Test
  fun `should fail when query is invalid`() = runTest {
    val getNetworkDataSource = givenGetNetworkDataSource()

    assertFailsWith<QueryNotSupportedException> {
      getNetworkDataSource.get(anyQuery())
    }
  }

  @Test
  fun `should fail when method is not Get`() = runTest {
    val getNetworkDataSource = givenGetNetworkDataSource()

    val invalidQuery = NetworkQuery(NetworkQuery.Method.Delete, randomString())

    assertFailsWith<QueryNotSupportedException> {
      getNetworkDataSource.get(invalidQuery)
    }
  }

  @Test
  fun `should propagate the same exception as the mapper when an exception is thrown`() = runTest {
    val getNetworkDataSource = givenGetNetworkDataSource(exceptionMapper = mockMapper)
    val invalidQuery = anyQuery()
    mocker.every { mockMapper.map(isAny()) } returns AnyException()

    assertFailsWith<AnyException> {
      getNetworkDataSource.get(invalidQuery)
    }
  }

  @Test
  fun `should successfully execute a get network request`() = runTest {
    val expectedGlobalHeaders = anyHeaders()
    val expectedHeaders = anyHeaders()
    val expectedParams = randomPairList()
    val expectedPath = randomString().removeSpaces()
    val mockEngine = mockEngine(response = """{"name":"${content.name}"}""") {
      requestSpy = it
    }
    val getNetworkDataSource = givenGetNetworkDataSource(mockEngine, expectedGlobalHeaders)
    val query = NetworkQuery(
      method = NetworkQuery.Method.Get,
      path = expectedPath,
      headers = expectedHeaders,
      urlParams = expectedParams
    )

    val response = getNetworkDataSource.get(query)

    assertEquals(content, response)
    with(requestSpy) {
      assertNotNull(this)
      assertTrue { url.fullPath.contains(baseUrl) }
      assertTrue { url.fullPath.contains(expectedPath) }
      assertTrue { headers.flattenEntries().containsAll(expectedHeaders) }
      assertTrue { headers.flattenEntries().containsAll(expectedGlobalHeaders) }
      assertTrue { url.parameters.flattenEntries().containsAll(expectedParams) }
    }
  }

  @Test
  fun `should successfully execute a get network request on a list`() = runTest {
    val expectedGlobalHeaders = anyHeaders()
    val expectedHeaders = anyHeaders()
    val expectedParams = randomPairList()
    val expectedPath = randomString().removeSpaces()
    val mockEngine = mockEngine(response = """[{"name":"${contentList.first().name}"},{"name":"${contentList.last().name}"}]""") {
      requestSpy = it
    }
    val getNetworkDataSource = givenGetNetworkDataSource(
      mockEngine, expectedGlobalHeaders,
      serializer = ListSerializer
      (DummyHttpResponse.serializer())
    )
    val query = NetworkQuery(
      method = NetworkQuery.Method.Get,
      path = expectedPath,
      headers = expectedHeaders,
      urlParams = expectedParams
    )

    val response = getNetworkDataSource.get(query)

    assertEquals(contentList, response)
    with(requestSpy) {
      assertNotNull(this)
      assertTrue { url.fullPath.contains(baseUrl) }
      assertTrue { url.fullPath.contains(expectedPath) }
      assertTrue { headers.flattenEntries().containsAll(expectedHeaders) }
      assertTrue { headers.flattenEntries().containsAll(expectedGlobalHeaders) }
      assertTrue { url.parameters.flattenEntries().containsAll(expectedParams) }
    }
  }

  @Test
  fun `should successfully execute a get network request with oauth`() = runTest {
    val expectedGlobalHeaders = anyHeaders()
    val expectedHeaders = anyHeaders()
    val expectedParams = randomPairList()
    val expectedPath = randomString().removeSpaces()
    val expectedTokenType = randomString()
    val expectedAccessToken = randomString()
    val fakeToken = OAuthToken(expectedAccessToken, expectedTokenType, randomLong(), randomString(), emptyList())
    val mockEngine = mockEngine(response = """{"name":"${content.name}"}""") {
      requestSpy = it
    }
    val datasource = givenGetNetworkDataSource(mockEngine, expectedGlobalHeaders)
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
    requestSpy!!.apply {
      assertTrue { url.fullPath.contains(baseUrl) }
      assertTrue { url.fullPath.contains(expectedPath) }
      assertTrue { headers.flattenEntries().containsAll(expectedHeaders) }
      assertTrue { headers.flattenEntries().containsAll(expectedGlobalHeaders) }
      assertTrue { url.parameters.flattenEntries().containsAll(expectedParams) }
      assertTrue { headers.contains("Authorization", "${fakeToken.tokenType} ${fakeToken.accessToken}") }
    }
  }

  @Test
  fun `should throw exception when execute a get network request with oauth`() = runTest {
    val mockEngine = mockEngine(response = "")
    val datasource = givenGetNetworkDataSource(mockEngine)
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
  fun `should throw illegal argument exception when  interactor fails`() = runTest {
    val mockEngine = mockEngine(response = """{"name":"${randomString()}"}""")
    val getNetworkDataSource = givenGetNetworkDataSource(mockEngine)
    val query = OAuthNetworkQuery(
      mockGetPasswordTokenInteractor,
      method = NetworkQuery.Method.Get,
      path = randomString(),
    )
    mocker.everySuspending { mockGetPasswordTokenInteractor.invoke(isAny()) } runs { throw AnyException() }

    assertFailsWith<AnyException> {
      getNetworkDataSource.get(query)
    }
  }

  private fun mockEngine(
    response: String,
    responseCode: HttpStatusCode = HttpStatusCode.OK,
    requestDataSpy: (HttpRequestData) -> Unit = { },
  ): MockEngine = MockEngine { requestData ->
    requestDataSpy(requestData)
    respond(response, responseCode)
  }

  private fun anyHeaders() = randomPairList()

  private class AnyException : Exception()

  @kotlinx.serialization.Serializable
  private data class DummyHttpResponse(val name: String = randomString())

  private fun givenGetNetworkDataSource(
    mockEngine: MockEngine = mockEngine(response = ""),
    globalHeaders: List<Pair<String, String>> = randomPairList(),
    exceptionMapper: Mapper<Exception, Exception> = IdentityMapper(),
    serializer: KSerializer<*> = DummyHttpResponse.serializer(),
  ) = GetNetworkDataSource(
    url = baseUrl,
    httpClient = HttpClient(engine = mockEngine),
    networkResponseDecoder = SerializedNetworkResponseDecoder(Json, serializer),
    globalHeaders = globalHeaders,
    exceptionMapper
  )
}
