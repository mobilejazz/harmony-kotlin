package com.worldreader.core.datasource.storage.datasource.cache.strategy.timestamp;

import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategyObject;

public interface TimestampCachingObject extends CachingStrategyObject {

  long getTimestamp();
}
