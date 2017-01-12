package com.worldreader.core.datasource.storage.datasource.cache.strategy.timestamp;

import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategy;

public class TimestampCachingStrategy<T extends TimestampCachingObject> implements CachingStrategy<T> {

  private static long HOUR = 3600000; //miliseconds

  @Override public boolean isValid(T data) {
    return data != null && validJustTwentyFourHours(data);
  }

  private boolean validJustTwentyFourHours(T data) {
    long difference = System.currentTimeMillis() - data.getTimestamp();
    long diffHour = difference / HOUR;
    return diffHour < 24;
  }
}
