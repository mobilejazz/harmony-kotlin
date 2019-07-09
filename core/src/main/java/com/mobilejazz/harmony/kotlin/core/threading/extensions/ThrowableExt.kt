package com.mobilejazz.harmony.kotlin.core.threading.extensions


fun Throwable.finalCause(): Throwable {
  return if (cause != null) {
    cause!!.finalCause()
  } else {
    this
  }
}

fun <T : Throwable> Throwable.unwrap(from: Class<T>): Throwable {

  val cause = this.cause

  return if (cause != null) {
    if (cause::class.java == from) {
      cause.unwrap(from)
    } else {
      cause
    }
  } else {
    this
  }
}