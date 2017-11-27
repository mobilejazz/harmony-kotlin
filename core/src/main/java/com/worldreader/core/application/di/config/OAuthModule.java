package com.worldreader.core.application.di.config;

import com.worldreader.core.BuildConfig;
import com.worldreader.core.application.di.qualifiers.WorldreaderOAuthClientId;
import com.worldreader.core.application.di.qualifiers.WorldreaderOAuthClientSecret;
import com.worldreader.core.datasource.OAuthDataSource;
import com.worldreader.core.datasource.network.datasource.oauth.OAuthNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.oauth.OAuthNetworkDataSourceImpl2;
import com.worldreader.core.datasource.network.general.retrofit.manager.WorldreaderUserRetrofitApiManager2;
import com.worldreader.core.datasource.network.general.retrofit.services.AuthApiService2;
import com.worldreader.core.datasource.network.general.retrofit.services.OAuthApiService2;
import com.worldreader.core.datasource.storage.datasource.oauth.OAuthBdDataSource;
import com.worldreader.core.datasource.storage.datasource.oauth.OAuthBdDataSourceImpl;
import com.worldreader.core.domain.repository.OAuthRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module public class OAuthModule {

  @Provides @Singleton static OAuthRepository provideOAuthRepository(OAuthDataSource dataSource) {
    return dataSource;
  }

  @Provides @Singleton static OAuthBdDataSource provideOABdDataSource(OAuthBdDataSourceImpl oAuthBdDataSource) {
    return oAuthBdDataSource;
  }

  @Provides @Singleton @WorldreaderOAuthClientId static String provideWorldreaderOAuthClientId() {
    return BuildConfig.WORLDREADER_OAUTH_CLIENT_ID;
  }

  @Provides @Singleton @WorldreaderOAuthClientSecret static String provideWorldreaderOAuthClientSecret() {
    return BuildConfig.WORLDREADER_OAUTH_CLIENT_SECRET;
  }

  @Provides @Singleton static OAuthApiService2 provideOAuthApiService(WorldreaderUserRetrofitApiManager2 apiManager) {
    return apiManager.oAuthApiService();
  }

  @Provides @Singleton static AuthApiService2 provideAuthApiService(WorldreaderUserRetrofitApiManager2 apiManager) {
    return apiManager.authApiService();
  }

  @Provides @Singleton static OAuthNetworkDataSource provideOAuthNetworkDataSource(OAuthApiService2 oAuthApiService, AuthApiService2 authApiService,
      @WorldreaderOAuthClientId String clientId, @WorldreaderOAuthClientSecret String secret) {
    return new OAuthNetworkDataSourceImpl2(oAuthApiService, authApiService, clientId, secret);
  }

}
