package com.worldreader.core.datasource.storage.datasource.cache;

import android.content.Context;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.datasource.storage.datasource.cache.manager.DbBooksOpenHelper;
import com.worldreader.core.datasource.storage.datasource.cache.manager.entity.CacheBytes;
import com.worldreader.core.datasource.storage.datasource.cache.resolver.CacheBytesSQLiteResolver;
import com.worldreader.core.datasource.storage.datasource.cache.resolver.SQLiteResolver;
import com.worldreader.core.datasource.storage.security.KeyManager;

import javax.inject.Inject;

public class CacheBookBddDataSourceImpl implements CacheBookBddDataSource {

  public static final String TAG = CacheBookBddDataSource.class.getSimpleName();

  private final SQLiteResolver<CacheBytes> cacheBytesSQLiteResolver;
  private final Logger logger;

  @Inject public CacheBookBddDataSourceImpl(Context context, DbBooksOpenHelper dbBooksOpenHelper,
      KeyManager keyManager, Logger logger) {
    this.cacheBytesSQLiteResolver =
        new CacheBytesSQLiteResolver(context, dbBooksOpenHelper, keyManager);
    this.logger = logger;
  }

  @Override public CacheBytes get(String key) {
    return cacheBytesSQLiteResolver.performGet(key);
  }

  @Override public void persist(CacheBytes cacheBytes) {
    cacheBytesSQLiteResolver.performPersist(cacheBytes);
  }

  @Override public void delete(String key) {
    cacheBytesSQLiteResolver.performDelete(key);
  }
}
