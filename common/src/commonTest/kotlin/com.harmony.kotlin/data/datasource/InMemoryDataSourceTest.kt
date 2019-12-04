package com.harmony.kotlin.data.datasource

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlin.test.Test

class InMemoryDataSourceTest {

  companion object {
    const val FAKE_STRING_KEY = "fake-key"
    val FAKE_INTEGERS_KEY = listOf(1, 2, 3)
    const val FAKE_INTEGER_VALUE = 134
  }

  @Test
  internal fun should_store_and_return_same_value_when_put_function_is_called() {
//    runBlocking {
//      // Given
//      val inMemoryDataSource = givenAInMemoryDataSource<Int>()
//      val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)
//
//      // When
//      val value = inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE)
//
//      // Then
//      assertThat(value).isEqualTo(FAKE_INTEGER_VALUE)
    }
  }

  @Test
  internal fun should_store_and_return_same_values_when_putAll_function_is_called() {
//    runBlocking {
//      // Given
//      val inMemoryDataSource = givenAInMemoryDataSource<Int>()
//      val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)
//
//      // When
//      val value = inMemoryDataSource.putAll(keyQuery, FAKE_INTEGERS_KEY)
//
//      // Then
//      assertThat(value).containsAll(FAKE_INTEGERS_KEY)
//    }
  }

  @Test
  fun should_get_value_when_get_function_is_called_and_value_stored() {
//    runBlocking {
//      // Given
//      val inMemoryDataSource = givenAInMemoryDataSource<Int>()
//      val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)
//
//      // when
//      inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE)
//      val expectedValue = inMemoryDataSource.get(keyQuery)
//
//      // then
//      assertThat(expectedValue).isEqualTo(FAKE_INTEGER_VALUE)
//    }
  }

//  @Test(expected = DataNotFoundException::class)
  fun should_throw_exception_if_value_for_key_not_exist() {
//    runBlocking {
//      // Given
//      val inMemoryDataSource = givenAInMemoryDataSource<Int>()
//      val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)
//
//      // when
//      val value = inMemoryDataSource.get(keyQuery)
//
//      // then
////      value.propagateCauseIfNeeded()
//    }
  }

//  @Test(expected = DataNotFoundException::class)
//  fun should_delete_value_when_exist() {
//    runBlocking {
//      // given
//      val inMemoryDataSource = givenAInMemoryDataSource<Int>()
//      val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)
//
//      // when
//      inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE)
//      inMemoryDataSource.delete(keyQuery)
//
//      // then
//      inMemoryDataSource.get(keyQuery)
//    }
//  }
//
//  private fun <T> givenAInMemoryDataSource() = InMemoryDataSource<T>()
//
//  private fun givenAKeyQuery(value: String) = KeyQuery(value)
//}