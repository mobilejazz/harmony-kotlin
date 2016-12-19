package com.worldreader.core.datasource.storage.datasource.cache;

import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheBytes;

public interface CacheBookBddDataSource {

  /**
   * Obtain a @link CacheBytes class with the data
   *
   * @param key - Key of the @link CacheObject
   * @return @link CacheObject
   */
  CacheBytes get(String key);

  /**
   * Save a @link CacheBytes into the data base
   */
  void persist(CacheBytes cacheBytes);

  /**
   * Remove the @link CacheBytes of the data base
   *
   * @param key - Key of the @link CacheBytes
   */
  void delete(String key);
}
