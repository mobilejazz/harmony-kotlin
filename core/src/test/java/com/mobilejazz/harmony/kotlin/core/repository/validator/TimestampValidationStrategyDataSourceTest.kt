package com.mobilejazz.harmony.kotlin.core.repository.validator

import com.google.gson.GsonBuilder
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.timestamp.TimestampValidationStrategyDataSource
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit


class TimestampValidationStrategyDataSourceTest {

  data class TestObject(private val id: Long, private val text: String) : TimestampValidationStrategyDataSource() {
    override val expiryTime: Long
      get() = TimeUnit.SECONDS.toMillis(1)
  }

  @Test
  internal fun shouldGetLastUpdateNonNullValue_WhenCreatingObjectUsingGson() {
    val jsonTestObject = """
      | {
      |   "id": 123,
      |   "text": "Sample text"
      | }
    """.trimMargin()

    val gson = GsonBuilder().create()

    val parsedObject = gson.fromJson(jsonTestObject, TestObject::class.java)

    Assert.assertNotNull(parsedObject.lastUpdate)
  }
}