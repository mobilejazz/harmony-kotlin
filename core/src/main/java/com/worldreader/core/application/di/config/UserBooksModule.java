package com.worldreader.core.application.di.config;

import android.content.Context;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.mobilejazz.logger.library.Logger;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.worldreader.core.application.di.qualifiers.WorldreaderOfflineDb;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.UserBooksDataSource;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.mapper.user.userbook.ListUserBookEntityToListUserBookMapper;
import com.worldreader.core.datasource.mapper.user.userbook.ListUserBookToListUserBookMapper;
import com.worldreader.core.datasource.mapper.user.userbook.UserBookEntityToUserBookMapper;
import com.worldreader.core.datasource.mapper.user.userbook.UserBookToUserBookEntityMapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.datasource.userbooks.UserBooksNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.userbooks.UserBooksNetworkDataSourceImpl;
import com.worldreader.core.datasource.network.datasource.userbooks.UserBooksNetworkRepositoryProviderImpl;
import com.worldreader.core.datasource.network.datasource.userbooks.UserBooksOfflineNetworkDataSource;
import com.worldreader.core.datasource.network.general.retrofit.manager.WorldreaderUserRetrofitApiManager2;
import com.worldreader.core.datasource.network.general.retrofit.services.UserBooksApiService;
import com.worldreader.core.datasource.network.mapper.userbooks.ListUserBookEntityToListUserBookNetworkBodyMapper;
import com.worldreader.core.datasource.network.mapper.userbooks.ListUserBookNetworkResponseToListUserBookEntityMapper;
import com.worldreader.core.datasource.network.mapper.userbooks.UserBookEntityToUserBookNetworkBodyMapper;
import com.worldreader.core.datasource.network.mapper.userbooks.UserBookNetworkResponseToUserBookEntityMapper;
import com.worldreader.core.datasource.repository.NetworkRepositoryProvider;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.userbooks.UserBookStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.userbooks.StorIOUserBooksDbDataSourceImpl;
import com.worldreader.core.datasource.storage.mapper.userbooks.UserBookDbToUserBookEntityListMapper;
import com.worldreader.core.datasource.storage.mapper.userbooks.UserBookDbToUserBookEntityMapper;
import com.worldreader.core.datasource.storage.mapper.userbooks.UserBookEntityToUserBookDbListMapper;
import com.worldreader.core.datasource.storage.mapper.userbooks.UserBookEntityToUserBookDbMapper;
import com.worldreader.core.datasource.storage.model.UserBookDb;
import com.worldreader.core.domain.interactors.user.GetFinishedBooksCountInteractor;
import com.worldreader.core.domain.interactors.user.GetFinishedBooksCountInteractorImpl;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.repository.UserBooksRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

@Module public class UserBooksModule {

  @Provides @Singleton UserBooksApiService provideUserBooksApiService(
      WorldreaderUserRetrofitApiManager2 manager) {
    return manager.userBooksApiService();
  }

  @Provides @Singleton @Named("user.books.network.datasource")
  UserBooksNetworkDataSource provideUserBooksNetworkDataSource(Context context,
      UserBooksApiService apiService,
      UserBookNetworkResponseToUserBookEntityMapper toUserBookEntityMapper,
      ListUserBookNetworkResponseToListUserBookEntityMapper toListUserBookEntityMapper,
      UserBookEntityToUserBookNetworkBodyMapper toUserBookNetworkBodyMapper,
      ListUserBookEntityToListUserBookNetworkBodyMapper toListUserBookNetworkBodyMapper,
      Logger logger) {
    return new UserBooksNetworkDataSourceImpl(context, apiService, toUserBookEntityMapper,
        toListUserBookEntityMapper, toUserBookNetworkBodyMapper, toListUserBookNetworkBodyMapper,
        logger);
  }

  @Provides @Singleton @Named("user.books.network.offline")
  UserBooksNetworkDataSource provideOfflineUserBooksNetworkDataSource() {
    return new UserBooksOfflineNetworkDataSource();
  }

  @Provides @Singleton
  NetworkRepositoryProvider<UserBooksNetworkDataSource> provideNetworkRepositoryProvider(
      Reachability reachability,
      @Named("user.books.network.datasource") UserBooksNetworkDataSource userBooksNetworkDataSource,
      @Named("user.books.network.offline") UserBooksNetworkDataSource offlineDataSource,
      IsAnonymousUserInteractor interactor) {
    return new UserBooksNetworkRepositoryProviderImpl(reachability, userBooksNetworkDataSource,
        offlineDataSource, interactor);
  }

  @Provides @Singleton
  Repository.Storage<UserBookEntity, UserBookStorageSpecification> provideUserBookDbRepository(
      @WorldreaderOfflineDb StorIOSQLite storIOSQLite,
      UserBookEntityToUserBookDbMapper toUserBookDbMapper,
      Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> toUserBookEntityMapper,
      UserBookDbToUserBookEntityListMapper toUserBookEntityListMapper,
      UserBookEntityToUserBookDbListMapper toUserBookDbListMapper) {
    return new StorIOUserBooksDbDataSourceImpl(storIOSQLite, toUserBookDbMapper,
        toUserBookEntityMapper, toUserBookEntityListMapper, toUserBookDbListMapper);
  }

  @Provides @Singleton
  Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> provideFromUserBookDbToUserBookEntity(
      Gson gson) {
    return new UserBookDbToUserBookEntityMapper(gson);
  }

  @Provides @Singleton
  Mapper<Optional<UserBookEntity>, Optional<UserBookDb>> provideFromUserBookEntityToUserBookDb(
      Gson gson) {
    return new UserBookEntityToUserBookDbMapper(gson);
  }

  @Provides @Singleton UserBooksRepository provideUserBooksRepository(
      NetworkRepositoryProvider<UserBooksNetworkDataSource> networkRepositoryProvider,
      UserBookEntityToUserBookMapper toUserBookMapper,
      ListUserBookEntityToListUserBookMapper toUserBookListMapper,
      Repository.Storage<UserBookEntity, UserBookStorageSpecification> storage,
      Repository.Storage<UserEntity2, RepositorySpecification> userStorage,
      UserBookToUserBookEntityMapper toUserBookEntityMapper,
      ListUserBookToListUserBookMapper toListUserBookEntityMapper) {
    return new UserBooksDataSource(networkRepositoryProvider, storage, userStorage,
        toUserBookMapper, toUserBookListMapper, toUserBookEntityMapper, toListUserBookEntityMapper);
  }

  @Provides @Singleton static GetFinishedBooksCountInteractor provideCountBooksFinishedInteractor(
      GetFinishedBooksCountInteractorImpl interactor) {
    return interactor;
  }
}
