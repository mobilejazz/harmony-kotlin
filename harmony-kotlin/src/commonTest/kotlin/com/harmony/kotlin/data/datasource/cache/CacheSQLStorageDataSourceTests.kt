package com.harmony.kotlin.data.datasource.cache

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.randomByteArray
import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.harmony.kotlin.data.query.AllObjectsQuery
import com.harmony.kotlin.data.query.anyKeyQuery
import com.harmony.kotlin.data.query.anyQuery
import com.harmony.kotlin.data.utilities.InsertionValue
import com.harmony.kotlin.data.utilities.anyByteArrayInsertionValue
import com.harmony.kotlin.data.utilities.anyInsertionValue
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.QueryNotSupportedException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

expect fun cacheDatabaseTests(): CacheDatabase

@kotlinx.coroutines.ExperimentalCoroutinesApi
class CacheSQLStorageDataSourceTests : BaseTest() {

  //region - get()
  @Test
  fun `should return value when exist`() = runTest {
    val expectedValue = anyInsertionValue(value = randomByteArray())
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(
      putValues = listOf(expectedValue)
    )

    val value = cacheSQLStorageDataSource.get(expectedValue.query)

    assertContentEquals(expectedValue.value, value)
  }

  @Test
  fun `should throw query not supported when query is invalid using get function`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.get(anyQuery())
    }
  }

  @Test
  fun `should throw data not found exception when no values using get function`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.get(anyKeyQuery())
    }
  }
  //endregion

  //region - put()

  @Test
  fun `should throw illegal argument exception when value is null`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<IllegalArgumentException> {
      cacheSQLStorageDataSource.put(anyKeyQuery(), null)
    }
  }

  @Test
  fun `should throw query not supported when query is invalid using put function`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.put(anyQuery(), randomByteArray())
    }
  }

  @Test
  fun `should insert value when not exist`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()
    val expectedValue = anyInsertionValue(value = randomByteArray())

    val valueFromPut = cacheSQLStorageDataSource.put(expectedValue.query, expectedValue.value)
    val valueFromGet = cacheSQLStorageDataSource.get(expectedValue.query)

    assertContentEquals(expectedValue.value, valueFromPut)
    assertContentEquals(expectedValue.value, valueFromGet)
  }

  @Test
  fun `should update value when exist`() = runTest {
    val insertedValue = anyByteArrayInsertionValue()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(
      putValues = listOf(insertedValue)
    )

    val valueToUpdate = randomByteArray()
    val valueFromPut = cacheSQLStorageDataSource.put(insertedValue.query, valueToUpdate)
    val valueFromGet = cacheSQLStorageDataSource.get(insertedValue.query)

    assertContentEquals(valueToUpdate, valueFromPut)
    assertContentEquals(valueToUpdate, valueFromGet)
  }

  //endregion

  //region - delete

  @Test
  fun `should throw query not supported when query is invalid using delete function`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.delete(anyQuery())
    }
  }

  @Test
  fun `should delete all content of the database`() = runTest {
    val elementOne = anyByteArrayInsertionValue()
    val elementTwo = anyByteArrayInsertionValue()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(
      putValues = listOf(elementOne, elementTwo)
    )

    cacheSQLStorageDataSource.delete(AllObjectsQuery())

    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.get(elementOne.query)
    }
    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.get(elementTwo.query)
    }
  }

  @Test
  fun `should delete value`() = runTest {
    val insertedValue = anyByteArrayInsertionValue()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(putValues = listOf(insertedValue))

    cacheSQLStorageDataSource.delete(insertedValue.query)

    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.get(insertedValue.query)
    }
  }
  //endregion

  private suspend fun givenCacheSQLStorageDataSource(
    database: CacheDatabase = cacheDatabaseTests(),
    putValues: List<InsertionValue<ByteArray>> = emptyList(),
  ): CacheSQLStorageDataSource {
    val cacheSQLStorageDataSource = CacheSQLStorageDataSource(database)

    putValues.forEach {
      cacheSQLStorageDataSource.put(it.query, it.value)
    }
    return cacheSQLStorageDataSource
  }
}
