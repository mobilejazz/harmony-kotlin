package com.worldreader.core.datasource.storage.datasource.cache;

import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategy;
import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategyObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

public interface CacheBddDataSource {

  /**
   * Obtain a @link CacheObject class with the data
   * @param key - Key of the @link CacheObject
   * @return @link CacheObject
   */
  CacheObject get(String key);

  /**
   * Save a @link CacheObject into the data base
   * @param cacheObject - @link CacheObject to save
   */
  void persist(CacheObject cacheObject);

  /**
   * Remove the @link CacheObject of the data base
   * @param key - Key of the @link CacheObject
   */
  void delete(String key);

  /**
   * Check if the cache object is valid or not depending of the @CachingStrategy and if is not valid
   * we throw a @link InvalidCacheException and we remove the current @link CacheObject of the data base.
   *
   * @param cacheObject Object to check if is valid or not
   * @param strategy Type of strategy to validate the cache
   * @param <T> Should be a object of @CachingStrategyObject
   * @throws InvalidCacheException - If the @link CacheObject is not valid and we need to refresh
   */
  <T extends CachingStrategyObject> void executeValidation(CacheObject cacheObject,
      CachingStrategy<T> strategy) throws InvalidCacheException;
}
