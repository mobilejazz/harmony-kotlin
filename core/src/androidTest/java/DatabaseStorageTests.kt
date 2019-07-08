import android.support.test.runner.AndroidJUnit4
import com.mobilejazz.harmony.kotlin.core.repository.datasource.database.ByteArrayStorageOpenHelper
import com.mobilejazz.harmony.kotlin.core.repository.datasource.database.DatabaseStorageDataSource
import com.mobilejazz.harmony.kotlin.core.repository.error.DataNotFoundException
import com.mobilejazz.harmony.kotlin.core.repository.mapper.*
import com.mobilejazz.harmony.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.harmony.kotlin.core.threading.extensions.unwrap
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
    val database = ByteArrayStorageOpenHelper(appContext, "test.db", 1).writableDatabase
    databaseStorageDataSource = DatabaseStorageDataSource(database)
  }

  @After
  fun tearDown() {
    appContext.deleteDatabase("test.db")
  }

  @Test
  fun shouldGetStoredObject_WhenCallingGetAfterPut_GivenSerializableObject() {
    // Given
    val testObject = TestObject(1, "test.object.1")
    val byteArrayPersisted = modelToByteArrayMapper.map(testObject)
    databaseStorageDataSource.put(KeyQuery("testObject"), byteArrayPersisted)

    // When
    val byteArrayObtained = databaseStorageDataSource.get(KeyQuery("testObject")).get()

    // Then
    Assert.assertTrue(byteArrayPersisted.contentEquals(byteArrayObtained))
    Assert.assertEquals(testObject, byteArrayToModelMapper.map(byteArrayObtained))
  }


  @Test
  fun shouldGetStoredList_WhenCallingGetAfterPut_GivenListOfSerializableObjects() {
    // Given
    val testList = listOf(TestObject(1, "test.object.1"), TestObject(2, "test.object.2"))
    val byteArrayPersisted = modelListToByteArrayMapper.map(testList)
    databaseStorageDataSource.put(KeyQuery("testList"), byteArrayPersisted)

    // When
    val byteArrayObtained = databaseStorageDataSource.get(KeyQuery("testList")).get()

    // Then
    Assert.assertTrue(byteArrayPersisted.contentEquals(byteArrayObtained))
    Assert.assertEquals(testList, byteArrayToModelListMapper.map(byteArrayObtained))
  }

  @Test(expected = DataNotFoundException::class)
  fun shouldThrowDataNotFoundException_WhenCallingGetAfterDelete_GivenSerializableObject() {
    // Given
    val testObject = TestObject(1, "test.object.1")
    val byteArrayPersisted = modelToByteArrayMapper.map(testObject)
    databaseStorageDataSource.put(KeyQuery("testDelete"), byteArrayPersisted)

    // When
    databaseStorageDataSource.delete(KeyQuery("testDelete"))

    // Then -> throws expected exception
    try {
      databaseStorageDataSource.get(KeyQuery("testDelete")).get()
    } catch (exception: ExecutionException) {
      throw exception.unwrap(ExecutionException::class.java)
    }
  }
}

private data class TestObject(private val id: Int, private val name: String) : Serializable
