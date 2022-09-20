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
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.utils.EmptyContent
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.util.flattenEntries
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@UsesMocks(Mapper::class, GetPasswordTokenInteractor::class)
class DeleteNetworkDataSourceTest : BaseTest() {
  private val baseUrl = "https://${randomString().removeSpaces()}"
  private val pathUrl = randomString().removeSpaces()
  private var requestSpy: HttpRequestData? = null

  private val mocker = Mocker()
  private lateinit var getPasswordTokenInteractor: GetPasswordTokenInteractor
  private lateinit var mockMapper: MockMapper<Exception, Exception>

  @BeforeTest
  fun beforeTest() {
    mocker.reset()
    getPasswordTokenInteractor = MockGetPasswordTokenInteractor(mocker)
    mockMapper = MockMapper(mocker)
  }

  @Test
  fun `should fail when unexpected query provided`() = runTest {
    val deleteNetworkDataSource = givenDeleteNetworkDataSource()

    assertFailsWith<QueryNotSupportedException> {
      deleteNetworkDataSource.delete(anyQuery())
    }
  }

  @Test
  fun `should fail when query method is NOT DELETE`() = runTest {
    val deleteNetworkDataSource = givenDeleteNetworkDataSource()

    val noDeleteQuery = NetworkQuery(NetworkQuery.Method.Get, randomString())

    assertFailsWith<QueryNotSupportedException> {
      deleteNetworkDataSource.delete(noDeleteQuery)
    }
  }

  @Test
  fun `should propagate mapped exception when an exception is thrown`() = runTest {
    mocker.every { mockMapper.map(isAny()) } returns AnyException()
    val deleteNetworkDataSource = givenDeleteNetworkDataSource(exceptionMapper = mockMapper)

    val invalidQuery = anyQuery()

    assertFailsWith<AnyException> {
      deleteNetworkDataSource.delete(invalidQuery)
    }
  }

  @Test
  fun `should send DELETE request`() = runTest {
    val mockEngine = mockEngine {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Delete,
      pathUrl
    )
    val deleteNetworkDataSource = givenDeleteNetworkDataSource(mockEngine)

    deleteNetworkDataSource.delete(contentTypeQuery)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals(HttpMethod.Delete, method)
      assertTrue(body is EmptyContent)
    }
  }

  @Test
  fun `should send request to expected url`() = runTest {
    val mockEngine = mockEngine {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Delete,
      pathUrl,
      listOf("query_string" to "whatever"),
    )
    val deleteNetworkDataSource = givenDeleteNetworkDataSource(mockEngine)

    deleteNetworkDataSource.delete(contentTypeQuery)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals("$baseUrl/$pathUrl?query_string=whatever", url.toString())
    }
  }

  @Test
  fun `should send request with expected headers`() = runTest {
    val mockEngine = mockEngine {
      requestSpy = it
    }
    val globalHeaders = anyHeaders()
    val queryHeaders = anyHeaders()
    val suspendHeaders = anyHeaders()
    val deleteNetworkDataSource = givenDeleteNetworkDataSource(mockEngine, globalHeaders)
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Delete, pathUrl,
      headers = queryHeaders,
      suspendHeaders = { suspendHeaders }
    )

    deleteNetworkDataSource.delete(contentTypeQuery)

    with(requestSpy) {
      assertNotNull(this)
      assertTrue { headers.flattenEntries().containsAll(globalHeaders) }
      assertTrue { headers.flattenEntries().containsAll(queryHeaders) }
      assertTrue { headers.flattenEntries().containsAll(suspendHeaders) }
    }
  }

  @Test
  fun `should send request with oauth`() = runTest {
    val mockEngine = mockEngine {
      requestSpy = it
    }
    val expectedTokenType = randomString()
    val expectedAccessToken = randomString()
    val fakeToken = OAuthToken(expectedAccessToken, expectedTokenType, randomLong(), randomString(), emptyList())
    mocker.everySuspending { getPasswordTokenInteractor(isAny()) } returns fakeToken
    val contentTypeQuery = OAuthNetworkQuery(
      getPasswordTokenInteractor,
      NetworkQuery.Method.Delete, pathUrl
    )
    val deleteNetworkDataSource = givenDeleteNetworkDataSource(mockEngine)

    deleteNetworkDataSource.delete(contentTypeQuery)

    mocker.verifyWithSuspend { getPasswordTokenInteractor(isAny()) }
    with(requestSpy) {
      assertNotNull(this)
      assertTrue { headers.contains("Authorization", "${fakeToken.tokenType} ${fakeToken.accessToken}") }
    }
  }

  @Test
  fun `should throw exception when auth interactor fails`() = runTest {
    val mockEngine = mockEngine()
    val deleteNetworkDataSource = givenDeleteNetworkDataSource(mockEngine)
    val query = OAuthNetworkQuery(
      getPasswordTokenInteractor,
      method = NetworkQuery.Method.Delete,
      path = randomString(),
    )
    mocker.everySuspending { getPasswordTokenInteractor.invoke(isAny()) } runs { throw AnyException() }

    assertFailsWith<AnyException> {
      deleteNetworkDataSource.delete(query)
    }
  }

  private fun mockHttpClient(
    mockEngine: MockEngine = MockEngine {
      respondOk()
    }
  ) = HttpClient(mockEngine) {
    install(ContentNegotiation)
  }

  private fun anyHeaders() = randomPairList()

  private class AnyException : Exception()

  private fun mockEngine(
    responseCode: HttpStatusCode = HttpStatusCode.OK,
    requestDataSpy: (HttpRequestData) -> Unit = { },
  ): MockEngine = MockEngine { requestData ->
    requestDataSpy(requestData)
    respond("", responseCode)
  }

  private fun givenDeleteNetworkDataSource(
    mockEngine: MockEngine = MockEngine {
      respondOk()
    },
    globalHeaders: List<Pair<String, String>> = emptyList(),
    exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()
  ): DeleteNetworkDataSource {
    return DeleteNetworkDataSource(
      baseUrl, mockHttpClient(mockEngine), globalHeaders, exceptionMapper
    )
  }
}
