package com.harmony.kotlin.data.datasource.cache

import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual interface CacheSQLConfiguration {
  actual fun provideCacheDatabase(databaseName: String): CacheDatabase
}

class CacheSQLNativeDefaultConfiguration : CacheSQLConfiguration {
  override fun provideCacheDatabase(databaseName: String): CacheDatabase {
    return CacheDatabase(NativeSqliteDriver(CacheDatabase.Schema, "$databaseName.db"))
  }
}
