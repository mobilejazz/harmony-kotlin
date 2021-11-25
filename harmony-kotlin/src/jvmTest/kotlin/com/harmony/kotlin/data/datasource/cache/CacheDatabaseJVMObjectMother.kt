package com.harmony.kotlin.data.datasource.cache

import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual fun cacheDatabaseTests(): CacheDatabase {
  val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  CacheDatabase.Schema.create(driver)
  return CacheDatabase(driver)
}
