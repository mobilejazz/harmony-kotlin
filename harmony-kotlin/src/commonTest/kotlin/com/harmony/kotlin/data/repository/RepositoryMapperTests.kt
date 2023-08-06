package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.mapper.Mapper
import com.harmony.kotlin.data.mapper.MockMapper
import com.harmony.kotlin.data.mapper.VoidMapper
import com.harmony.kotlin.data.mapper.anyVoidMapper
import com.harmony.kotlin.data.operation.anyOperation
import com.harmony.kotlin.data.query.anyQuery
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@UsesMocks(GetRepository::class, PutRepository::class, DeleteRepository::class, Mapper::class)
class RepositoryMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should map get operations`() = runTest {
    val getRepository = MockGetRepository<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val expectedQuery = anyQuery()
    val expectedOperation = anyOperation()
    val anyString = randomString()
    val expectedValue = randomInt()

    val repository = RepositoryMapper(getRepository, anyVoidRepository(), anyVoidRepository<Int>(), toOutMapper, VoidMapper())
    mocker.everySuspending { getRepository.get(expectedQuery, expectedOperation) } returns anyString
    mocker.every { toOutMapper.map(anyString) } returns expectedValue

    val result = repository.get(expectedQuery, expectedOperation)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      getRepository.get(expectedQuery, expectedOperation)
      toOutMapper.map(anyString)
    }
  }

  @Test
  fun `should map put operations`() = runTest {
    val putRepository = MockPutRepository<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()
    val expectedValueString = expectedValue.toString()
    val expectedOperation = anyOperation()

    val repository = RepositoryMapper(anyVoidRepository(), putRepository, anyVoidRepository<Int>(), toOutMapper, toInMapper)
    mocker.every { toInMapper.map(expectedValue) } returns expectedValueString
    mocker.everySuspending { putRepository.put(expectedQuery, expectedValueString, expectedOperation) } returns expectedValueString
    mocker.every { toOutMapper.map(expectedValueString) } returns expectedValue

    val result = repository.put(expectedQuery, expectedValue, expectedOperation)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      toInMapper.map(expectedValue)
      putRepository.put(expectedQuery, expectedValueString, expectedOperation)
      toOutMapper.map(expectedValueString)
    }
  }

  @Test
  fun `should map delete operations`() = runTest {
    val deleteRepository = MockDeleteRepository(mocker)
    val expectedQuery = anyQuery()
    val expectedOperation = anyOperation()

    val repository = RepositoryMapper(anyVoidRepository(), anyVoidRepository<String>(), deleteRepository, anyVoidMapper(), anyVoidMapper())
    mocker.everySuspending { deleteRepository.delete(expectedQuery, expectedOperation) } returns Unit

    repository.delete(expectedQuery, expectedOperation)

    mocker.verifyWithSuspend { deleteRepository.delete(expectedQuery, expectedOperation) }
  }
}

@UsesMocks(GetRepository::class, Mapper::class)
class GetRepositoryMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should map get operations`() = runTest {
    val getRepository = MockGetRepository<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val expectedQuery = anyQuery()
    val expectedOperation = anyOperation()
    val anyString = randomString()
    val expectedValue = randomInt()

    val repository = GetRepositoryMapper(getRepository, toOutMapper)
    mocker.everySuspending { getRepository.get(expectedQuery, expectedOperation) } returns anyString
    mocker.every { toOutMapper.map(anyString) } returns expectedValue

    val result = repository.get(expectedQuery, expectedOperation)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      getRepository.get(expectedQuery, expectedOperation)
      toOutMapper.map(anyString)
    }
  }
}

@UsesMocks(PutRepository::class, Mapper::class)
class PutRepositoryMapperTests : BaseTest() {

  val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
  }

  @Test
  fun `should map put operations`() = runTest {
    val putRepository = MockPutRepository<String>(mocker)
    val toOutMapper = MockMapper<String, Int>(mocker)
    val toInMapper = MockMapper<Int, String>(mocker)
    val expectedQuery = anyQuery()
    val expectedValue = randomInt()
    val expectedValueString = expectedValue.toString()
    val expectedOperation = anyOperation()

    val repository = PutRepositoryMapper(putRepository, toOutMapper, toInMapper)
    mocker.every { toInMapper.map(expectedValue) } returns expectedValueString
    mocker.everySuspending { putRepository.put(expectedQuery, expectedValueString, expectedOperation) } returns expectedValueString
    mocker.every { toOutMapper.map(expectedValueString) } returns expectedValue

    val result = repository.put(expectedQuery, expectedValue, expectedOperation)

    assertEquals(expectedValue, result)
    mocker.verifyWithSuspend {
      toInMapper.map(expectedValue)
      putRepository.put(expectedQuery, expectedValueString, expectedOperation)
      toOutMapper.map(expectedValueString)
    }
  }
}
