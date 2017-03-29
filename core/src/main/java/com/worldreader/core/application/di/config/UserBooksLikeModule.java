package com.worldreader.core.application.di.config;

import android.content.Context;
import com.mobilejazz.logger.library.Logger;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.worldreader.core.application.di.qualifiers.WorldreaderOfflineDb;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.UserBooksLikeDataSource;
import com.worldreader.core.datasource.mapper.user.userbooklike.ListUserBookLikeEntityToListUserBookLikeMapper;
import com.worldreader.core.datasource.mapper.user.userbooklike.UserBookLikeEntityToUserBookLikeMapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.network.datasource.userbookslike.UserBooksLikeNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.userbookslike.UserBooksLikeNetworkDataSourceImpl;
import com.worldreader.core.datasource.network.datasource.userbookslike.UserBooksLikeNetworkRepositoryProviderImpl;
import com.worldreader.core.datasource.network.datasource.userbookslike.UserBooksLikeOfflineNetworkDataSource;
import com.worldreader.core.datasource.network.general.retrofit.services.UserBooksApiService;
import com.worldreader.core.datasource.repository.NetworkRepositoryProvider;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.userbookslike.UserBookLikeStorageSpec;
import com.worldreader.core.datasource.storage.datasource.userbookslike.StorIOUserBooksLikeDbDataSourceImpl;
import com.worldreader.core.datasource.storage.mapper.userbooklikes.ListUserBookLikeDbToListUserBookLikeEntityMapper;
import com.worldreader.core.datasource.storage.mapper.userbooklikes.ListUserBookLikeEntityToListUserBookLikeDbMapper;
import com.worldreader.core.datasource.storage.mapper.userbooklikes.UserBookLikeDbToUserBookEntityLikeMapper;
import com.worldreader.core.datasource.storage.mapper.userbooklikes.UserBookLikeEntityToUserBookLikeDbMapper;
import com.worldreader.core.domain.interactors.user.application.IsAnonymousUserInteractor;
import com.worldreader.core.domain.repository.UserBooksLikeRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

@Module public class UserBooksLikeModule {

  @Provides @Singleton @Named("user.books.like.network.datasource")
  static UserBooksLikeNetworkDataSource provideUserBooksLikeNetworkDataSource(Context context, UserBooksApiService apiService, Logger logger) {
    return new UserBooksLikeNetworkDataSourceImpl(context, apiService, logger);
  }

  @Provides @Singleton @Named("user.books.like.network.offline") static UserBooksLikeNetworkDataSource provideOfflineUserBooksNetworkDataSource() {
    return new UserBooksLikeOfflineNetworkDataSource();
  }

  @Provides @Singleton static NetworkRepositoryProvider<UserBooksLikeNetworkDataSource> provideNetworkRepositoryProvider(Reachability reachability,
      @Named("user.books.like.network.datasource") UserBooksLikeNetworkDataSource userBooksNetworkDataSource,
      @Named("user.books.like.network.offline") UserBooksLikeNetworkDataSource offlineDataSource, IsAnonymousUserInteractor interactor) {
    return new UserBooksLikeNetworkRepositoryProviderImpl(reachability, userBooksNetworkDataSource, offlineDataSource, interactor);
  }

  @Provides @Singleton
  static Repository.Storage<UserBookLikeEntity, UserBookLikeStorageSpec> provideUserBookDbRepository(@WorldreaderOfflineDb StorIOSQLite storIOSQLite,
      UserBookLikeEntityToUserBookLikeDbMapper toUserBookDbMapper, UserBookLikeDbToUserBookEntityLikeMapper toUserBookEntityMapper,
      ListUserBookLikeDbToListUserBookLikeEntityMapper toUserBookEntityListMapper,
      ListUserBookLikeEntityToListUserBookLikeDbMapper toUserBookDbListMapper) {
    return new StorIOUserBooksLikeDbDataSourceImpl(storIOSQLite, toUserBookDbMapper, toUserBookEntityMapper, toUserBookEntityListMapper,
        toUserBookDbListMapper);
  }

  @Provides @Singleton
  static UserBooksLikeRepository provideUserBooksLikeRepository(NetworkRepositoryProvider<UserBooksLikeNetworkDataSource> networkRepositoryProvider,
      UserBookLikeEntityToUserBookLikeMapper toUserBookMapper, ListUserBookLikeEntityToListUserBookLikeMapper toUserBookListMapper,
      Repository.Storage<UserBookLikeEntity, UserBookLikeStorageSpec> storage, Repository.Storage<UserEntity2, RepositorySpecification> userStorage) {
    return new UserBooksLikeDataSource(networkRepositoryProvider, storage, userStorage, toUserBookMapper, toUserBookListMapper);
  }

}
