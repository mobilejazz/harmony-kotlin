package com.harmony.kotlin.library.oauth.data

import com.harmony.kotlin.data.datasource.GetDataSource
import com.harmony.kotlin.data.datasource.PutDataSource
import com.harmony.kotlin.data.operation.Operation
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.data.query.Query
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.PutRepository
import com.harmony.kotlin.error.notSupportedQuery
import com.harmony.kotlin.library.oauth.data.entity.OAuthTokenEntity
import com.harmony.kotlin.library.oauth.data.query.OAuthQuery

internal class OAuthTokenRepository(
  private val putNetworkDataSource: PutDataSource<OAuthTokenEntity>,
  private val getStorageDataSource: GetDataSource<OAuthTokenEntity>,
  private val putStorageDataSource: PutDataSource<OAuthTokenEntity>
) : GetRepository<OAuthTokenEntity>, PutRepository<OAuthTokenEntity> {

  override suspend fun get(query: Query, operation: Operation): OAuthTokenEntity {
    when (query) {
      is KeyQuery -> {
        val tokenEntity = getStorageDataSource.get(query)
        return if (!tokenEntity.isValid()) {
          tokenEntity.refreshToken?.let {
            put(OAuthQuery.RefreshToken(query.key, tokenEntity.refreshToken), null)
          } ?: tokenEntity
        } else {
          tokenEntity
        }
      }

      else -> notSupportedQuery()
    }
  }

  override suspend fun put(query: Query, value: OAuthTokenEntity?, operation: Operation): OAuthTokenEntity {
    return putNetworkDataSource.put(query, value).let {
      putStorageDataSource.put(query, it)
    }
  }
}
