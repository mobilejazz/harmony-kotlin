package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomIntList
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.MockMapper
import com.harmony.kotlin.data.query.anyQuery
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@UsesMocks(GetDataSource::class, PutDataSource::class, Mapper::class)
class SerializationDataSourceMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should map get operations`() = runTest {
    val getRepository = MockGetDataSource<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toOutListMapper = MockMapper<String, List<Int>>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val toInListMapper = MockMapper<List<Int>, String>(mocker)
    val expectedQuery = anyQuery()
    val anyString = randomString()
    val expectedValue = randomInt()

    val repository = SerializationDataSourceMapper(
      getRepository,
      anyVoidDataSource(),
      toOutMapper,
      toOutListMapper,
      toInMapper,
      toInListMapper
    )

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
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toOutListMapper = MockMapper<String, List<Int>>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val toInListMapper = MockMapper<List<Int>, String>(mocker)
    val expectedQuery = anyQuery()
    val expectedValues = randomIntList()
    val expectedValueString = randomString()

    val repository = SerializationDataSourceMapper(getRepository, anyVoidDataSource(), toOutMapper, toOutListMapper, toInMapper, toInListMapper)
    mocker.everySuspending { getRepository.get(expectedQuery) } returns expectedValueString
    mocker.every { toOutListMapper.map(expectedValueString) } returns expectedValues

    val result = repository.getAll(expectedQuery)

    assertContentEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      getRepository.get(expectedQuery)
      toOutListMapper.map(expectedValueString)
    }
  }

  @Test
  fun `should map put operations`() = runTest {
    val putRepository = MockPutDataSource<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toOutListMapper = MockMapper<String, List<Int>>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val toInListMapper = MockMapper<List<Int>, String>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()
    val expectedValueString = expectedValue.toString()

    val repository = SerializationDataSourceMapper(anyVoidDataSource(), putRepository, toOutMapper, toOutListMapper, toInMapper, toInListMapper)
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
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toOutListMapper = MockMapper<String, List<Int>>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val toInListMapper = MockMapper<List<Int>, String>(mocker)
    val expectedQuery = anyQuery()
    val expectedValues = randomIntList()
    val expectedValueString = randomString()

    val repository = SerializationDataSourceMapper(anyVoidDataSource(), putRepository, toOutMapper, toOutListMapper, toInMapper, toInListMapper)
    mocker.everySuspending { putRepository.put(expectedQuery, expectedValueString) } returns expectedValueString
    mocker.every { toInListMapper.map(expectedValues) } returns expectedValueString
    mocker.every { toOutListMapper.map(expectedValueString) } returns expectedValues

    val result = repository.putAll(expectedQuery, expectedValues)

    assertEquals(expectedValues, result)
    mocker.verifyWithSuspend {
      toInListMapper.map(expectedValues)
      putRepository.put(expectedQuery, expectedValueString)
      toOutListMapper.map(expectedValueString)
    }
  }
}
