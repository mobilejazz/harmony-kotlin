@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.data.datasource.network.error.HttpException
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.UnauthorizedException
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NetworkErrorMappingTests : BaseTest() {

  private val apiMock: ApiMock = ApiMock()

  // Workaround: Using runBlocking instead of runBlockingTest (used by our runTest) because of this issue:
  // - https://github.com/Kotlin/kotlinx.coroutines/issues/1222
  @Test
  fun `should throw UnauthorizedException when backend returns 401`() {
    assertFailsWith<UnauthorizedException> {
      runBlocking {
        apiMock.executeRequest(UnauthorizedRequest)
      }
    }
  }

  @Test
  fun `should throw DataNotFound when backend returns 404`() {
    assertFailsWith<DataNotFoundException> {
      runBlocking {
        apiMock.executeRequest(NotFoundRequest)
      }
    }
  }

  @Test
  fun `should throw HttpException when backend returns any 40X minus 401 - 404 50X`() {
    val httpException = assertFailsWith<HttpException> {
      runBlocking {
        apiMock.executeRequest(BadRequest)
      }
    }
    assertEquals(httpException.statusCode, BadRequest.statusCode.value)
    assertEquals(httpException.response, BadRequest.responseBody)
  }
}
