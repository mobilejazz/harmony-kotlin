package com.worldreader.core.datasource.storage.datasource.cache.strategy.timestamp;

import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategy;

public class TimestampCachingStrategy<T extends TimestampCachingObject>
    implements CachingStrategy<T> {

  private long HOUR = 3600000; //miliseconds

  @Override public boolean isValid(T data) {
    if (data != null) {
      return validJustTwentyFourHours(data);
    } else {
      return false;
    }
  }

  private boolean validJustTwentyFourHours(T data) {
    long difference = System.currentTimeMillis() - data.getTimestamp();
    long diffHour = difference / HOUR;
    return diffHour < 24;
  }
}
