package com.mobilejazz.harmony.kotlin.core.repository.datasource

import com.harmony.kotlin.data.query.VoidQuery
import com.harmony.kotlin.data.datasource.file.FileStreamValueDataStorage
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Index
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.Serializable

data class Foo(val id: Int, val name: String) : Serializable

class FileStreamValueDataStorageTest {

  @Rule
  @JvmField
  val tmpFolder = TemporaryFolder()

  companion object {
    const val FAKE_ID = 191
    const val FAKE_NAME = "Jose Luis Franconetti Olmedo"
  }

  @Test
  fun should_store_value_and_return_same_value_when_put_function_is_called() {
    runBlocking {
      val ds = givenAFileStreamValueDataSource()
      val foo = givenAFooObject()

      ds.put(VoidQuery, foo)

      val result = ds.getAll(VoidQuery)
      val expectedFoo = result[0]

      assertThat(result).isNotEmpty
      assertThat(expectedFoo).isEqualTo(foo)
      assertThat(expectedFoo.id).isEqualTo(FAKE_ID)
      assertThat(expectedFoo.name).isEqualTo(FAKE_NAME)
    }
  }

  @Test
  fun should_store_multiple_values_and_return_same_values_when_putAll_function_is_called() {
    runBlocking {
      val ds = givenAFileStreamValueDataSource()
      val foos = givenAListOfFooObjects(4)

      ds.putAll(VoidQuery, foos)

      val expectedFoos = ds.getAll(VoidQuery)

      assertThat(expectedFoos)
        .isNotEmpty
        .hasSize(4)
        .containsAll(foos)
    }
  }

  @Test
  fun should_store_multiple_values_in_different_stages_when_put_function_is_called() {
    runBlocking {
      val ds = givenAFileStreamValueDataSource()
      val fooJose = Foo(FAKE_ID, FAKE_NAME)
      val fooJoan = Foo(12, "Joan Martin")

      ds.put(VoidQuery, fooJose)
      ds.put(VoidQuery, fooJoan)

      val expectedFoos = ds.getAll(VoidQuery)

      assertThat(expectedFoos)
        .isNotNull
        .isNotEmpty
        .hasSize(2)
        .contains(fooJoan, Index.atIndex(0))
        .contains(fooJose, Index.atIndex(1))
    }
  }

  @Test
  fun should_delete_all_values_when_deleteAll_function_is_called() {
    runBlocking {
      val ds = givenAFileStreamValueDataSource()
      val foos = givenAListOfFooObjects(6)

      ds.putAll(VoidQuery, foos)
      ds.deleteAll(VoidQuery)
      val expectedFoos = ds.getAll(VoidQuery)

      assertThat(expectedFoos)
        .isNotNull
        .isEmpty()
    }
  }

  private fun givenAFileStreamValueDataSource(): FileStreamValueDataStorage<Foo> {
    return FileStreamValueDataStorage(tmpFolder.newFile("fake.txt"))
  }

  private fun givenAFooObject(): Foo = Foo(FAKE_ID, FAKE_NAME)

  private fun givenAListOfFooObjects(quantity: Int): List<Foo> {
    val foos = mutableListOf<Foo>()
    for (i in 1..quantity) {
      foos.add(givenAFooObject())
    }
    return foos.toList()
  }
}
