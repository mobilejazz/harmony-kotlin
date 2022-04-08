package com.mobilejazz.kmmsample.core.feature.hackerposts

import com.harmony.kotlin.data.datasource.DataSourceMapper
import com.harmony.kotlin.data.datasource.DataSourceQueryMapper
import com.harmony.kotlin.data.datasource.VoidDeleteDataSource
import com.harmony.kotlin.data.datasource.VoidPutDataSource
import com.harmony.kotlin.data.datasource.cache.CacheSQLConfiguration
import com.harmony.kotlin.data.datasource.cache.CacheSQLStorageDataSource
import com.harmony.kotlin.data.datasource.network.GetNetworkDataSource
import com.harmony.kotlin.data.datasource.toGetRepository
import com.harmony.kotlin.data.mapper.CBORByteArrayToObject
import com.harmony.kotlin.data.mapper.CBORObjectToByteArray
import com.harmony.kotlin.data.repository.CacheRepository
import com.harmony.kotlin.data.repository.GetRepository
import com.harmony.kotlin.data.repository.withMapping
import com.harmony.kotlin.data.validator.vastra.ValidationServiceManager
import com.harmony.kotlin.data.validator.vastra.strategies.VastraValidator
import com.harmony.kotlin.data.validator.vastra.strategies.timestamp.TimestampValidationStrategy
import com.harmony.kotlin.domain.interactor.toGetInteractor
import com.mobilejazz.kmmsample.core.NetworkConfiguration
import com.mobilejazz.kmmsample.core.feature.hackerposts.data.entity.HackerNewsPostEntity
import com.mobilejazz.kmmsample.core.feature.hackerposts.data.mapper.HackerNewsPostEntityToHackerNewsPostMapper
import com.mobilejazz.kmmsample.core.feature.hackerposts.data.mapper.HackerNewsQueryToNetworkQueryMapper
import com.mobilejazz.kmmsample.core.feature.hackerposts.data.mapper.ListIntToHackerNewsPostsIdsMapper
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor.GetHackerNewsPostInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.interactor.GetHackerNewsPostsInteractor
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPostsIds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.cbor.Cbor

interface HackerNewsPostsComponent {
  fun getHackerNewsPostsInteractor(): GetHackerNewsPostsInteractor
  fun getHackerNewsPostInteractor(): GetHackerNewsPostInteractor
}

class HackerNewsPostsDefaultModule(
  private val networkConfiguration: NetworkConfiguration,
  private val coroutineDispatcher: CoroutineDispatcher,
  private val cacheSQLConfiguration: CacheSQLConfiguration,
  private val cbor: Cbor
) : HackerNewsPostsComponent {

  override fun getHackerNewsPostsInteractor(): GetHackerNewsPostsInteractor {
    return GetHackerNewsPostsInteractor(
      getHackerNewsIdsPostsRepository.toGetInteractor(coroutineDispatcher),
      getHackerNewsPostsRepository.toGetInteractor(coroutineDispatcher)
    )
  }

  override fun getHackerNewsPostInteractor(): GetHackerNewsPostInteractor {
    return GetHackerNewsPostInteractor(
      getHackerNewsPostsRepository.toGetInteractor(coroutineDispatcher)
    )
  }

  private val getHackerNewsIdsPostsRepository: GetRepository<HackerNewsPostsIds> by lazy {
    val hackerPostsIdsNetworkDataSource = GetNetworkDataSource(
      networkConfiguration.baseUrl,
      networkConfiguration.httpClient,
      ListSerializer(Int.serializer()),
      networkConfiguration.json
    )

    val hackerNewsQueryToNetworkQueryMapper = HackerNewsQueryToNetworkQueryMapper()

    val hackerNewsNetworkDataSourceWithMapper = DataSourceQueryMapper(
      hackerPostsIdsNetworkDataSource,
      VoidPutDataSource(),
      VoidDeleteDataSource(),
      hackerNewsQueryToNetworkQueryMapper,
      hackerNewsQueryToNetworkQueryMapper,
      hackerNewsQueryToNetworkQueryMapper,
    )

    hackerNewsNetworkDataSourceWithMapper.toGetRepository(ListIntToHackerNewsPostsIdsMapper())
  }

  private val getHackerNewsPostsRepository: GetRepository<HackerNewsPost> by lazy {

    val hackerPostsNetworkDataSource = GetNetworkDataSource(
      networkConfiguration.baseUrl,
      networkConfiguration.httpClient,
      HackerNewsPostEntity.serializer(),
      networkConfiguration.json
    )

    val hackerNewsQueryToNetworkQuery = HackerNewsQueryToNetworkQueryMapper()

    val hackerNewsNetworkDataSourceWithMapper = DataSourceQueryMapper(
      hackerPostsNetworkDataSource,
      VoidPutDataSource(),
      VoidDeleteDataSource(),
      hackerNewsQueryToNetworkQuery,
      hackerNewsQueryToNetworkQuery,
      hackerNewsQueryToNetworkQuery
    )

    val cacheSQLStorageDataSource = CacheSQLStorageDataSource(cacheSQLConfiguration.provideCacheDatabase("hacker-news-post"))

    val cacheHackerNewsPostDataSource: DataSourceMapper<ByteArray, HackerNewsPostEntity> = DataSourceMapper(
      cacheSQLStorageDataSource,
      cacheSQLStorageDataSource,
      cacheSQLStorageDataSource,
      CBORByteArrayToObject(cbor, HackerNewsPostEntity.serializer()),
      CBORObjectToByteArray(cbor, HackerNewsPostEntity.serializer())
    )

    val repository = CacheRepository(
      cacheHackerNewsPostDataSource,
      cacheHackerNewsPostDataSource,
      cacheHackerNewsPostDataSource,
      hackerNewsNetworkDataSourceWithMapper,
      VoidPutDataSource(),
      VoidDeleteDataSource(),
      VastraValidator(
        ValidationServiceManager(listOf(TimestampValidationStrategy()))
      )
    )

    repository.withMapping(HackerNewsPostEntityToHackerNewsPostMapper())
  }
}
