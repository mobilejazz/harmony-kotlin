package com.worldreader.core.common.date;

import java.util.*;

public interface Dates {

  int DAYS_PER_WEEK = 7;

  Date addMinutes(Date date, int minutes);

  Date minusMinutes(Date date, int minutes);

  Date addHour(Date date, int hour);

  Date minusHour(Date date, int hour);

  Date addDays(Date date, int days);

  Date minusDays(Date date, int days);

  Date addMonths(Date date, int months);

  Date minusMonths(Date date, int months);

  Date today();

  Date now();

  Date toStartOfDay(long milliseconds);

  Date toStartOfDay(Date date);

  boolean isToday(Date date);

  int daysBetween(Date dateOne, Date dateTwo);

  String format(Date date, String format);

  String format(Date date, DateFormatterStyle style);

  List<String> getWeekdays();

  String getWeekday(Date date);

  Date getStartOfWeek();

  Date getEndOfWeek();

  enum DateFormatterStyle {
    SHORT,
    MEDIUM,
    LONG
  }

  enum DayOfWeek {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private int numDay;

    DayOfWeek(int numDay) {
      this.numDay = numDay;
    }

    public int getNumDay() {
      return numDay;
    }
  }
}
