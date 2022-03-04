package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.common.randomString
import kotlin.test.Test
import kotlin.test.assertEquals

class IdentityMapperTests {

  @Test
  fun `should success`() {
    val expectedValue = randomString()
    val mapper = IdentityMapper<String>()

    val result = mapper.map(expectedValue)

    assertEquals(expectedValue, result)
  }
}
