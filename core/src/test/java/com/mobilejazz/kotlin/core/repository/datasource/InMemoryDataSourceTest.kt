package com.mobilejazz.kotlin.core.repository.datasource

import com.mobilejazz.kotlin.core.repository.datasource.memory.InMemoryDataSource
import com.mobilejazz.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.kotlin.core.repository.error.QueryNotSupportedException
import com.mobilejazz.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.kotlin.core.threading.extensions.Future
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InMemoryDataSourceTest {

  companion object {
    const val FAKE_STRING_KEY = "fake-key"
    const val FAKE_INTEGER_KEY = 1
    val FAKE_INTEGERS_KEY = listOf(1, 2, 3)
    const val FAKE_INTEGER_VALUE = 134
  }

  @Test
  internal fun should_store_and_return_same_value_when_put_function_is_called() {
    // Given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)

    // When
    val value = inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE)

    // Then
    assertThat(value.get()).isEqualTo(FAKE_INTEGER_VALUE)
  }

  @Test
  internal fun should_store_and_return_same_values_when_putAll_function_is_called() {
    // Given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)

    // When
    val value = inMemoryDataSource.putAll(keyQuery, FAKE_INTEGERS_KEY)

    // Then
    assertThat(value.get()).containsAll(FAKE_INTEGERS_KEY)
  }

  @Test(expected = QueryNotSupportedException::class)
  fun should_fail_storing_value_when_key_type_is_not_supported_calling_func_put() {
    // Given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_INTEGER_KEY)

    // When
    val value = inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE)

    value.propagateCauseIfNeeded()
  }

  @Test(expected = QueryNotSupportedException::class)
  fun should_fail_storing_values_when_key_type_is_not_supported_calling_func_putAll() {
    // Given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_INTEGER_KEY)

    // When
    val value = inMemoryDataSource.putAll(keyQuery, FAKE_INTEGERS_KEY)

    value.propagateCauseIfNeeded()
  }

  @Test
  fun should_get_value_when_get_function_is_called_and_value_stored() {
    // Given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)

    // when
    inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE)
    val expectedValue = inMemoryDataSource.get(keyQuery).get()

    // then
    assertThat(expectedValue).isEqualTo(FAKE_INTEGER_VALUE)
  }

  @Test(expected = QueryNotSupportedException::class)
  fun should_fail_getting_value_when_key_type_is_not_supported() {
    // Given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_INTEGER_KEY)

    // when
    inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE)
    val value = inMemoryDataSource.get(keyQuery)

    // then
    value.propagateCauseIfNeeded()
  }

  @Test(expected = DataNotFoundException::class)
  fun should_throw_exception_if_value_for_key_not_exist() {
    // Given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)

    // when
    val value = inMemoryDataSource.get(keyQuery)

    // then
    value.propagateCauseIfNeeded()
  }

  @Test(expected = DataNotFoundException::class)
  fun should_delete_value_when_exist() {
    // given
    val inMemoryDataSource = givenAInMemoryDataSource<Int>()
    val keyQuery = givenAKeyQuery(FAKE_STRING_KEY)

    // when
    inMemoryDataSource.put(keyQuery, FAKE_INTEGER_VALUE).get()
    inMemoryDataSource.delete(keyQuery).get()
    val value = inMemoryDataSource.get(keyQuery)

    // then
    value.propagateCauseIfNeeded()
  }

  private fun <T> givenAInMemoryDataSource() = InMemoryDataSource<T>()

  private fun <T> givenAKeyQuery(value: T) = KeyQuery(value)
}

fun <T> Future<T>.propagateCauseIfNeeded(): T {
  try {
    return this.get()
  } catch (e: Exception) {
    throw e.cause!!
  }
}