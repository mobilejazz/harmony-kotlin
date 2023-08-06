package com.harmony.kotlin.data.datasource

import com.harmony.kotlin.common.ANY_ITEMS_COUNT
import com.harmony.kotlin.common.BaseTest
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
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

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
  fun `should not allow negative amount of executions`() {
    var exception: IllegalStateException? = null

    try {
      RetryDataSource(
        getDataSource, VoidDataSource(), VoidDataSource<Unit>(),
        maxAmountOfExecutions = randomInt(max = 0)
      )
    } catch (illegalStateException: IllegalStateException) {
      exception = illegalStateException
    }

    assertNotNull(exception)
  }

  @Test
  fun `should work when request works at first attempt`() = runTest {
    val expectedResponse = randomString()
    var callCounter = 0
    mocker.everySuspending { getDataSource.get(isAny()) } runs {
      callCounter++
      expectedResponse
    }
    val retryDataSource = RetryDataSource(getDataSource, VoidDataSource(), VoidDataSource<Unit>(), maxAmountOfExecutions = ANY_ITEMS_COUNT)

    val actualResult = retryDataSource.get(VoidQuery)

    assertEquals(actualResult, expectedResponse)
    assertEquals(1, callCounter)
  }

  @Test
  fun `should work when request works at second attempt`() = runTest {
    val expectedResponse = randomString()
    var callCounter = 0
    mocker.everySuspending { getDataSource.get(isAny()) } runs {
      callCounter++
      if (callCounter == 1) throw DataNotFoundException()
      else expectedResponse
    }
    val retryDataSource = RetryDataSource(getDataSource, VoidDataSource(), VoidDataSource<Unit>(), maxAmountOfExecutions = ANY_ITEMS_COUNT)

    val actualResult = retryDataSource.get(VoidQuery)

    assertEquals(actualResult, expectedResponse)
    assertEquals(2, callCounter)
  }

  @Test
  fun `should fail when all retries failed`() = runTest {
    val dataException = DataNotFoundException("dummy error")
    mocker.everySuspending { putDataSource.put(isAny(), isAny()) } runs { throw dataException }
    val retryDataSource = RetryDataSource(
      VoidDataSource(),
      putDataSource,
      VoidDataSource<Unit>(),
      maxAmountOfExecutions = ANY_ITEMS_COUNT,
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
      if (callCounter == 1) throw AnyException("first exception")
      else throw dataException
    }
    val retryDataSource = RetryDataSource<String>(
      VoidDataSource(), VoidDataSource(),
      deleteDataSource,
      maxAmountOfExecutions = Int.MAX_VALUE,
      retryIf = { it !is DataNotFoundException },
      logger = ConsoleLogger()
    )

    val catchException = assertFailsWith<DataNotFoundException> {
      retryDataSource.delete(VoidQuery)
    }
    assertEquals(catchException, dataException)
    assertEquals(2, callCounter)
  }

  private data class AnyException(
    override val message: String
  ) : Exception()
}
