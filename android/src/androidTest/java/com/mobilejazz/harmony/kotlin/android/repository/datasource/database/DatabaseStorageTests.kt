package com.mobilejazz.harmony.kotlin.android.repository.datasource.database

import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.harmony.kotlin.data.mapper.ByteArrayToListModelMapper
import com.harmony.kotlin.data.mapper.ByteArrayToModelMapper
import com.harmony.kotlin.data.mapper.ListModelToByteArrayMapper
import com.harmony.kotlin.data.mapper.ModelToByteArrayMapper
import com.mobilejazz.harmony.kotlin.android.InstrumentationTest
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.harmony.kotlin.core.threading.extensions.unwrap
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.Serializable
import java.util.concurrent.ExecutionException


@RunWith(AndroidJUnit4::class)
class DatabaseStorageTests : InstrumentationTest() {

  private lateinit var databaseStorageDataSource: DatabaseStorageDataSource

  private val modelToByteArrayMapper: Mapper<TestObject, ByteArray> = ModelToByteArrayMapper()
  private val modelListToByteArrayMapper: Mapper<List<TestObject>, ByteArray> = ListModelToByteArrayMapper()
  private val byteArrayToModelMapper: Mapper<ByteArray, TestObject> = ByteArrayToModelMapper()
  private val byteArrayToModelListMapper: Mapper<ByteArray, List<TestObject>> = ByteArrayToListModelMapper()

  @Before
  fun setUp() {
    val database = FrameworkSQLiteOpenHelperFactory().create(
        SupportSQLiteOpenHelper.Configuration.builder(appContext)
            .callback(ByteArrayStorageCallback(1))
            .name("test.db")
            .build()
    ).writableDatabase

    databaseStorageDataSource = DatabaseStorageDataSource(database)
  }

  @After
  fun tearDown() {
    appContext.deleteDatabase("test.db")
  }

  @Test
  fun shouldGetStoredObject_WhenCallingGetAfterPut_GivenSerializableObject() {
    runBlocking {
      // Given
      val testObject = TestObject(1, "test.object.1")
      val byteArrayPersisted = modelToByteArrayMapper.map(testObject)
      databaseStorageDataSource.put(KeyQuery("testObject"), byteArrayPersisted)

      // When
      val byteArrayObtained = databaseStorageDataSource.get(KeyQuery("testObject"))

      // Then
      Assert.assertTrue(byteArrayPersisted.contentEquals(byteArrayObtained))
      Assert.assertEquals(testObject, byteArrayToModelMapper.map(byteArrayObtained))
    }
  }

  @Test
  fun shouldGetLastStoredObject_WhenCallingGetAfterPut_GivenSerializableObject() {
    runBlocking {
      // Given
      val testObject1 = TestObject(1, "test.object.1")
      val testObject2 = TestObject(2, "test.object.2")

      databaseStorageDataSource.put(KeyQuery("testObject"), modelToByteArrayMapper.map(testObject1))

      val byteArrayPersisted = modelToByteArrayMapper.map(testObject2)
      databaseStorageDataSource.put(KeyQuery("testObject"), byteArrayPersisted)

      // When
      val byteArrayObtained = databaseStorageDataSource.get(KeyQuery("testObject"))

      // Then
      Assert.assertTrue(byteArrayPersisted.contentEquals(byteArrayObtained))
      Assert.assertEquals(testObject2, byteArrayToModelMapper.map(byteArrayObtained))
    }
  }

  @Test
  fun shouldGetStoredList_WhenCallingGetAfterPut_GivenListOfSerializableObjects() {
    runBlocking {
      // Given
      val testList = listOf(TestObject(1, "test.object.1"), TestObject(2, "test.object.2"))
      val byteArrayPersisted = modelListToByteArrayMapper.map(testList)
      databaseStorageDataSource.put(KeyQuery("testList"), byteArrayPersisted)

      // When
      val byteArrayObtained = databaseStorageDataSource.get(KeyQuery("testList"))

      // Then
      Assert.assertTrue(byteArrayPersisted.contentEquals(byteArrayObtained))
      Assert.assertEquals(testList, byteArrayToModelListMapper.map(byteArrayObtained))
    }
  }

  @Test(expected = DataNotFoundException::class)
  fun shouldThrowDataNotFoundException_WhenCallingGetAfterDelete_GivenSerializableObject() {
    runBlocking {
      // Given
      val testObject = TestObject(1, "test.object.1")
      val byteArrayPersisted = modelToByteArrayMapper.map(testObject)
      databaseStorageDataSource.put(KeyQuery("testDelete"), byteArrayPersisted)

      // When
      databaseStorageDataSource.delete(KeyQuery("testDelete"))

      // Then -> throws expected exception
      try {
        databaseStorageDataSource.get(KeyQuery("testDelete"))
      } catch (exception: ExecutionException) {
        throw exception.unwrap(ExecutionException::class.java)
      }
    }
  }
}

private data class TestObject(private val id: Int, private val name: String) : Serializable
