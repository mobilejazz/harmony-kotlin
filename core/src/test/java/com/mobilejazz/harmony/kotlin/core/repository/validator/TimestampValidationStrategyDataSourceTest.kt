package com.mobilejazz.harmony.kotlin.core.repository.validator

import com.google.gson.GsonBuilder
import com.mobilejazz.harmony.kotlin.core.repository.validator.vastra.strategies.timestamp.TimestampValidationStrategyDataSource
import org.junit.Assert
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit


class TimestampValidationStrategyDataSourceTest {

  data class TestObject(private val id: Long, private val text: String, override var lastUpdate: Date) : TimestampValidationStrategyDataSource(lastUpdate) {
    override fun expiryTime(): Long {
      return TimeUnit.SECONDS.toMillis(1)
    }
  }

  @Test
  internal fun shouldGetLastUpdateNonNullValue_WhenCreatingObjectUsingGson() {
    val jsonTestObject = """
      | {
      |   "id": 123,
      |   "text": "Sample text",
      |   "lastUpdate": "2019-02-19T19:37:14.809Z" 
      | }
    """.trimMargin()

    val gson = GsonBuilder().create()

    val parsedObject = gson.fromJson(jsonTestObject, TestObject::class.java)

    Assert.assertNotNull(parsedObject.lastUpdate)
  }
}