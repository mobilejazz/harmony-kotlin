package com.mobilejazz.kotlin.core.ext

import android.content.Context
import android.util.TypedValue
import java.util.*

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

fun Long.fromMillistoDays(): Int {
  return (this / 86400000).toInt()
}

fun Long.fromMillisToWeeks(): Int {
  return this.fromMillistoDays() / 7
}

fun Int.sp(ctx: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), ctx.resources.displayMetrics)

fun Int.dp(ctx: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), ctx.resources.displayMetrics)

fun Float.digits(numOfDigits: Int): String {
  return String.format("%.${numOfDigits}f", this)
}