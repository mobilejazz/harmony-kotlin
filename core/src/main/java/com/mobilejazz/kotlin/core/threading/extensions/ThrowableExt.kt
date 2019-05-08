package com.mobilejazz.kotlin.core.threading.extensions


fun Throwable.finalCause(): Throwable {
  return if (cause != null) {
    cause!!.finalCause()
  } else {
    this
  }
}