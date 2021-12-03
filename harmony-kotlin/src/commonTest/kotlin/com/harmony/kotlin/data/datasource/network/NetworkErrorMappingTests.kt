@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.data.datasource.network.error.HttpException
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.UnauthorizedException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

class NetworkErrorMappingTests : BaseTest() {

  private val apiMock: ApiMock = ApiMock()

  @Test
  fun `should throw UnauthorizedException when backend returns 401`() {
    assertFailsWith<UnauthorizedException> {
      runTest {
        apiMock.executeRequest(UnauthorizedRequest)
      }
    }
  }

  @Test
  fun `should throw DataNotFound when backend returns 404`() {
    assertFailsWith<DataNotFoundException> {
      runTest {
        apiMock.executeRequest(NotFoundRequest)
      }
    }
  }

  @Test
  fun `should throw HttpException when backend returns any 40X (minus 401 & 404) & 50X`() {
    try {
      runTest {
        apiMock.executeRequest(BadRequest)
      }
    } catch (e: HttpException) {
      assertEquals(e.statusCode, BadRequest.statusCode.value)
      assertEquals(e.response, BadRequest.responseBody)
    } catch (e: Exception) {
      fail()
    }
  }
}
