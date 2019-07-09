package com.mobilejazz.harmony.kotlin.core.ext

import java.util.*

object Dates {

  fun now() = Date()

  fun today() = Date()

  fun tomorrow() = setDate(value = 1)

  fun yesterday() = setDate(value = -1)

  private fun setDate(value: Int): Date {
    calendar.time = Date()
    calendar.add(Calendar.DATE, value)
    return calendar.time
  }

  fun of(year: Int = -1, month: Int = -1, day: Int = -1, hour: Int = -1, minute: Int = -1, second: Int = -1): Date {
    calendar.time = Date()
    if (year > -1) calendar.set(Calendar.YEAR, year)
    if (month > -1) calendar.set(Calendar.MONTH, month)
    if (day > -1) calendar.set(Calendar.DATE, day)
    if (hour > -1) calendar.set(Calendar.HOUR, hour)
    if (minute > -1) calendar.set(Calendar.MINUTE, minute)
    if (second > -1) calendar.set(Calendar.SECOND, second)
    return calendar.time
  }

}