package com.harmony.kotlin.ext

import java.util.Calendar

val Int.year: Duration
  get() = Duration(unit = Calendar.YEAR, value = this)

val Int.years: Duration
  get() = year

val Int.month: Duration
  get() = Duration(unit = Calendar.MONTH, value = this)

val Int.months: Duration
  get() = month

val Int.week: Duration
  get() = Duration(unit = Calendar.WEEK_OF_MONTH, value = this)

val Int.weeks: Duration
  get() = week

val Int.fortnight: Duration
  get() = Duration(unit = Calendar.WEEK_OF_MONTH, value = this * 2)

val Int.fortnights: Duration
  get() = fortnight

val Int.day: Duration
  get() = Duration(unit = Calendar.DATE, value = this)

val Int.days: Duration
  get() = day

val Int.hour: Duration
  get() = Duration(unit = Calendar.HOUR, value = this)

val Int.hours: Duration
  get() = hour

val Int.minute: Duration
  get() = Duration(unit = Calendar.MINUTE, value = this)

val Int.minutes: Duration
  get() = minute

val Int.second: Duration
  get() = Duration(unit = Calendar.SECOND, value = this)

val Int.seconds: Duration
  get() = second

const val MILLISECONDS_IN_DAY = 86_400_000
const val DAYS_IN_WEEK = 7

fun Long.fromMillistoDays(): Int {
  return (this / MILLISECONDS_IN_DAY).toInt()
}

fun Long.fromMillisToWeeks(): Int {
  return this.fromMillistoDays() / DAYS_IN_WEEK
}
