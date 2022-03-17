package com.harmony.kotlin.data.query

import com.harmony.kotlin.common.randomInt
import com.harmony.kotlin.common.randomString
import kotlin.test.Test
import kotlin.test.assertEquals

class QueryTests {

  @Test
  fun `AllObjectsQuery should has always the same key`() {
    val expectedKey = "all-objects-key"
    val query = AllObjectsQuery()

    assertEquals(query.key, expectedKey)
  }

  @Test
  fun `IdQuery should use the identifier as the string key for the KeyQuery`() {
    val id = randomInt()
    val query = IdQuery(id)

    assertEquals(query.key, id.toString())
  }

  @Test
  fun `PaginationOffsetLimitQuery should properly form the key`() {
    val id = randomString()
    val offset = randomInt()
    val limit = randomInt()

    val query = PaginationOffsetLimitQuery(id, offset, limit)

    val expectedKey = "$id-$offset-$limit"
    assertEquals(expectedKey, query.key)
  }

  @Test
  fun `PaginationQuery must use the identifier as the key`() {
    val expectedIdentifier = randomString()

    val query = PaginationQuery(expectedIdentifier)

    assertEquals(expectedIdentifier, query.key)
  }

  @Test
  fun `StringIdQuery must use the id as the key`() {
    val expectedIdentifier = randomString()

    val query = StringIdQuery(expectedIdentifier)

    assertEquals(expectedIdentifier, query.key)
  }

  @Test
  fun `IntegerIdQuery must use the id as the key`() {
    val expectedIdentifier = randomInt()

    val query = IntegerIdQuery(expectedIdentifier)

    assertEquals(expectedIdentifier.toString(), query.key)
  }
}
