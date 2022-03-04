package com.harmony.kotlin.data.mapper

import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.data.entity.PaginationOffsetLimit
import com.harmony.kotlin.data.utilities.randomIntList
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class PaginationOffsetLimitMapperTests {

  @Test
  fun `should success`() {
    val expectedPaginationValue = PaginationOffsetLimit(randomIntList(), randomInt(), randomInt(), randomInt())

    val paginationMapper = PaginationOffsetLimitMapper(IdentityMapper<Int>())
    val result = paginationMapper.map(expectedPaginationValue)

    assertContentEquals(expectedPaginationValue.values, result.values)
    assertEquals(expectedPaginationValue.limit, result.limit)
    assertEquals(expectedPaginationValue.offset, result.offset)
    assertEquals(expectedPaginationValue.size, result.size)
  }
}
