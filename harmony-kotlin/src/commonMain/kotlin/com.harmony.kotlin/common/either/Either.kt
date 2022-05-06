package com.harmony.kotlin.common.either

import com.harmony.kotlin.common.either.Either.Left
import com.harmony.kotlin.common.either.Either.Right

/**
 * Either implementation mainly based on arrow's implementation:
 * https://github.com/arrow-kt/arrow/blob/main/arrow-libs/core/arrow-core/src/commonMain/kotlin/arrow/core/Either.kt
 *
 * Highly recommendable to read its documentation:
 * https://arrow-kt.io/docs/apidocs/arrow-core/arrow.core/-either/
 */
sealed class Either<out LEFT, out RIGHT> {

  /**
   * The left side of the disjoint union, as opposed to the [Right] side.
   */
  data class Left<out T>(val value: T) : Either<T, Nothing>() {
    override fun isRight() = false
    override fun isLeft() = true
    override fun get(): Nothing = throw NoSuchElementException("Either.Right.value on Left")
  }

  /**
   * The right side of the disjoint union, as opposed to the [Left] side.
   */
  data class Right<out T>(val value: T) : Either<Nothing, T>() {
    override fun isRight() = true
    override fun isLeft() = false
    override fun get(): T = value
  }

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   */
  abstract fun isLeft(): Boolean

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   */
  abstract fun isRight(): Boolean

  /**
   * The contained value if the instance is [Right], will throw NoSuchElementException when it is [Left]
   */
  abstract fun get(): RIGHT

  /**
   * Applies `ifLeft` if this is a [Left] or `ifRight` if this is a [Right].
   */
  inline fun <C> fold(ifLeft: (LEFT) -> C, ifRight: (RIGHT) -> C): C = when (this) {
    is Left -> ifLeft(value)
    is Right -> ifRight(value)
  }

  /**
   * Returns the value from this [Right] or null if this is a [Left].
   */
  fun getOrNull(): RIGHT? = fold({ null }, { it })

  /**
   * Returns the value from this [Left] or null if this is a [Right].
   */
  fun getLeftOrNull(): LEFT? = fold({ it }, { null })

  /**
   *  If this is a [Left], then return the left value in [Right] or vice versa.
   */
  fun swap() = fold(
    { Right(it) },
    { Left(it) }
  )

  /**
   * The given function is applied if this is a [Right] but original [RIGHT] is returned.
   */
  inline fun tap(f: (RIGHT) -> Unit): Either<LEFT, RIGHT> = fold(
    { Left(it) },
    {
      f(it)
      Right(it)
    }
  )

  /**
   * The given function is applied if this is a [Right].
   */
  inline fun <C> map(f: (RIGHT) -> C): Either<LEFT, C> = fold(
    { Left(it) },
    { Right(f(it)) }
  )

  /**
   * The given function is applied if this is a [Left].
   */
  inline fun <C> mapLeft(f: (LEFT) -> C): Either<C, RIGHT> = fold(
    { Left(f(it)) },
    { Right(it) }
  )

  /**
   * Map over Left and Right of this Either
   * if the instance is [Left] will map with [leftMapper]
   * if the instance is [Right] will map with [rightMapper]
   */
  inline fun <C, P> bimap(leftMapper: (LEFT) -> C, rightMapper: (RIGHT) -> P): Either<C, P> = fold(
    { Left(leftMapper(it)) },
    { Right(rightMapper(it)) }
  )
}

/**
 * Binds the given function across [Right].
 *
 * @param f The function to bind across [Right].
 */
inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
  when (this) {
    is Either.Left -> this
    is Either.Right -> f(this.value)
  }

/**
 * Returns the value from this [Right] or the given argument if this is a [Left].
 */
fun <B> Either<*, B>.getOrElse(default: () -> B): B = fold({ default() }, { it })

/**
 * Returns the value from this [Right] or null if this is a [Left].
 */
fun <B> Either<*, B>.getOrNull(): B? = getOrElse { null }

/**
 * Returns the value from this [Right] or allows clients to transform [Left] to [Right] while providing access to the value of [Left].
 */
inline fun <A, B> Either<A, B>.getOrHandle(default: (A) -> B): B = fold({ default(it) }, { it })
