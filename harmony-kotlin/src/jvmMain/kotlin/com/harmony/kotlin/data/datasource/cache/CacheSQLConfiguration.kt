package com.harmony.kotlin.data.datasource.cache

import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver.Companion.IN_MEMORY

actual interface CacheSQLConfiguration {
  actual fun provideCacheDatabase(databaseName: String): CacheDatabase
}

class CacheSQLNativeDefaultConfiguration : CacheSQLConfiguration {
  val driver: SqlDriver = JdbcSqliteDriver(IN_MEMORY)

  override fun provideCacheDatabase(databaseName: String): CacheDatabase {
    return CacheDatabase(driver)
  }
}
