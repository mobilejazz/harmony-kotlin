package com.worldreader.core.common.date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;
import java.util.*;

public class JodaDates implements Dates {

  @Inject public JodaDates() {
  }

  @Override public Date addMinutes(Date date, int minutes) {
    DateTime dateIncrementDays = asDateTime(date).plusMinutes(minutes);
    return asDate(dateIncrementDays);
  }

  @Override public Date minusMinutes(Date date, int minutes) {
    DateTime dateIncrementDays = asDateTime(date).minusMinutes(minutes);
    return asDate(dateIncrementDays);
  }

  @Override public Date addHour(Date date, int hour) {
    DateTime dateIncrementHours = asDateTime(date).plusHours(hour);
    return asDate(dateIncrementHours);
  }

  @Override public Date minusHour(Date date, int hour) {
    DateTime dateHours = asDateTime(date).minusHours(hour);
    return asDate(dateHours);
  }

  @Override public Date addDays(Date date, int days) {
    DateTime dateIncrementDays = asDateTime(date).plusDays(days);
    return asDate(dateIncrementDays);
  }

  @Override public Date minusDays(Date date, int days) {
    DateTime dateIncrementDays = asDateTime(date).minusDays(days);
    return asDate(dateIncrementDays);
  }

  @Override public Date today() {
    return new Date();
  }

  @Override public Date addMonths(Date date, int months) {
    DateTime dateIncrementDays = asDateTime(date).plusMonths(months);
    return asDate(dateIncrementDays);
  }

  @Override public Date minusMonths(Date date, int months) {
    DateTime dateIncrementDays = asDateTime(date).minusDays(months);
    return asDate(dateIncrementDays);
  }

  @Override public Date now() {
    return new Date();
  }

  @Override public Date toStartOfDay(long milliseconds) {
    return toStartOfDay(new Date(milliseconds));
  }

  @Override public Date toStartOfDay(Date date) {
    return new DateTime(date).withTimeAtStartOfDay().toDate();
  }

  @Override public boolean isToday(Date date) {
    return ((new DateTime(date).toLocalDate()).equals(new LocalDate()));
  }

  @Override public int daysBetween(Date dateOne, Date dateTwo) {
    return Days.daysBetween(asDateTime(dateOne), asDateTime(dateTwo)).getDays();
  }

  @Override public String format(Date date, String format) {
    DateTime dateTime = new DateTime(date);
    DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
    return dateTime.toString(formatter);
  }

  @Override public String format(Date date, DateFormatterStyle style) {
    DateTime dateTime = new DateTime(date);

    DateTimeFormatter formatter;
    switch (style) {
      case SHORT:
        formatter = DateTimeFormat.shortDate();
        break;
      case MEDIUM:
        formatter = DateTimeFormat.mediumDate();
        break;
      case LONG:
        formatter = DateTimeFormat.longDate();
        break;
      default:
        formatter = DateTimeFormat.mediumDate();
        break;
    }

    return dateTime.toString(formatter);
  }

  @Override public List<String> getWeekdays() {
    List<String> weekdays = new ArrayList<>(DayOfWeek.values().length);
    LocalDate date = new LocalDate();

    for (DayOfWeek day : DayOfWeek.values()) {
      date = date.withDayOfWeek(day.getNumDay());
      weekdays.add(DateTimeFormat.forPattern("EE").print(date));
    }

    return weekdays;
  }

  @Override public String getWeekday(Date date) {
    DateTime dateTime = asDateTime(date);
    return DateTimeFormat.forPattern("EE").print(dateTime);
  }

  @Override public Date getStartOfWeek() {
    return new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY)
        .toDateTimeAtStartOfDay()
        .toDate();
  }

  @Override public Date getEndOfWeek() {
    return new LocalDate().withDayOfWeek(DateTimeConstants.SUNDAY)
        .toDateTimeAtStartOfDay()
        .toDate();
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private DateTime asDateTime(Date date) {
    return date == null ? null : new DateTime(date);
  }

  private Date asDate(DateTime dateTime) {
    return dateTime == null ? null : dateTime.toDate();
  }
}
