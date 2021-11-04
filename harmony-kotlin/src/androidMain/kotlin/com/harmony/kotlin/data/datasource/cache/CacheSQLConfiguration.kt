package com.harmony.kotlin.data.datasource.cache

import android.content.Context
import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver

actual interface CacheSQLConfiguration {
  actual fun provideCacheDatabase(databaseName: String): CacheDatabase
}

class CacheSQLAndroidDefaultConfiguration(private val context: Context) : CacheSQLConfiguration {
  override fun provideCacheDatabase(databaseName: String): CacheDatabase {
    return CacheDatabase(AndroidSqliteDriver(CacheDatabase.Schema, context, "$databaseName.db"))
  }
}
