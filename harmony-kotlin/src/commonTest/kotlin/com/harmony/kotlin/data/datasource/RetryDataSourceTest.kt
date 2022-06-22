package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.ANY_ITEMS_COUNT
import com.harmony.kotlin.common.BaseTest
import com.harmony.kotlin.common.getSome
import com.harmony.kotlin.common.logger.ConsoleLogger
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomNullable
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.query.VoidQuery
import com.harmony.kotlin.error.DataNotFoundException
import org.kodein.mock.Mocker
import org.kodein.mock.UsesMocks
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@UsesMocks(GetDataSource::class, PutDataSource::class, DeleteDataSource::class)
class RetryDataSourceTest : BaseTest() {
  private lateinit var getDataSource: GetDataSource<String>
  private lateinit var putDataSource: PutDataSource<String>
  private lateinit var deleteDataSource: DeleteDataSource
  private val mocker = Mocker()

  @BeforeTest
  fun beforeTests() {
    mocker.reset()
    getDataSource = MockGetDataSource(mocker)
    putDataSource = MockPutDataSource(mocker)
    deleteDataSource = MockDeleteDataSource(mocker)
  }

  @Test
  fun `should not allow negative amount of retries`() {
    var exception: IllegalStateException? = null

    try {
      RetryDataSource(
        getDataSource,
        maxAmountOfRetries = randomInt(max = 0)
      )
    } catch (illegalStateException: IllegalStateException) {
      exception = illegalStateException
    }

    assertNotNull(exception)
  }

  @Test
  fun `should fail executing get when not using a get data source type`() = runTest {
    val retryDataSource = RetryDataSource(getDataSource = null, putDataSource, deleteDataSource)
    var exception: IllegalStateException? = null

    try {
      retryDataSource.get(VoidQuery)
    } catch (illegalStateException: IllegalStateException) {
      exception = illegalStateException
    }

    assertNotNull(exception)
  }

  @Test
  fun `should fail executing put when not using a put data source type`() = runTest {
    val retryDataSource = RetryDataSource(getDataSource, putDataSource = null, deleteDataSource)
    var exception: IllegalStateException? = null

    try {
      retryDataSource.put(VoidQuery, randomString())
    } catch (illegalStateException: IllegalStateException) {
      exception = illegalStateException
    }

    assertTrue(exception is IllegalStateException)
  }

  @Test
  fun `should fail executing delete when not using a delete data source type`() = runTest {
    val retryDataSource = RetryDataSource(getDataSource, putDataSource, deleteDataSource = null)
    var exception: IllegalStateException? = null

    try {
      retryDataSource.delete(VoidQuery)
    } catch (illegalStateException: IllegalStateException) {
      exception = illegalStateException
    }

    assertNotNull(exception)
  }

  @Test
  fun `should work when request works at first attempt`() = runTest {
    val expectedResponse = getSome { randomString() }
    var callCounter = 0
    mocker.everySuspending { getDataSource.getAll(isAny()) } runs {
      callCounter++
      expectedResponse
    }
    val retryDataSource = RetryDataSource(getDataSource, maxAmountOfRetries = ANY_ITEMS_COUNT)

    val actualResult = retryDataSource.getAll(VoidQuery)

    assertEquals(actualResult, expectedResponse)
    assertEquals(1, callCounter)
  }

  @Test
  fun `should work when request works at second attempt`() = runTest {
    val expectedResponse = getSome { randomString() }
    var callCounter = 0
    mocker.everySuspending { getDataSource.getAll(isAny()) } runs {
      callCounter++
      if (callCounter == 1) throw DataNotFoundException()
      else expectedResponse
    }
    val retryDataSource = RetryDataSource(getDataSource, maxAmountOfRetries = ANY_ITEMS_COUNT)

    val actualResult = retryDataSource.getAll(VoidQuery)

    assertEquals(actualResult, expectedResponse)
    assertEquals(2, callCounter)
  }

  @Test
  fun `should fail when all retries failed`() = runTest {
    val dataException = DataNotFoundException("dummy error")
    mocker.everySuspending { putDataSource.put(isAny(), isAny()) } runs { throw dataException }
    val retryDataSource = RetryDataSource(
      putDataSource = putDataSource,
      maxAmountOfRetries = ANY_ITEMS_COUNT,
      logger = ConsoleLogger()
    )
    var catchException: Exception? = null

    try {
      retryDataSource.put(VoidQuery, randomNullable { randomString() })
    } catch (e: Exception) {
      catchException = e
    }

    assertEquals(catchException, dataException)
  }

  @Test
  fun `should retry only with expected error`() = runTest {
    val dataException = DataNotFoundException("dummy error")
    var callCounter = 0
    mocker.everySuspending {
      deleteDataSource.delete(isAny())
    } runs {
      callCounter++
      if (callCounter == 1) throw Exception("first exception")
      else throw dataException
    }
    val retryDataSource = RetryDataSource<String>(
      deleteDataSource = deleteDataSource,
      maxAmountOfRetries = Int.MAX_VALUE,
      retryIf = { it !is DataNotFoundException },
      logger = ConsoleLogger()
    )
    var catchException: Exception? = null

    try {
      retryDataSource.delete(VoidQuery)
    } catch (exception: Exception) {
      catchException = exception
    }
    assertEquals(catchException, dataException)
    assertEquals(2, callCounter)
  }
}
