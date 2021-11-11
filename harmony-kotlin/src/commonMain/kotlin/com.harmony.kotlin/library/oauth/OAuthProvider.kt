package com.harmony.kotlin.library.oauth

import com.harmony.kotlin.common.Platform
import com.harmony.kotlin.common.logger.KtorHarmonyLogger
import com.harmony.kotlin.data.datasource.DataSourceMapper
import com.harmony.kotlin.data.datasource.network.DefaultUnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.UnauthorizedResolution
import com.harmony.kotlin.data.datasource.network.ktor.configureExceptionErrorMapping
import com.harmony.kotlin.data.mapper.CBORByteArrayToObject
import com.harmony.kotlin.data.mapper.CBORObjectToByteArray
import com.harmony.kotlin.data.mapper.VoidMapper
import com.harmony.kotlin.data.repository.RepositoryMapper
import com.harmony.kotlin.data.repository.SingleDeleteDataSourceRepository
import com.harmony.kotlin.domain.interactor.DeleteInteractor
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.harmony.kotlin.domain.interactor.PutInteractor
import com.harmony.kotlin.library.oauth.data.OAuthTokenRepository
import com.harmony.kotlin.library.oauth.data.datasource.network.OAuthNetworkDataSource
import com.harmony.kotlin.library.oauth.data.entity.OAuthTokenEntity
import com.harmony.kotlin.library.oauth.data.mapper.OAuthTokenEntityToOAuthTokenMapper
import com.harmony.kotlin.library.oauth.domain.interactor.*
import com.harmony.kotlin.library.oauth.domain.model.OAuthStorageConfiguration
import com.harmony.kotlin.library.oauth.domain.model.OAuthToken
import com.harmony.kotlin.library.oauth.domain.model.oauthStorageConfigurationInMemory
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

object Default {
  const val TOKEN_IDENTIFIER = "user-token-default-identifier"
}

interface OAuthComponent {
  fun authenticatePasswordCredentialInteractor(): AuthenticatePasswordCredentialInteractor
  fun getPasswordTokenInteractor(): GetPasswordTokenInteractor
  fun getApplicationTokenInteractor(): GetApplicationTokenInteractor
  fun deletePasswordTokenInteractor(): DeletePasswordTokenInteractor
}

class OAuthDefaultModule(
  private val apiPath: String,
  private val coroutineContext: CoroutineContext,
  private val clientId: String,
  private val clientSecret: String,
  private val resolution: UnauthorizedResolution = DefaultUnauthorizedResolution,
  private val basicAuthorizationCode: String, // todo: temporal until we find a way to hash the clientId and clientSecret in base 64
  private val oauthStorageConfiguration: OAuthStorageConfiguration = oauthStorageConfigurationInMemory(),
  private val moduleLogger: com.harmony.kotlin.common.logger.Logger
) : OAuthComponent {

  override fun authenticatePasswordCredentialInteractor(): AuthenticatePasswordCredentialInteractor =
    AuthenticatePasswordCredentialInteractor(putTokenInteractor, coroutineContext)

  override fun getPasswordTokenInteractor(): GetPasswordTokenInteractor = GetDefaultPasswordTokenInteractor(coroutineContext, getTokenInteractor)

  override fun getApplicationTokenInteractor(): GetApplicationTokenInteractor = GetApplicationTokenInteractor(
    coroutineContext, clientId, clientSecret,
    putTokenInteractor
  )

  override fun deletePasswordTokenInteractor(): DeletePasswordTokenInteractor = DeletePasswordTokenInteractor(coroutineContext, deleteTokenInteractor)

  @ExperimentalSerializationApi
  private val oauthRepository: RepositoryMapper<OAuthTokenEntity, OAuthToken> by lazy {
    val networkDataSource = OAuthNetworkDataSource(httpClient, apiPath, basicAuthorizationCode)

    val cbor = Cbor
    val dataSourceMapper = DataSourceMapper(
      oauthStorageConfiguration.getDataSource, oauthStorageConfiguration.putDataSource, oauthStorageConfiguration.deleteDataSource,
      CBORByteArrayToObject(cbor, OAuthTokenEntity.serializer()), CBORObjectToByteArray(cbor, OAuthTokenEntity.serializer())
    )

    val repository = OAuthTokenRepository(networkDataSource, dataSourceMapper, dataSourceMapper)

    return@lazy RepositoryMapper(repository, repository, SingleDeleteDataSourceRepository(dataSourceMapper), OAuthTokenEntityToOAuthTokenMapper(), VoidMapper())
  }

  private val putTokenInteractor: PutInteractor<OAuthToken> by lazy {
    PutInteractor(coroutineContext, oauthRepository)
  }

  private val getTokenInteractor: GetInteractor<OAuthToken> by lazy {
    GetInteractor(coroutineContext, oauthRepository)
  }

  private val deleteTokenInteractor: DeleteInteractor by lazy {
    DeleteInteractor(coroutineContext, oauthRepository)
  }

  private val httpClient: HttpClient by lazy {
    HttpClient {
      install(JsonFeature) {
        serializer = KotlinxSerializer(
          Json {
            isLenient = true
            ignoreUnknownKeys = true
          }
        )
      }
      if (Platform.IS_JVM) {
        install(Logging) {
          logger = KtorHarmonyLogger(moduleLogger)
          level = LogLevel.ALL
        }
      }
      configureExceptionErrorMapping(resolution)
      expectSuccess = false
    }
  }
}
