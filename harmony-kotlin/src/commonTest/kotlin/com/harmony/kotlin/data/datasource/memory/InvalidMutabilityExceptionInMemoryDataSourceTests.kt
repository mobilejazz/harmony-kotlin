package com.harmony.kotlin.data.datasource.memory

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.query.KeyQuery
import kotlin.test.Test
import kotlin.test.assertFailsWith

class InvalidMutabilityExceptionInMemoryDataSourceTests : BaseTest() {
  object OutsideScope {
    val inMemoryDataSource = InMemoryDataSource<String>()
  }

  @Test
  fun `should fail because InvalidMutabilityException`() = runTest {
    assertFailsWith(RuntimeException::class) {
      val pair = Pair(KeyQuery(randomString()), randomString())
      OutsideScope.inMemoryDataSource.put(pair.first, pair.second)
    }
  }
}
