package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.MockMapper
import com.harmony.kotlin.data.mapper.anyVoidMapper
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.error.DataNotFoundException
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@UsesMocks(GetDataSource::class, PutDataSource::class, DeleteDataSource::class, Mapper::class)
class DataSourceQueryMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `get method success`() = runTest {
    val getDataSource = MockGetDataSource<String>(mocker)
    val getQueryMapper = MockMapper<KeyQuery, KeyQuery>(mocker)

    val expectedQuery = KeyQuery("Input")
    val expectedQueryMapped = KeyQuery("Output")
    val expectedResult = randomString()

    val dataSourceQueryMapper = DataSourceQueryMapper(
      getDataSource,
      anyVoidDataSource(),
      anyVoidDataSource<Unit>(),
      getQueryMapper,
      anyVoidMapper(),
      anyVoidMapper()
    )

    mocker.every { getQueryMapper.map(expectedQuery) } returns expectedQueryMapped
    mocker.everySuspending { getDataSource.get(expectedQueryMapped) } returns expectedResult

    val result = dataSourceQueryMapper.get(expectedQuery)
    assertEquals(expectedResult, result)

    mocker.verifyWithSuspend {
      getQueryMapper.map(expectedQuery)
      getDataSource.get(expectedQueryMapped)
    }
  }

  @Test
  fun `get method error`() = runTest {
    val getDataSource = MockGetDataSource<String>(mocker)
    val getQueryMapper = MockMapper<KeyQuery, KeyQuery>(mocker)

    val expectedQuery = KeyQuery("Input")
    val expectedQueryMapped = KeyQuery("Output")

    val dataSourceQueryMapper = DataSourceQueryMapper(
      getDataSource,
      anyVoidDataSource(),
      anyVoidDataSource<Unit>(),
      getQueryMapper,
      anyVoidMapper(),
      anyVoidMapper()
    )

    assertFailsWith(DataNotFoundException::class) {
      mocker.every { getQueryMapper.map(expectedQuery) } returns expectedQueryMapped
      mocker.everySuspending { getDataSource.get(expectedQueryMapped) } runs { throw DataNotFoundException() }

      dataSourceQueryMapper.get(expectedQuery)

      mocker.verifyWithSuspend {
        getQueryMapper.map(expectedQuery)
        getDataSource.get(expectedQueryMapped)
      }
    }
  }

  @Test
  fun `put method success`() = runTest {
    val putDataSource = MockPutDataSource<String>(mocker)
    val putQueryMapper = MockMapper<KeyQuery, KeyQuery>(mocker)

    val expectedQuery = KeyQuery("Input")
    val expectedQueryMapped = KeyQuery("Output")
    val expectedValue = randomString()
    val expectedResult = randomString()

    val dataSourceQueryMapper = DataSourceQueryMapper(
      anyVoidDataSource(),
      putDataSource,
      anyVoidDataSource<Unit>(),
      anyVoidMapper(),
      putQueryMapper,
      anyVoidMapper()
    )

    mocker.every { putQueryMapper.map(expectedQuery) } returns expectedQueryMapped
    mocker.everySuspending { putDataSource.put(expectedQueryMapped, expectedValue) } returns expectedResult

    val result = dataSourceQueryMapper.put(expectedQuery, expectedValue)
    assertEquals(expectedResult, result)

    mocker.verifyWithSuspend {
      putQueryMapper.map(expectedQuery)
      putDataSource.put(expectedQueryMapped, expectedValue)
    }
  }

  @Test
  fun `put method error`() = runTest {
    val putDataSource = MockPutDataSource<String>(mocker)
    val putQueryMapper = MockMapper<KeyQuery, KeyQuery>(mocker)

    val expectedQuery = KeyQuery("Input")
    val expectedQueryMapped = KeyQuery("Output")
    val expectedValue = randomString()

    val dataSourceQueryMapper = DataSourceQueryMapper(
      anyVoidDataSource(),
      putDataSource,
      anyVoidDataSource<Unit>(),
      anyVoidMapper(),
      putQueryMapper,
      anyVoidMapper()
    )

    assertFailsWith(DataNotFoundException::class) {
      mocker.every { putQueryMapper.map(expectedQuery) } returns expectedQueryMapped
      mocker.everySuspending { putDataSource.put(expectedQueryMapped, expectedValue) } runs { throw DataNotFoundException() }

      dataSourceQueryMapper.put(expectedQuery, expectedValue)

      mocker.verifyWithSuspend {
        putQueryMapper.map(expectedQuery)
        putDataSource.put(expectedQueryMapped, expectedValue)
      }
    }
  }

  @Test
  fun `delete method success`() = runTest {
    val deleteDataSource = MockDeleteDataSource(mocker)
    val deleteQueryMapper = MockMapper<KeyQuery, KeyQuery>(mocker)

    val expectedQuery = KeyQuery("Input")
    val expectedQueryMapped = KeyQuery("Output")

    val dataSourceQueryMapper = DataSourceQueryMapper(
      anyVoidDataSource(),
      anyVoidDataSource<String>(),
      deleteDataSource,
      anyVoidMapper(),
      anyVoidMapper(),
      deleteQueryMapper,
    )

    mocker.every { deleteQueryMapper.map(expectedQuery) } returns expectedQueryMapped
    mocker.everySuspending { deleteDataSource.delete(expectedQueryMapped) } returns Unit

    dataSourceQueryMapper.delete(expectedQuery)

    mocker.verifyWithSuspend {
      deleteQueryMapper.map(expectedQuery)
      deleteDataSource.delete(expectedQueryMapped)
    }
  }

  @Test
  fun `delete method error`() = runTest {
    val deleteDataSource = MockDeleteDataSource(mocker)
    val deleteQueryMapper = MockMapper<KeyQuery, KeyQuery>(mocker)

    val expectedQuery = KeyQuery("Input")
    val expectedQueryMapped = KeyQuery("Output")

    val dataSourceQueryMapper = DataSourceQueryMapper(
      anyVoidDataSource(),
      anyVoidDataSource<Unit>(),
      deleteDataSource,
      anyVoidMapper(),
      anyVoidMapper(),
      deleteQueryMapper,
    )

    assertFailsWith(DataNotFoundException::class) {
      mocker.every { deleteQueryMapper.map(expectedQuery) } returns expectedQueryMapped
      mocker.everySuspending { deleteDataSource.delete(expectedQueryMapped) } runs { throw DataNotFoundException() }

      dataSourceQueryMapper.delete(expectedQuery)

      mocker.verifyWithSuspend {
        deleteQueryMapper.map(expectedQuery)
        deleteDataSource.delete(expectedQueryMapped)
      }
    }
  }
}
