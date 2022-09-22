package com.harmony.kotlin.either

import com.harmony.kotlin.common.either.Either
import com.harmony.kotlin.common.either.asSingleEither
import com.harmony.kotlin.common.either.eitherOf
import com.harmony.kotlin.error.DataNotFoundException
import com.harmony.kotlin.error.HarmonyException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EitherTests {
  @Test
  fun `eitherOf should capture defined exception`() {
    val either: Either<DataNotFoundException, String> = eitherOf<DataNotFoundException, String> {
      throw DataNotFoundException()
    }

    assertTrue { either.getLeftOrNull()!! is DataNotFoundException }
  }

  @Test
  fun `eitherOf should not capture exception out of the defined one`() {
    assertFailsWith<AnyException> {
      eitherOf<DataNotFoundException, String> {
        throw AnyException
      }
    }
  }

  @Test
  fun `asSingleEither should transform a list of Either into a Either of List`() {
    val listOfEither: List<Either<HarmonyException, String>> = listOf(Either.Right("foo"), Either.Right("bar"))

    val eitherOfList = listOfEither.asSingleEither()

    assertEquals(Either.Right(listOf("foo", "bar")), eitherOfList)
  }

  @Test
  fun `asSingleEither should transform into a Left Either when error happens`() {
    val listOfEither: List<Either<DataNotFoundException, String>> = listOf(Either.Right("foo"), Either.Left(DataNotFoundException()), Either.Right("bar"))

    val eitherOfList = listOfEither.asSingleEither()

    assertTrue { eitherOfList.isLeft() }
    assertTrue { eitherOfList.getLeftOrNull()!! is DataNotFoundException }
  }

  @Test
  fun `asSingleEither should transform a list of Either into a Either of List ignoring error when discardLeft is true`() {
    val listOfEither: List<Either<DataNotFoundException, String>> = listOf(Either.Right("foo"), Either.Left(DataNotFoundException()), Either.Right("bar"))

    val eitherOfList = listOfEither.asSingleEither(discardLeft = true)

    assertEquals(Either.Right(listOf("foo", "bar")), eitherOfList)
  }

  private object AnyException : Exception()
}
