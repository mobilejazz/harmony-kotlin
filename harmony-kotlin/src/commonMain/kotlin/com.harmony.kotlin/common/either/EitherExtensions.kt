package com.harmony.kotlin.common.either

/**
 * Transform a List<Either<L,R>> to Either<L,List<R>>
 * @param discardLeft - true if left values should be ignored
 */
fun <L, R> List<Either<L, R>>.asSingleEither(discardLeft: Boolean = false): Either<L, List<R>> {
  val rightValue = mutableListOf<R>()
  var leftValue: L? = null

  this.forEach {
    when {
      it.isLeft() && !discardLeft -> {
        leftValue = it.getLeftOrNull()!!
        return@forEach
      }
      it.isRight() -> {
        rightValue.add(it.get())
      }
    }
  }
  return leftValue?.let {
    Either.Left<L>(leftValue!!)
  } ?: run {
    Either.Right<List<R>>(rightValue)
  }
}
