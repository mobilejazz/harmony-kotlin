package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomLong
import com.harmony.kotlin.common.randomNullable
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
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.flattenEntries
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
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
class PutNetworkDataSourceTest : BaseTest() {
  @Serializable
  data class Dummy(val key: String)

  private fun anyObject() = Dummy(randomString())
  private val baseUrl = "https://${randomString().removeSpaces()}"
  private val pathUrl = randomString().removeSpaces()
  private val objectToSend = anyObject()
  private val objectToSendAsJson = "{\"key\":\"${objectToSend.key}\"}"
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
    val putNetworkDataSource = givenPutNetworkDataSource()

    assertFailsWith<QueryNotSupportedException> {
      putNetworkDataSource.put(anyQuery(), randomNullable { anyObject() })
    }
  }

  @Test
  fun `should fail when query method is DELETE`() = runTest {
    val putNetworkDataSource = givenPutNetworkDataSource()

    val noPutQuery = NetworkQuery(NetworkQuery.Method.Delete, randomString())

    assertFailsWith<QueryNotSupportedException> {
      putNetworkDataSource.put(noPutQuery, randomNullable { anyObject() })
    }
  }

  @Test
  fun `should fail when query method is GET`() = runTest {
    val putNetworkDataSource = givenPutNetworkDataSource()

    val noPutQuery = NetworkQuery(NetworkQuery.Method.Get, randomString())

    assertFailsWith<QueryNotSupportedException> {
      putNetworkDataSource.put(noPutQuery, randomNullable { anyObject() })
    }
  }

  @Test
  fun `should propagate mapped exception when an exception is thrown`() = runTest {
    mocker.every { mockMapper.map(isAny()) } returns AnyException()
    val putNetworkDataSource = givenPutNetworkDataSource(exceptionMapper = mockMapper)

    val invalidQuery = anyQuery()

    assertFailsWith<AnyException> {
      putNetworkDataSource.put(invalidQuery, randomNullable { anyObject() })
    }
  }

  @Test
  fun `should fail when query method is PUT with content type and value provided`() = runTest {
    val putNetworkDataSource = givenPutNetworkDataSource()

    val invalidQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.Json(anyObject())),
      randomString()
    )

    assertFailsWith<IllegalArgumentException> {
      putNetworkDataSource.put(invalidQuery, anyObject())
    }
  }

  @Test
  fun `should fail when query method is POST with content type and value provided`() = runTest {
    val putNetworkDataSource = givenPutNetworkDataSource()

    val invalidQuery = NetworkQuery(
      NetworkQuery.Method.Post(NetworkQuery.ContentType.FormUrlEncoded(emptyList())),
      randomString()
    )

    assertFailsWith<IllegalArgumentException> {
      putNetworkDataSource.put(invalidQuery, anyObject())
    }
  }

  @Test
  fun `should send PUT request as json`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.Json(objectToSend)),
      pathUrl
    )
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine)

    putNetworkDataSource.put(contentTypeQuery, null)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals("application/json", body.contentType.toString())
      assertTrue(body is TextContent)
    }
  }

  @Test
  fun `should send PUT request as form data`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.FormUrlEncoded(randomPairList())),
      pathUrl
    )
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine)

    putNetworkDataSource.put(contentTypeQuery, null)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals("application/x-www-form-urlencoded; charset=UTF-8", body.contentType.toString())
      assertTrue(body is FormDataContent)
    }
  }

  @Test
  fun `should send PUT request and get same object that was sent`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(), pathUrl
    )
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine)

    val putResult = putNetworkDataSource.put(contentTypeQuery, objectToSend)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals(HttpMethod.Put, method)
    }
    assertEquals(objectToSend, putResult)
  }

  @Test
  fun `should send PUT request and get Unit when using Unit serializer`() = runTest {
    val mockEngine = mockEngine(response = "") {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.Json(objectToSend)),
      pathUrl
    )
    val putNetworkDataSource = PutNetworkDataSource(
      baseUrl, mockHttpClient(mockEngine), Unit.serializer(), Json, emptyList()
    )

    val putResult = putNetworkDataSource.put(contentTypeQuery, null)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals(HttpMethod.Put, method)
    }
    assertEquals(Unit, putResult)
  }

  @Test
  fun `should send PUT request and get Unit when using IgnoreNetworkResponseDecoder`() = runTest {
    val mockEngine = mockEngine(response = "") {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.Json(objectToSend)),
      pathUrl
    )
    val putNetworkDataSource = PutNetworkDataSource(
      baseUrl, mockHttpClient(mockEngine), IgnoreNetworkResponseDecoder(), emptyList()
    )

    val putResult = putNetworkDataSource.put(contentTypeQuery, null)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals(HttpMethod.Put, method)
    }
    assertEquals(Unit, putResult)
  }

  @Test
  fun `should send PUT request and get Unit list when using Unit serializer for put`() = runTest {
    val mockEngine = mockEngine(response = "") {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.Json(listOf(objectToSend))),
      pathUrl
    )
    val putNetworkDataSource = PutNetworkDataSource(
      baseUrl, mockHttpClient(mockEngine), Unit.serializer(), Json, emptyList()
    )

    val putResult = putNetworkDataSource.put(contentTypeQuery, null)

    assertEquals(Unit, putResult)
  }

  @Test
  fun `should send PUT request and get Unit list when using IgnoreNetworkResponseDecoder for put`() = runTest {
    val mockEngine = mockEngine(response = "") {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.Json(listOf(objectToSend))),
      pathUrl
    )
    val putNetworkDataSource = PutNetworkDataSource(
      baseUrl, mockHttpClient(mockEngine), IgnoreNetworkResponseDecoder(), emptyList()
    )

    val putResult = putNetworkDataSource.put(contentTypeQuery, null)

    assertEquals(Unit, putResult)
  }

  @Test
  fun `should send PUT request with content type and get same object that was sent`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(NetworkQuery.ContentType.Json(objectToSend)), pathUrl
    )
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine)

    val putResult = putNetworkDataSource.put(contentTypeQuery, null)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals("application/json", body.contentType.toString())
      assertEquals(HttpMethod.Put, method)
    }
    assertEquals(objectToSend, putResult)
  }

  @Test
  fun `should send POST request and get same object that was sent`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Post(), pathUrl
    )
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine)

    val putResult = putNetworkDataSource.put(contentTypeQuery, objectToSend)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals("application/json", body.contentType.toString())
      assertEquals(HttpMethod.Post, method)
    }
    assertEquals(objectToSend, putResult)
  }

  @Test
  fun `should send PUT request and get same object that was sent with oauth`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val expectedTokenType = randomString()
    val expectedAccessToken = randomString()
    val fakeToken = OAuthToken(expectedAccessToken, expectedTokenType, randomLong(), randomString(), emptyList())
    mocker.everySuspending { getPasswordTokenInteractor(isAny()) } returns fakeToken
    val contentTypeQuery = OAuthNetworkQuery(
      getPasswordTokenInteractor,
      NetworkQuery.Method.Put(), pathUrl
    )
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine)

    val putResult = putNetworkDataSource.put(contentTypeQuery, objectToSend)

    mocker.verifyWithSuspend { getPasswordTokenInteractor(isAny()) }
    assertEquals(objectToSend, putResult)
    with(requestSpy) {
      assertNotNull(this)
      assertTrue { headers.contains("Authorization", "${fakeToken.tokenType} ${fakeToken.accessToken}") }
    }
  }

  @Test
  fun `should send request to expected url`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(), pathUrl,
      listOf("query_string" to "whatever"),
    )
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine)

    putNetworkDataSource.put(contentTypeQuery, objectToSend)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals("$baseUrl/$pathUrl?query_string=whatever", url.toString())
    }
  }

  @Test
  fun `should send request with expected headers`() = runTest {
    val mockEngine = mockEngine(response = objectToSendAsJson) {
      requestSpy = it
    }
    val globalHeaders = anyHeaders()
    val queryHeaders = anyHeaders()
    val suspendHeaders = anyHeaders()
    val putNetworkDataSource = givenPutNetworkDataSource(mockEngine, globalHeaders)
    val contentTypeQuery = NetworkQuery(
      NetworkQuery.Method.Put(), pathUrl,
      headers = queryHeaders,
      suspendHeaders = { suspendHeaders }
    )

    putNetworkDataSource.put(contentTypeQuery, objectToSend)

    with(requestSpy) {
      assertNotNull(this)
      assertEquals("application/json", body.contentType.toString())
      assertTrue { headers.flattenEntries().containsAll(globalHeaders) }
      assertTrue { headers.flattenEntries().containsAll(queryHeaders) }
      assertTrue { headers.flattenEntries().containsAll(suspendHeaders) }
    }
  }

  @Test
  fun `should throw exception when execute a auth interactor fails`() = runTest {
    val mockEngine = mockEngine(response = "")
    val datasource = givenPutNetworkDataSource(mockEngine)
    val contentTypeQuery = OAuthNetworkQuery(
      getPasswordTokenInteractor,
      NetworkQuery.Method.Put(), pathUrl
    )
    mocker.everySuspending { getPasswordTokenInteractor(isAny()) } runs { throw AnyException() }

    assertFailsWith<AnyException> {
      datasource.put(contentTypeQuery, null)
    }
  }

  private fun mockHttpClient(
    mockEngine: MockEngine = MockEngine {
      respondOk("{\"key\":\"${randomString()}\"}")
    }
  ) = HttpClient(mockEngine) {
    install(ContentNegotiation) {
      json(
        Json {
          isLenient = false
          ignoreUnknownKeys = true
        }
      )
    }
  }

  private fun anyHeaders() = randomPairList()

  private class AnyException : Exception()

  private fun mockEngine(
    response: String,
    responseCode: HttpStatusCode = HttpStatusCode.OK,
    requestDataSpy: (HttpRequestData) -> Unit = { },
  ): MockEngine = MockEngine { requestData ->
    requestDataSpy(requestData)
    respond(response, responseCode)
  }

  private fun givenPutNetworkDataSource(
    mockEngine: MockEngine = MockEngine {
      respondOk("{\"key\":\"${randomString()}\"}")
    },
    globalHeaders: List<Pair<String, String>> = emptyList(),
    exceptionMapper: Mapper<Exception, Exception> = IdentityMapper()
  ) = PutNetworkDataSource(
    baseUrl, mockHttpClient(mockEngine), SerializedNetworkResponseDecoder(Json, Dummy.serializer()), globalHeaders, exceptionMapper
  )
}
