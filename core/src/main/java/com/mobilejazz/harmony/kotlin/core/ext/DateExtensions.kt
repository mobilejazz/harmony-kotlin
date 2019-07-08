package com.mobilejazz.harmony.kotlin.core.ext

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

internal val calendar: Calendar by lazy {
  getInstance()
}

internal val today: Date by lazy {
  val currentDate = Date()
  Dates.of(currentDate.currentYear, currentDate.monthOfYear, currentDate.dayOfMonth)
}

operator fun Date.plus(duration: Duration): Date {
  calendar.time = this
  calendar.add(duration.unit, duration.value)
  return calendar.time
}

operator fun Date.minus(duration: Duration): Date {
  calendar.time = this
  calendar.add(duration.unit, -duration.value)
  return calendar.time
}

fun Date?.differenceInMonths(other: Date?): Int {
  return if (this != null && other != null) {
    val year = calendar.apply { time = this@differenceInMonths }.get(YEAR)
    val month = calendar.get(MONTH)
    val otherYear = calendar.apply { time = other }.get(YEAR)
    val otherMonth = calendar.get(MONTH)

    val diffInYears = otherYear - year
    diffInYears * 12 + (otherMonth - month)
  } else {
    0
  }


}

fun Date.timeIntervalSinceNow(): Long {
  return Date().time - this.time
}

fun Date?.differenceInDays(other: Date?): Int {
  return if (this != null && other != null) (other.time - this.time).fromMillistoDays() else 0
}

fun Date.differenceInWeeks(other: Date): Int {
  return (other.time - this.time).fromMillisToWeeks()
}

fun Date.with(year: Int = -1, month: Int = -1, day: Int = -1, hour: Int = -1, minute: Int = -1, second: Int = -1): Date {
  calendar.time = this
  if (year > -1) calendar.set(YEAR, year)
  if (month > -1) calendar.set(MONTH, month - 1)
  if (day > -1) calendar.set(DATE, day - 1)
  if (hour > -1) calendar.set(HOUR, hour)
  if (minute > -1) calendar.set(MINUTE, minute)
  if (second > -1) calendar.set(SECOND, second)
  return calendar.time
}

fun Date.with(weekday: Int = -1): Date {
  calendar.time = this
  if (weekday > -1) calendar.set(WEEK_OF_MONTH, weekday)
  return calendar.time
}

val Date.minutOfTheHour: Int
  get() = calendar.apply { time = this@minutOfTheHour }.get(MINUTE)

val Date.hourOfTheDay: Int
  get() = calendar.apply { time = this@hourOfTheDay }.get(HOUR_OF_DAY)

val Date.dayOfYeah: Int
  get() = calendar.apply { time = this@dayOfYeah }.get(DAY_OF_YEAR)

val Date.dayOfMonth: Int
  get() = calendar.apply { time = this@dayOfMonth }.get(DAY_OF_MONTH)

val Date.monthOfYear: Int
  get() = calendar.apply { time = this@monthOfYear }.get(MONTH)

val Date.week: Int
  get() = calendar.apply { time = this@week }.get(WEEK_OF_YEAR)

val Date.currentYear: Int
  get() = calendar.apply { time = this@currentYear }.get(YEAR)

fun Date.formatedDateText(format: String = "EEE, MMM d, yyyy") = SimpleDateFormat(format, Locale.UK).format(this)

fun String.toDate(format: String = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"): Date? {
  return try {
    SimpleDateFormat(format, Locale.UK).parse(this)
  } catch (e: Exception) {
    null
  }
}

//in terms of day and year
infix fun Date?.isBefore(other: Date?): Boolean {
  return if (null == this || null == other) {
    false
  } else {
    val thisDay = calendar.apply { time = this@isBefore }.get(DAY_OF_YEAR)
    val otherDay = calendar.apply { time = other }.get(DAY_OF_YEAR)
    val thisYear = calendar.apply { time = this@isBefore }.get(YEAR)
    val otherYear = calendar.apply { time = other }.get(YEAR)
    (thisDay < otherDay && thisYear == otherYear) || thisYear < otherYear
  }
}

//in terms of day and year
infix fun Date?.isAfter(other: Date?): Boolean {
  return if (null == this || null == other) {
    false
  } else {
    val thisDay = calendar.apply { time = this@isAfter }.get(DAY_OF_YEAR)
    val otherDay = calendar.apply { time = other }.get(DAY_OF_YEAR)
    val thisYear = calendar.apply { time = this@isAfter }.get(YEAR)
    val otherYear = calendar.apply { time = other }.get(YEAR)
    (thisDay > otherDay && thisYear == otherYear) || thisYear > otherYear
  }
}

//in terms of day and year
infix fun Date?.same(other: Date?): Boolean {
  return if (null == this || null == other) {
    false
  } else {
    val thisDay = calendar.apply { time = this@same }.get(DAY_OF_YEAR)
    val otherDay = calendar.apply { time = other }.get(DAY_OF_YEAR)
    val thisYear = calendar.apply { time = this@same }.get(YEAR)
    val otherYear = calendar.apply { time = other }.get(YEAR)
    thisDay == otherDay && thisYear == otherYear
  }
}