package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.data.error.DataNotValidException
import com.harmony.kotlin.data.query.anyQuery
import com.harmony.kotlin.data.utilities.randomIntList
import com.harmony.kotlin.data.validator.MockValidator
import com.harmony.kotlin.data.validator.Validator
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@UsesMocks(GetDataSource::class, PutDataSource::class, DeleteDataSource::class, Validator::class)
class DataSourceValidatorTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should success when data is valid using get function`() = runTest {
    val getDataSource = MockGetDataSource<Int>(mocker)
    val validator = MockValidator<Int>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()

    val dataSource = GetDataSourceValidator(getDataSource, validator)
    mocker.everySuspending { getDataSource.get(expectedQuery) } returns expectedValue
    mocker.every { validator.isValid(expectedValue) } returns true

    val result = dataSource.get(expectedQuery)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      getDataSource.get(expectedQuery)
      validator.isValid(expectedValue)
    }
  }

  @Test
  fun `should throw DataNotValidException when is not valid using get funtion`() = runTest {
    val getDataSource = MockGetDataSource<Int>(mocker)
    val validator = MockValidator<Int>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()

    val dataSource = GetDataSourceValidator(getDataSource, validator)
    mocker.everySuspending { getDataSource.get(expectedQuery) } returns expectedValue
    mocker.every { validator.isValid(expectedValue) } returns false

    assertFailsWith<DataNotValidException> {
      dataSource.get(expectedQuery)
    }
    mocker.verifyWithSuspend {
      getDataSource.get(expectedQuery)
      validator.isValid(expectedValue)
    }
  }

  @Test
  fun `should success when data is valid using getAll function`() = runTest {
    val getDataSource = MockGetDataSource<Int>(mocker)
    val validator = MockValidator<Int>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomIntList()

    val dataSource = GetDataSourceValidator(getDataSource, validator)
    mocker.everySuspending { getDataSource.getAll(expectedQuery) } returns expectedValue
    expectedValue.forEach { mocker.every { validator.isValid(it) } returns true }
    mocker.every { validator.isValid(expectedValue) } returns true

    val result = dataSource.getAll(expectedQuery)

    assertContentEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      getDataSource.getAll(expectedQuery)
      validator.isValid(expectedValue)
    }
  }

  @Test
  fun `should throw DataNotValidException when is not valid using getAll funtion`() = runTest {
    val getDataSource = MockGetDataSource<Int>(mocker)
    val validator = MockValidator<Int>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomIntList()

    val dataSource = GetDataSourceValidator(getDataSource, validator)
    mocker.everySuspending { getDataSource.getAll(expectedQuery) } returns expectedValue
    mocker.every { validator.isValid(expectedValue) } returns false

    assertFailsWith<DataNotValidException> {
      dataSource.getAll(expectedQuery)
    }
    mocker.verifyWithSuspend {
      getDataSource.getAll(expectedQuery)
      validator.isValid(expectedValue)
    }
  }
}
