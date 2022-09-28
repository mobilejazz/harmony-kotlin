package com.harmony.kotlin.common.either

/**
 * Transform a block of code into a Either
 */
inline fun <reified L : Throwable, R> eitherOf(block: () -> R): Either<L, R> {
  return try {
    Either.Right(block())
  } catch (e: Throwable) {
    if (e is L) {
      Either.Left(e)
    } else {
      throw e
    }
  }
}
