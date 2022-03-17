package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.common.randomInt
import kotlin.test.Test
import kotlin.test.assertEquals

class ClosureMapperTests {

  @Test
  fun `should success`() {
    val expectedValue = randomInt()
    val mapper = ClosureMapper<Int, String> { value -> value.toString() }

    val result = mapper.map(expectedValue)

    assertEquals(expectedValue.toString(), result)
  }
}
