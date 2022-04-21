package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.common.getSome
import com.harmony.kotlin.common.randomByteArray
import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.error.MappingException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ByteArrayMapperTest {

  @Test
  fun `assert object serialized properly`() {
    val mapper = ObjectToByteArrayMapper(Dummy.serializer(), Json)
    val content = randomString()
    val expectedByteArray = "{\"value\":\"$content\"}".encodeToByteArray()
    val data = Dummy(content)

    val actualByteArray = mapper.map(data)

    assertTrue(expectedByteArray.contentEquals(actualByteArray))
  }

  @Test(expected = MappingException::class)
  fun `assert mapper exception is thrown when mapper fails to serialize object`() {
    val json = mockk<Json>()
    val serializer = Dummy.serializer()
    val mapper = ObjectToByteArrayMapper(serializer, json)
    every { json.encodeToString(serializer, any()) } throws SerializationException()

    mapper.map(Dummy(randomString()))
  }

  @Test
  fun `assert json deserialized to object properly`() {
    val mapper = ByteArrayToObjectMapper(Dummy.serializer(), Json)
    val content = randomString()
    val byteArray = "{\"value\":\"$content\"}".encodeToByteArray()
    val expectedData = Dummy(content)

    val actualData = mapper.map(byteArray)

    assertEquals(expectedData, actualData)
  }

  @Test(expected = MappingException::class)
  fun `assert mapper exception is thrown when mapper fails to deserialize json`() {
    val json = mockk<Json>()
    val serializer = Dummy.serializer()
    val mapper = ByteArrayToObjectMapper(serializer, json)
    every { json.decodeFromString(serializer, any()) } throws SerializationException()

    mapper.map(randomByteArray())
  }

  @Test
  fun `assert object list serialized properly`() {
    val mapper = ObjectListToByteArrayMapper(Dummy.serializer(), Json)
    val (json, dataList) = getJsonObjectListMap()
    val expectedByteArray = json.encodeToByteArray()

    val actualByteArray = mapper.map(dataList)

    assertTrue(expectedByteArray.contentEquals(actualByteArray))
  }

  @Test(expected = MappingException::class)
  fun `assert mapper exception is thrown when mapper fails to serialize object list`() {
    val json = mockk<Json>()
    val mapper = ObjectListToByteArrayMapper(Dummy.serializer(), json)
    val listToSerialize = getSome { Dummy(randomString()) }
    every { json.encodeToString(any(), listToSerialize) } throws SerializationException()

    mapper.map(listToSerialize)
  }

  @Test
  fun `assert json deserialized to object list properly`() {
    val mapper = ByteArrayToObjectListMapper(Dummy.serializer(), Json)
    val (json, expectedDataList) = getJsonObjectListMap()
    val expectedByteArray = json.encodeToByteArray()

    val actualDataList = mapper.map(expectedByteArray)

    assertEquals(expectedDataList, actualDataList)
  }

  @Test(expected = MappingException::class)
  fun `assert mapper exception is thrown when mapper fails to deserialize json array`() {
    val json = mockk<Json>()
    val mapper = ByteArrayToObjectListMapper(Dummy.serializer(), json)
    val serializerSlot = slot<KSerializer<List<Dummy>>>()
    every { json.decodeFromString(capture(serializerSlot), any()) } throws SerializationException()

    mapper.map(randomByteArray())
  }

  private fun getJsonObjectListMap(): Pair<String, List<Dummy>> {
    val content = randomString()
    val objectCount = randomInt(min = 0, max = 100)
    val serializedObject = JsonObject(mapOf("value" to JsonPrimitive(content)))
    val serializedObjectList = buildList {
      repeat(objectCount) {
        add(serializedObject)
      }
    }
    val json = JsonArray(serializedObjectList).toString()
    val dataList = getSome(objectCount) { Dummy(content) }

    return Pair(json, dataList)
  }

  @Serializable
  data class Dummy(val value: String)
}
