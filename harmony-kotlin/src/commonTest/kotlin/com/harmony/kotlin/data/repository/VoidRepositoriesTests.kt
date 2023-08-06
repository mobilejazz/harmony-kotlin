package com.harmony.kotlin.data.repository

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.data.operation.anyOperation
import com.harmony.kotlin.data.query.anyQuery
import kotlin.test.Test
import kotlin.test.assertFailsWith

class VoidRepositoriesTests : BaseTest() {

  @Test
  fun `should throw not supported operation when get function is executed`() = runTest {
    val repository = VoidRepository<Int>()

    assertFailsWith<UnsupportedOperationException> {
      repository.get(anyQuery(), anyOperation())
    }
  }

  @Test
  fun `should throw not supported operation when put function is executed`() = runTest {
    val repository = VoidRepository<Int>()

    assertFailsWith<UnsupportedOperationException> {
      repository.put(anyQuery(), randomInt(), anyOperation())
    }
  }

  @Test
  fun `should throw not supported operation when delete function is executed`() = runTest {
    val repository = VoidRepository<Int>()

    assertFailsWith<UnsupportedOperationException> {
      repository.delete(anyQuery(), anyOperation())
    }
  }
}

class VoidGetRepositoriesTests : BaseTest() {

  @Test
  fun `should throw not supported operation when get function is executed`() = runTest {
    val repository = VoidGetRepository<Int>()

    assertFailsWith<UnsupportedOperationException> {
      repository.get(anyQuery(), anyOperation())
    }
  }
}

class VoidPutRepositoriesTests : BaseTest() {

  @Test
  fun `should throw not supported operation when put function is executed`() = runTest {
    val repository = VoidPutRepository<Int>()

    assertFailsWith<UnsupportedOperationException> {
      repository.put(anyQuery(), randomInt(), anyOperation())
    }
  }
}

class VoidDeleteRepositoriesTests : BaseTest() {

  @Test
  fun `should throw not supported operation when delete function is executed`() = runTest {
    val repository = VoidDeleteRepository()

    assertFailsWith<UnsupportedOperationException> {
      repository.delete(anyQuery(), anyOperation())
    }
  }
}
