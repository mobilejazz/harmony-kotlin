package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomIntList
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.MockMapper
import com.harmony.kotlin.data.mapper.VoidMapper
import com.harmony.kotlin.data.mapper.anyVoidMapper
import com.harmony.kotlin.data.query.anyQuery
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@UsesMocks(GetDataSource::class, PutDataSource::class, DeleteDataSource::class, Mapper::class)
class DataSourceMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should map get operations`() = runTest {
    val getRepository = MockGetDataSource<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val expectedQuery = anyQuery()
    val anyString = randomString()
    val expectedValue = randomInt()

    val repository = DataSourceMapper(getRepository, anyVoidDataSource(), anyVoidDataSource<Int>(), toOutMapper, VoidMapper())
    mocker.everySuspending { getRepository.get(expectedQuery) } returns anyString
    mocker.every { toOutMapper.map(anyString) } returns expectedValue

    val result = repository.get(expectedQuery)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      getRepository.get(expectedQuery)
      toOutMapper.map(anyString)
    }
  }

  @Test
  fun `should map getAll operations`() = runTest {
    val getRepository = MockGetDataSource<String>(mocker)
    val toOutMapper = StringToIntMapper()
    val expectedQuery = anyQuery()
    val expectedValues = randomIntList()
    val anyStrings = expectedValues.map { it.toString() }

    val repository = DataSourceMapper(getRepository, anyVoidDataSource(), anyVoidDataSource<Int>(), toOutMapper, VoidMapper())
    mocker.everySuspending { getRepository.getAll(expectedQuery) } returns anyStrings

    val result = repository.getAll(expectedQuery)

    assertContentEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      getRepository.getAll(expectedQuery)
    }
  }

  @Test
  fun `should map put operations`() = runTest {
    val putRepository = MockPutDataSource<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()
    val expectedValueString = expectedValue.toString()

    val repository = DataSourceMapper(anyVoidDataSource(), putRepository, anyVoidDataSource<Int>(), toOutMapper, toInMapper)
    mocker.every { toInMapper.map(expectedValue) } returns expectedValueString
    mocker.everySuspending { putRepository.put(expectedQuery, expectedValueString) } returns expectedValueString
    mocker.every { toOutMapper.map(expectedValueString) } returns expectedValue

    val result = repository.put(expectedQuery, expectedValue)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      toInMapper.map(expectedValue)
      putRepository.put(expectedQuery, expectedValueString)
      toOutMapper.map(expectedValueString)
    }
  }

  @Test
  fun `should map putAll operations`() = runTest {
    val putRepository = MockPutDataSource<String>(mocker)
    val toOutMapper = StringToIntMapper()
    val toInMapper = IntToStringMapper()
    val expectedQuery = anyQuery()
    val expectedValues = randomIntList()
    val expectedValuesString = expectedValues.map { it.toString() }

    val repository = DataSourceMapper(anyVoidDataSource(), putRepository, anyVoidDataSource<Int>(), toOutMapper, toInMapper)
    mocker.everySuspending { putRepository.putAll(expectedQuery, expectedValuesString) } returns expectedValuesString

    val result = repository.putAll(expectedQuery, expectedValues)

    assertEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      putRepository.putAll(expectedQuery, expectedValuesString)
    }
  }

  @Test
  fun `should map delete operations`() = runTest {
    val deleteRepository = MockDeleteDataSource(mocker)
    val expectedQuery = anyQuery()

    val repository = DataSourceMapper(anyVoidDataSource(), anyVoidDataSource<String>(), deleteRepository, anyVoidMapper(), anyVoidMapper())
    mocker.everySuspending { deleteRepository.delete(expectedQuery) } returns Unit

    repository.delete(expectedQuery)

    mocker.verifyWithSuspend { deleteRepository.delete(expectedQuery) }
  }
}

@UsesMocks(GetDataSource::class, Mapper::class)
class GetDataSourceMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should map get operations`() = runTest {
    val getRepository = MockGetDataSource<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val expectedQuery = anyQuery()
    val anyString = randomString()
    val expectedValue = randomInt()

    val repository = GetDataSourceMapper(getRepository, toOutMapper)
    mocker.everySuspending { getRepository.get(expectedQuery) } returns anyString
    mocker.every { toOutMapper.map(anyString) } returns expectedValue

    val result = repository.get(expectedQuery)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      getRepository.get(expectedQuery)
      toOutMapper.map(anyString)
    }
  }

  @Test
  fun `should map getAll operations`() = runTest {
    val getRepository = MockGetDataSource<String>(mocker)
    val toOutMapper = StringToIntMapper()
    val expectedQuery = anyQuery()
    val expectedValues = randomIntList()
    val anyStrings = expectedValues.map { it.toString() }

    val repository = GetDataSourceMapper(getRepository, toOutMapper)
    mocker.everySuspending { getRepository.getAll(expectedQuery) } returns anyStrings

    val result = repository.getAll(expectedQuery)

    assertContentEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      getRepository.getAll(expectedQuery)
    }
  }
}

@UsesMocks(PutDataSource::class, Mapper::class)
class PutDataSourceMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should map put operations`() = runTest {
    val putRepository = MockPutDataSource<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()
    val expectedValueString = expectedValue.toString()

    val repository = PutDataSourceMapper(putRepository, toOutMapper, toInMapper)
    mocker.every { toInMapper.map(expectedValue) } returns expectedValueString
    mocker.everySuspending { putRepository.put(expectedQuery, expectedValueString) } returns expectedValueString
    mocker.every { toOutMapper.map(expectedValueString) } returns expectedValue

    val result = repository.put(expectedQuery, expectedValue)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      toInMapper.map(expectedValue)
      putRepository.put(expectedQuery, expectedValueString)
      toOutMapper.map(expectedValueString)
    }
  }

  @Test
  fun `should map putAll operations`() = runTest {
    val putRepository = MockPutDataSource<String>(mocker)
    val toOutMapper = StringToIntMapper()
    val toInMapper = IntToStringMapper()
    val expectedQuery = anyQuery()
    val expectedValues = randomIntList()
    val expectedValuesString = expectedValues.map { it.toString() }

    val repository = PutDataSourceMapper(putRepository, toOutMapper, toInMapper)
    mocker.everySuspending { putRepository.putAll(expectedQuery, expectedValuesString) } returns expectedValuesString

    val result = repository.putAll(expectedQuery, expectedValues)

    assertEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      putRepository.putAll(expectedQuery, expectedValuesString)
    }
  }
}

private class StringToIntMapper : Mapper<String, Int> {
  override fun map(from: String): Int = from.toInt()
}

private class IntToStringMapper : Mapper<Int, String> {
  override fun map(from: Int): String = from.toString()
}
