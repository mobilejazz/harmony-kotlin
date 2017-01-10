package com.worldreader.core.datasource.storage.datasource.cache;

import com.mobilejazz.logger.library.Logger;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheObject;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.CacheTableMeta;
import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategy;
import com.worldreader.core.datasource.storage.datasource.cache.strategy.CachingStrategyObject;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import java.util.List;

public class CacheBddDataSourceImp implements CacheBddDataSource {

  private static final String TAG = CacheBddDataSource.class.getSimpleName();

  private StorIOSQLite storIOSQLite;
  private Logger logger;

  public CacheBddDataSourceImp(StorIOSQLite storIOSQLite, Logger logger) {
    this.storIOSQLite = storIOSQLite;
    this.logger = logger;
  }

  @Override public CacheObject get(String key) {
    List<CacheObject> cacheObject = storIOSQLite.get()
        .listOfObjects(CacheObject.class)
        .withQuery(Query.builder().table(CacheTableMeta.TABLE).where(CacheTableMeta.COLUMN_KEY + " = ?").whereArgs(key).build())
        .prepare()
        .executeAsBlocking();

    logger.d(TAG, "get() - Key: " + key + " - Response size: " + cacheObject.size());

    return cacheObject.size() > 0 ? cacheObject.get(0) : null;
  }

  @Override public void persist(CacheObject cacheObject) {
    logger.d(TAG, "persist() - Key: " + cacheObject.getKey());

    storIOSQLite.put().object(cacheObject).prepare().executeAsBlocking();
  }

  @Override public void delete(String key) {
    logger.d(TAG, "delete() - Key: " + key);

    storIOSQLite.delete()
        .byQuery(DeleteQuery.builder().table(CacheTableMeta.TABLE).where(CacheTableMeta.COLUMN_KEY + " = ?").whereArgs(key).build())
        .prepare()
        .executeAsBlocking();
  }

  @Override public <T extends CachingStrategyObject> void executeValidation(CacheObject cacheObject, CachingStrategy<T> strategy)
      throws InvalidCacheException {
    if (cacheObject == null) {
      throw new InvalidCacheException();
    } else {
      if (!strategy.isValid((T) cacheObject)) {
        delete(cacheObject.getKey());
        throw new InvalidCacheException();
      }
    }
  }
}
