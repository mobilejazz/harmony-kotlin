package com.worldreader.core.datasource.storage.datasource.cache.strategy.ttl;

import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategyObject;

public interface TtlCachingObject extends CachingStrategyObject {

  long getPersistedTime();
}
