package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.common.randomInt
import kotlin.test.Test
import kotlin.test.assertFailsWith

class VoidMapperTests {

  @Test
  fun `should throw UnsupportedOperationException`() {
    val mapper = VoidMapper<Int, Int>()

    assertFailsWith<UnsupportedOperationException> {
      mapper.map(randomInt())
    }
  }
}
