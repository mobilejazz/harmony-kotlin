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
  fun `should response the values if exist using get`() = runTest {
    val pair = Pair(KeyQuery(randomString()), listOf(randomString(), randomString()))
    val inMemoryDataSource = givenInMemoryDataSourceOfList(insertValues = pair)

    val response = inMemoryDataSource.get(pair.first)

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
  fun `should throw IllegalArgumentException if the value is null when put function is called`() = runTest {
    assertFailsWith<IllegalArgumentException> {
      val inMemoryDataSource = givenInMemoryDataSource()
      val query = KeyQuery(randomString())

      inMemoryDataSource.put(query, null)
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

    val inMemoryDataSource = givenInMemoryDataSource(insertValue = valueToInsert)

    inMemoryDataSource.delete(query = valueToInsert.first)

    assertFailsWith<DataNotFoundException> {
      inMemoryDataSource.get(valueToInsert.first)
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
  ): InMemoryDataSource<String> {
    val inMemoryDataSource = InMemoryDataSource<String>()

    insertValue?.let {
      inMemoryDataSource.put(it.first, it.second)
    }

    return inMemoryDataSource
  }

  private suspend fun givenInMemoryDataSourceOfList(
    insertValues: Pair<KeyQuery, List<String>>? = null
  ): InMemoryDataSource<List<String>> {
    val inMemoryDataSource = InMemoryDataSource<List<String>>()

    insertValues?.let {
      inMemoryDataSource.put(it.first, it.second)
    }

    return inMemoryDataSource
  }
}
