package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomIntList
import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.MockDeleteDataSource
import com.harmony.kotlin.data.datasource.MockGetDataSource
import com.harmony.kotlin.data.datasource.MockPutDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.datasource.anyVoidDataSource
import com.harmony.kotlin.data.operation.anyOperation
import com.harmony.kotlin.data.query.anyQuery
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@UsesMocks(GetDataSource::class, PutDataSource::class, DeleteDataSource::class)
class SingleDataSourceRepositoryTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTest() {
    mocker.reset()
  }

  @Test
  fun `should successfully execute get function`() = runTest {
    val getDataSource = MockGetDataSource<Int>(mocker)
    val expectedValue = randomInt()
    val expectedQuery = anyQuery()

    val repository = SingleDataSourceRepository(getDataSource, anyVoidDataSource(), anyVoidDataSource<Int>())
    mocker.everySuspending { getDataSource.get(expectedQuery) } returns expectedValue

    val result = repository.get(expectedQuery, anyOperation())

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      getDataSource.get(isSame(expectedQuery))
    }
  }

  @Test
  fun `should successfully execute getAll function`() = runTest {
    val getDataSource = MockGetDataSource<Int>(mocker)
    val expectedValues = randomIntList()
    val expectedQuery = anyQuery()

    val repository = SingleDataSourceRepository(getDataSource, anyVoidDataSource(), anyVoidDataSource<Int>())
    mocker.everySuspending { getDataSource.getAll(expectedQuery) } returns expectedValues

    val result = repository.getAll(expectedQuery, anyOperation())

    assertContentEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      getDataSource.getAll(isSame(expectedQuery))
    }
  }

  @Test
  fun `should successfully execute put function`() = runTest {
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()
    val putDataSource = MockPutDataSource<Int>(mocker)

    val repository = SingleDataSourceRepository(anyVoidDataSource(), putDataSource, anyVoidDataSource<Int>())
    mocker.everySuspending { putDataSource.put(expectedQuery, expectedValue) } returns expectedValue

    val result = repository.put(expectedQuery, expectedValue, anyOperation())

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      putDataSource.put(isSame(expectedQuery), isEqual(expectedValue))
    }
  }

  @Test
  fun `should successfully execute putAll function`() = runTest {
    val expectedQuery = anyQuery()
    val expectedValues = randomIntList()
    val putDataSource = MockPutDataSource<Int>(mocker)

    val repository = SingleDataSourceRepository(anyVoidDataSource(), putDataSource, anyVoidDataSource<Int>())
    mocker.everySuspending { putDataSource.putAll(expectedQuery, expectedValues) } returns expectedValues

    val result = repository.putAll(expectedQuery, expectedValues, anyOperation())

    assertContentEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      putDataSource.putAll(expectedQuery, expectedValues)
    }
  }

  @Test
  fun `should successfully execute delete function`() = runTest {
    val expectedQuery = anyQuery()
    val deleteDataSource = MockDeleteDataSource(mocker)

    val repository = SingleDataSourceRepository(anyVoidDataSource(), anyVoidDataSource<Int>(), deleteDataSource)
    mocker.everySuspending { deleteDataSource.delete(expectedQuery) } returns Unit

    repository.delete(expectedQuery)

    mocker.verifyWithSuspend {
      deleteDataSource.delete(isSame(expectedQuery))
    }
  }
}
