package com.harmony.kotlin.library.oauth.domain.model

import com.harmony.kotlin.data.datasource.DeleteDataSource
import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.datasource.cache.CacheSQLStorageDataSource
import com.harmony.kotlin.data.datasource.database.CacheDatabase
import com.harmony.kotlin.data.datasource.memory.InMemoryDataSource

data class OAuthStorageConfiguration(val getDataSource: GetDataSource<ByteArray>, val putDataSource: PutDataSource<ByteArray>, val deleteDataSource: DeleteDataSource)

fun oauthStorageConfigurationInMemory(): OAuthStorageConfiguration {
  val inMemoryDataSource = InMemoryDataSource<ByteArray>()
  return OAuthStorageConfiguration(inMemoryDataSource, inMemoryDataSource, inMemoryDataSource)
}

fun oauthStorageConfigurationWithCacheSQL(cacheDatabase: CacheDatabase): OAuthStorageConfiguration {
  val storageDataSource = CacheSQLStorageDataSource(cacheDatabase)
  return OAuthStorageConfiguration(storageDataSource, storageDataSource, storageDataSource)
}

