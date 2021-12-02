package com.harmony.kotlin.data.datasource.cache

import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual fun cacheDatabaseTests(): CacheDatabase {
  return CacheDatabase(NativeSqliteDriver(CacheDatabase.Schema, "tests.db"))
}
