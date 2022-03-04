package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.common.randomInt
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CBORObjectToByteArrayMapperTests {

  @Serializable
  data class Dummy(val id: Int)

  @Test
  fun `should encode successfully to ByteArray an object`() {
    val value = randomDummy()
    val cbor = Cbor { }

    val mapper = CBORObjectToByteArray(cbor, Dummy.serializer())
    val result = mapper.map(value)

    val expectedValue = cbor.encodeToByteArray(value)
    assertContentEquals(expectedValue, result)
  }

  @Test
  fun `should encode successfully to ByteArray an object list`() {
    val values = listOf(randomDummy(), randomDummy())
    val cbor = Cbor { }

    val mapper = CBORListObjectToByteArray(cbor, Dummy.serializer())
    val result = mapper.map(values)

    val expectedValue = cbor.encodeToByteArray(ListSerializer(Dummy.serializer()), values)
    assertContentEquals(expectedValue, result)
  }

  @Test
  fun `should decode object from byte array to object`() {
    val cbor = Cbor { }
    val expectedValue = randomDummy()
    val expectedValueByteArray = cbor.encodeToByteArray(expectedValue)

    val mapper = CBORByteArrayToObject(cbor, Dummy.serializer())
    val result = mapper.map(expectedValueByteArray)

    assertEquals(expectedValue, result)
  }

  @Test
  fun `should decode list object from byte array to  list object`() {
    val cbor = Cbor { }
    val expectedValues = listOf(randomDummy(), randomDummy())
    val expectedValueByteArray = cbor.encodeToByteArray(expectedValues)

    val mapper = CBORByteArrayToListObject(cbor, Dummy.serializer())
    val results = mapper.map(expectedValueByteArray)

    assertContentEquals(expectedValues, results)
  }

  private fun randomDummy() = Dummy(randomInt())
}
