@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.datasource.memory

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.VoidQuery
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.QueryNotSupportedException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InMemoryDataSourceTests : BaseTest() {

  @Test
  fun `should throw DataNotFoundException if value is missing`() = runTest {
    assertFailsWith<DataNotFoundException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val query = KeyQuery(randomString())

      inMemoryDataSource.get(query)
    }
  }

  @Test
  fun `should throw QueryNotSupportedException if query is invalid when get function is called`() = runTest {
    assertFailsWith<QueryNotSupportedException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val invalidQuery = VoidQuery

      inMemoryDataSource.get(invalidQuery)
    }
  }

  @Test
  fun `should response the value if exist`() = runTest {
    val pair = Pair(KeyQuery(randomString()), randomString())
    val inMemoryDataSource = givenInMemoryDataSource(insertValue = pair)

    val response = inMemoryDataSource.get(pair.first)

    assertEquals(pair.second, response)
  }

  @Test
  fun `should throw DataNotFoundException if values are missing`() = runTest {
    assertFailsWith<DataNotFoundException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val query = KeyQuery(randomString())

      inMemoryDataSource.getAll(query)
    }
  }

  @Test
  fun `should throw QueryNotSupportedException if query is invalid when getAll function is called`() = runTest {
    assertFailsWith<QueryNotSupportedException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val invalidQuery = VoidQuery

      inMemoryDataSource.getAll(invalidQuery)
    }
  }

  @Test
  fun `should response the values if exist`() = runTest {
    val pair = Pair(KeyQuery(randomString()), listOf(randomString(), randomString()))
    val inMemoryDataSource = givenInMemoryDataSource(insertValues = pair)

    val response = inMemoryDataSource.getAll(pair.first)

    assertEquals(pair.second, response)
  }

  @Test
  fun `should throw QueryNotSupportedException if query is invalid when put function is called`() = runTest {
    assertFailsWith<QueryNotSupportedException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val invalidQuery = VoidQuery

      inMemoryDataSource.put(invalidQuery, randomString())
    }
  }

  @Test
  fun `should throw QueryNotSupportedException if query is invalid when putAll function is called`() = runTest {
    assertFailsWith<QueryNotSupportedException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val invalidQuery = VoidQuery

      inMemoryDataSource.putAll(invalidQuery, listOf(randomString()))
    }
  }

  @Test
  fun `should throw IllegalArgumentException if the value is null when put function is called`() = runTest {
    assertFailsWith<IllegalArgumentException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val query = KeyQuery(randomString())

      inMemoryDataSource.put(query, null)
    }
  }

  @Test
  fun `should throw IllegalArgumentException if the value is null when putAll function is called`() = runTest {
    assertFailsWith<IllegalArgumentException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val query = KeyQuery(randomString())

      inMemoryDataSource.putAll(query, null)
    }
  }

  @Test
  fun `should store value if query is valid when put function is called`() = runTest {
    val inMemoryDataSource = givenInMemoryDataSource()
    val query = KeyQuery(randomString())
    val expectedValue = randomString()

    inMemoryDataSource.put(query, expectedValue)
    val response = inMemoryDataSource.get(query)

    assertEquals(expectedValue, response)
  }

  @Test
  fun `should store values if query is valid when putAll function is called`() = runTest {
    val inMemoryDataSource = givenInMemoryDataSource()
    val query = KeyQuery(randomString())
    val expectedValue = listOf(randomString())

    inMemoryDataSource.putAll(query, expectedValue)
    val response = inMemoryDataSource.getAll(query)

    assertEquals(expectedValue, response)
  }

  @Test
  fun `should not allow use the same key for the different put and putAll functions removing the value from the other query`() = runTest {
    assertFailsWith<DataNotFoundException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val query = KeyQuery(randomString())
      val expectedValue = randomString()

      inMemoryDataSource.put(query, expectedValue)
      inMemoryDataSource.putAll(query, listOf(randomString()))

      inMemoryDataSource.get(query)
    }
  }

  @Test
  fun `should throw QueryNotSupportedException if query is invalid when delete function is called`() = runTest {
    assertFailsWith<QueryNotSupportedException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val invalidQuery = VoidQuery

      inMemoryDataSource.delete(query = invalidQuery)
    }
  }

  @Test
  fun `should delete value if exist`() = runTest {
    val valueToInsert = Pair(KeyQuery(randomString()), randomString())
    val valuesToInsert = Pair(KeyQuery(randomString()), listOf(randomString(), randomString()))

    val inMemoryDataSource = givenInMemoryDataSource(insertValue = valueToInsert, insertValues = valuesToInsert)

    inMemoryDataSource.delete(query = valueToInsert.first)
    inMemoryDataSource.delete(query = valuesToInsert.first)

    assertFailsWith<DataNotFoundException> {
      inMemoryDataSource.get(valueToInsert.first)
    }

    assertFailsWith<DataNotFoundException> {
      inMemoryDataSource.getAll(valuesToInsert.first)
    }
  }
  @Test
  fun `should not fail because InvalidMutabilityException `() = runTest {
    class OutsideScope {
      val inMemoryDataSource = InMemoryDataSource<String>()
    }

    val scope = OutsideScope()
    val pair = Pair(KeyQuery(randomString()), randomString())
    scope.inMemoryDataSource.put(pair.first, pair.second)
  }

  private suspend fun givenInMemoryDataSource(
    insertValue: Pair<KeyQuery, String>? = null,
    insertValues: Pair<KeyQuery, List<String>>? = null
  ): InMemoryDataSource<String> {
    val inMemoryDataSource = InMemoryDataSource<String>()

    insertValue?.let {
      inMemoryDataSource.put(it.first, it.second)
    }

    insertValues?.let {
      inMemoryDataSource.putAll(it.first, it.second)
    }

    return inMemoryDataSource
  }
}
