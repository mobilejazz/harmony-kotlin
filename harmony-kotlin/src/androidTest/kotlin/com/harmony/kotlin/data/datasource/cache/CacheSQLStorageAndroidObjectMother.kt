package com.harmony.kotlin.data.datasource.cache

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver

actual fun cacheDatabaseTests(): CacheDatabase {
  val app = ApplicationProvider.getApplicationContext<Application>()
  val driver = AndroidSqliteDriver(CacheDatabase.Schema, app, "tests")
  return CacheDatabase(driver)
}
