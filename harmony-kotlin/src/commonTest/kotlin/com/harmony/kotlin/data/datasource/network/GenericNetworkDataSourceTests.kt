package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.error.QueryNotSupportedException
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.MockMapper
import com.harmony.kotlin.data.query.anyQuery
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@UsesMocks(Mapper::class)
class GenericNetworkDataSourceTests : BaseTest() {

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
  fun `should propagate exception if request throw exception`() = runTest {
    val mockMapper = MockMapper<Exception, Exception>(mocker)
    val datasource = GetNetworkDataSource(anyUrl(), mockHttpClient(), anySerializer(), anyJson(), exceptionMapper = mockMapper)
    val invalidQuery = anyQuery()

    assertFailsWith<AnyException> {
      mocker.every { mockMapper.map(isAny()) } returns AnyException()
      datasource.get(invalidQuery)
    }
  }

  private fun anyUrl() = randomString()

  private fun anyJson() = Json

  private fun anySerializer() = String.serializer()

  private fun mockHttpClient() = ApiMock().client

  private class AnyException : Exception()
}
