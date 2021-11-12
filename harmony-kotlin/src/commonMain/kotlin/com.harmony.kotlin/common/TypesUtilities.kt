package com.harmony.kotlin.common

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlin.random.Random

const val ANY_ITEMS_COUNT = 10

/**
 * will execute [builder] function [itemsCount] times and will generate a list with those results
 * @param itemsCount amount of needed objects to build
 * @param builder function that will create the expected object to populate the list
 * @return a list with the generated objects
 */
fun <T> getSome(itemsCount: Int = ANY_ITEMS_COUNT, builder: (index: Int) -> T): List<T> {
  val values = mutableListOf<T>()
  for (index in 0 until itemsCount) {
    values.add(builder(index))
  }
  return values
}

/**
 * will return a null or the result of execute [builder] function
 * @param builder function that will create the expected object
 * @return null or the object created by [builder]
 */
fun <T> randomNullable(builder: () -> T): T? =
  when (randomBoolean()) {
    true -> builder()
    else -> null
  }

fun randomString(length: Int = ANY_ITEMS_COUNT): String {
  val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + ' '
  return (1..length)
    .map { allowedChars.random() }
    .joinToString("")
}

fun randomValidXmlName(length: Int = ANY_ITEMS_COUNT): String {
  val allowedChars = ('a'..'z') + ('A'..'Z')
  return (1..length)
    .map { allowedChars.random() }
    .joinToString("")
}

fun randomUUID(): Uuid = uuid4()

fun randomInt(min: Int = -100, max: Int = 100): Int = Random.nextInt(min, max)

fun randomDouble() = Random.nextDouble()

fun randomCoordinate() = Random.nextDouble(-90.0, 90.0)

fun randomBoolean(): Boolean = Random.nextBoolean()

fun randomLong(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = Random.nextLong(min, max)

fun randomByteArray(amount: Int? = null): ByteArray = Random.nextBytes(amount ?: randomInt(1, 1024))
