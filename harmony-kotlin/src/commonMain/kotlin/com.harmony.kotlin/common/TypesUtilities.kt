package com.harmony.kotlin.common

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import io.ktor.utils.io.core.toByteArray
import kotlin.random.Random
import kotlin.random.nextUInt

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

fun randomInt(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int = Random.nextInt(min, max)

fun randomIntRange(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): IntRange {
  val newMin = Random.nextInt(min, max)
  val newMax = Random.nextInt(newMin, max)
  return IntRange(newMin, newMax)
}

fun randomDouble(min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE) = Random.nextDouble(min, max)

fun randomCoordinate() = Random.nextDouble(-90.0, 90.0)

fun randomBoolean(): Boolean = Random.nextBoolean()

fun randomLong(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE) = Random.nextLong(min, max)

fun randomByteArray(amount: Int? = null): ByteArray = Random.nextBytes(amount ?: randomInt(1, 1024))

fun randomByte(): Byte = Random.nextBytes(1).first()

fun randomUShort(min: UShort = UShort.MIN_VALUE, max: UShort = UShort.MAX_VALUE) =
  Random.nextUInt(min.toUInt(), max.toUInt()).toUShort()

fun randomUInt(min: UInt = UInt.MIN_VALUE, max: UInt = UInt.MAX_VALUE) =
  Random.nextUInt(min, max)

fun randomUByte(min: UByte = UByte.MIN_VALUE, max: UByte = UByte.MAX_VALUE) =
  Random.nextUInt(min.toUInt(), max.toUInt()).toUByte()

fun randomByteArray() = randomString().toByteArray()

fun randomByteArrayList(): List<ByteArray> {
  val elements = randomInt(0, 10)
  return (0..elements).map { randomByteArray() }.toList()
}

fun randomIntList(): List<Int> {
  val elements = randomInt(0, 50)
  return (0..elements).map { randomInt() }.toList()
}

fun randomStringList(): List<String> {
  val elements = randomInt(0, 50)
  return (0..elements).map { randomString() }.toList()
}

fun randomPair() = Pair(randomString(), randomString())

fun randomPairList() = (0..randomInt(0, 50)).map { Pair(randomString().removeSpaces(), randomString().removeSpaces()) }.toList()
