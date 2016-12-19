package com.worldreader.core.datasource.storage.datasource.cache.strategy;

public interface CachingStrategy<T extends CachingStrategyObject> {

  boolean isValid(T data);
}
