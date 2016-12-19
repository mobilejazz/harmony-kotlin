package com.worldreader.core.datasource.storage.datasource.cache.strategy.ttl;

import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategy;

import java.util.concurrent.*;

public class TtlCachingStrategy<T extends TtlCachingObject> implements CachingStrategy<T> {

  private final long ttlMillis;

  public TtlCachingStrategy(int ttl, TimeUnit timeUnit) {
    ttlMillis = timeUnit.toMillis(ttl);
  }

  @Override public boolean isValid(T data) {
    return (data.getPersistedTime() + ttlMillis) > System.currentTimeMillis();
  }
}
