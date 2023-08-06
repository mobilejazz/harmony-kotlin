@file:Suppress("IllegalIdentifier")

package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.common.getSome
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.error.DataSerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JsonMapperTests {
  @Serializable
  data class Dummy(val key: String)

  @Test
  fun `should map from model to json`() {
    val content = randomString()
    val objectToMap = Dummy(content)

    val json = ModelToJsonStringMapper(Dummy.serializer()).map(objectToMap)

    assertEquals("{\"key\":\"$content\"}", json)
  }

  @Test
  fun `should map from list to json`() {
    val someObjects = getSome { Dummy(randomString()) }

    val json = ModelToJsonStringMapper(ListSerializer(Dummy.serializer())).map(someObjects)

    val expectedJson = someObjects.joinToString(prefix = "[", separator = ",", postfix = "]") {
      "{\"key\":\"${it.key}\"}"
    }
    assertEquals(expectedJson, json)
  }

  @Test
  fun `should map from json to model`() {
    val randomString = randomString()
    val json = "{\"key\":\"$randomString\"}"

    val objectFromJson = JsonStringToModelMapper(Dummy.serializer()).map(json)

    assertEquals(Dummy(randomString), objectFromJson)
  }

  @Test
  fun `should map from json to list of model`() {
    val someStrings = getSome { randomString() }
    val jsonArray = someStrings.joinToString(prefix = "[", separator = ",", postfix = "]") {
      "{\"key\":\"$it\"}"
    }
    val listFromJson = JsonStringToModelMapper(ListSerializer(Dummy.serializer())).map(jsonArray)

    val expectedList = someStrings.map { Dummy(it) }
    assertEquals(expectedList, listFromJson)
  }

  @Test
  fun `should throw MappingSerializationException when deserializing a not expected model`() {
    var expectedException: Exception? = null
    val json = "{\"key\":\"${randomString()}\",\"key2\":\"${randomString()}\"}"

    try {
      JsonStringToModelMapper(Dummy.serializer()).map(json)
    } catch (e: Exception) {
      expectedException = e
    }

    assertNotNull(expectedException)
    assertTrue(expectedException is DataSerializationException)
  }
}
