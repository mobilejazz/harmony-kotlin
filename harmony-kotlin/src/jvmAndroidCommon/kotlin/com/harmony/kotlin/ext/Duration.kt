package com.harmony.kotlin.ext

import java.util.Date

class Duration(internal val unit: Int, internal val value: Int) {

  val ago = calculate(from = Date(), value = -value)

  val since = calculate(from = Date(), value = value)

  private fun calculate(from: Date, value: Int): Date {
    calendar.time = from
    calendar.add(unit, value)
    return calendar.time
  }

  override fun equals(other: Any?): Boolean {
    if (other == null || other !is Duration) {
      return false
    }
    return unit == other.unit && value == other.value
  }
}
