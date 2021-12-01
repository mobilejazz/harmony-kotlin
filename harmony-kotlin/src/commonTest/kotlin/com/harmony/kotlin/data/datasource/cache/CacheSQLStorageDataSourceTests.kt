package com.harmony.kotlin.data.datasource.cache

import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.harmony.kotlin.data.error.DataNotFoundException
import com.harmony.kotlin.data.error.QueryNotSupportedException
import com.harmony.kotlin.data.query.AllObjectsQuery
import com.harmony.kotlin.data.query.anyKeyQuery
import com.harmony.kotlin.data.query.anyQuery
import com.harmony.kotlin.data.utilities.InsertionValue
import com.harmony.kotlin.data.utilities.InsertionValues
import com.harmony.kotlin.data.utilities.anyByteArrayInsertionValue
import com.harmony.kotlin.data.utilities.anyByteArrayInsertionValues
import com.harmony.kotlin.data.utilities.anyInsertionValue
import com.harmony.kotlin.data.utilities.randomByteArray
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
  fun `should throw query not supported when query is invalid using get()`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.get(anyQuery())
    }
  }

  @Test
  fun `should throw data not found exception when no values using get()`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.get(anyKeyQuery())
    }
  }
  //endregion

  //region - getAll()

  @Test
  fun `should throw data not found exception when no values using getAll()`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.getAll(anyKeyQuery())
    }
  }

  @Test
  fun `should throw query not supported when query is invalid using getAll()`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.getAll(anyQuery())
    }
  }

  @Test
  fun `should return values when exist`() = runTest {
    val expectedValues = anyByteArrayInsertionValues()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(
      putAllValues = listOf(expectedValues)
    )

    val value = cacheSQLStorageDataSource.getAll(expectedValues.query)

    assertByteArrayListContentEquals(expectedValues.value, value)
  }

  @Test
  @Ignore
  fun `should return all the values from the database when AllObjectsQuery is provided`() = runTest {
    val expectedValuesOne = anyByteArrayInsertionValues()
    val expectedValuesTwo = anyByteArrayInsertionValues()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(
      putAllValues = listOf(expectedValuesOne, expectedValuesTwo)
    )

    val values = cacheSQLStorageDataSource.getAll(AllObjectsQuery())

    // todo: add asserts to check that both arrays are equals
    assertTrue { (expectedValuesOne.value.size + expectedValuesTwo.value.size) == values.size }
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
  fun `should throw query not supported when query is invalid using put()`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.put(anyQuery(), randomByteArray())
    }
  }

  @Test
  fun `should insert value when not exist`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()
    val expectedValue = anyInsertionValue(randomByteArray())

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

  //region - putAll()

  @Test
  fun `should throw illegal argument exception when values is null`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<IllegalArgumentException> {
      cacheSQLStorageDataSource.putAll(anyKeyQuery(), null)
    }
  }

  @Test
  fun `should throw query not supported when query is invalid using putAll()`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.putAll(anyQuery(), listOf(randomByteArray()))
    }
  }

  @Test
  fun `should insert values when not exist`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()
    val expectedValues = anyByteArrayInsertionValues()

    val valuesFromPutAll = cacheSQLStorageDataSource.putAll(expectedValues.query, expectedValues.value)
    val valuesFromGetAll = cacheSQLStorageDataSource.getAll(expectedValues.query)

    assertByteArrayListContentEquals(expectedValues.value, valuesFromPutAll)
    assertByteArrayListContentEquals(expectedValues.value, valuesFromGetAll)
  }

  @Test
  fun `should update values when exist`() = runTest {
    val insertedValues = anyByteArrayInsertionValues()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(
      putAllValues = listOf(insertedValues)
    )

    val valuesToUpdate = listOf(randomByteArray())
    val valueFromPut = cacheSQLStorageDataSource.putAll(insertedValues.query, valuesToUpdate)
    val valueFromGet = cacheSQLStorageDataSource.getAll(insertedValues.query)

    assertByteArrayListContentEquals(valuesToUpdate, valueFromPut)
    assertByteArrayListContentEquals(valuesToUpdate, valueFromGet)
  }
  //endregion

  //region - delete

  @Test
  fun `should throw query not supported when query is invalid using delete()`() = runTest {
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource()

    assertFailsWith<QueryNotSupportedException> {
      cacheSQLStorageDataSource.delete(anyQuery())
    }
  }

  @Test
  fun `should delete all content of the database`() = runTest {
    val elementOne = anyByteArrayInsertionValues()
    val elementTwo = anyByteArrayInsertionValues()
    val elementThree = anyByteArrayInsertionValue()
    val elementFour = anyByteArrayInsertionValue()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(
      putAllValues = listOf(elementOne, elementTwo),
      putValues = listOf(elementThree, elementFour)
    )

    cacheSQLStorageDataSource.delete(AllObjectsQuery())

    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.getAll(elementOne.query)
    }
    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.getAll(elementTwo.query)
    }
    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.get(elementThree.query)
    }
    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.get(elementFour.query)
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

  @Test
  fun `should delete values`() = runTest {
    val insertedValues = anyByteArrayInsertionValues()
    val cacheSQLStorageDataSource = givenCacheSQLStorageDataSource(putAllValues = listOf(insertedValues))

    cacheSQLStorageDataSource.delete(insertedValues.query)

    assertFailsWith<DataNotFoundException> {
      cacheSQLStorageDataSource.getAll(insertedValues.query)
    }
  }
  //endregion

  private suspend fun givenCacheSQLStorageDataSource(
    database: CacheDatabase = cacheDatabaseTests(),
    putValues: List<InsertionValue<ByteArray>> = emptyList(),
    putAllValues: List<InsertionValues<ByteArray>> = emptyList()
  ): CacheSQLStorageDataSource {
    val cacheSQLStorageDataSource = CacheSQLStorageDataSource(database)

    putValues.forEach {
      cacheSQLStorageDataSource.put(it.query, it.value)
    }

    putAllValues.forEach {
      cacheSQLStorageDataSource.putAll(it.query, it.value)
    }

    return cacheSQLStorageDataSource
  }

  private fun assertByteArrayListContentEquals(expectedByteArrayList: List<ByteArray>, byteArrayList: List<ByteArray>, message: String? = null) {
    expectedByteArrayList.forEachIndexed { idx, byteArray ->
      assertContentEquals(byteArray, byteArrayList[idx], message)
    }
  }
}
