package com.worldreader.core.application.di.config;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.worldreader.core.application.di.qualifiers.WorldreaderOfflineDb;
import com.worldreader.core.datasource.UserScoreDataSource;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.mapper.user.score.UserScoreEntityToUserScoreMapper;
import com.worldreader.core.datasource.mapper.user.score.UserScoreToUserScoreEntityMapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.network.datasource.score.UserScoreNetworkDataSource;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.score.UserScoreNetworkSpecification;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.score.StorIOUserScoreDbDataSource;
import com.worldreader.core.datasource.storage.datasource.score.UserScoreStorageDataSource;
import com.worldreader.core.datasource.storage.mapper.score.UserScoreDbToUserScoreEntityMapper;
import com.worldreader.core.datasource.storage.mapper.score.UserScoreEntityToUserScoreDbCollectionMapper;
import com.worldreader.core.datasource.storage.mapper.score.UserScoreEntityToUserScoreDbMapper;
import com.worldreader.core.datasource.storage.model.UserScoreDb;
import com.worldreader.core.domain.model.user.UserScore;
import com.worldreader.core.domain.repository.UserScoreRepository;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.List;

@Module public class UserScoreModule {

  @Provides @Singleton
  public Repository.Network<UserScoreEntity, UserScoreNetworkSpecification> providerUserScoreNetwork(
      UserScoreNetworkDataSource networkDataSource) {
    return networkDataSource;
  }

  @Provides @Singleton
  Repository.Storage<UserScoreEntity, UserScoreStorageSpecification> providerUserScoreStorage(
      @WorldreaderOfflineDb StorIOSQLite storIOSQLite,
      Mapper<Optional<UserScoreDb>, Optional<UserScoreEntity>> toUserScoreEntity,
      Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb,
      Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScoreDb>>> toUserScoreDbCollection) {
    return new StorIOUserScoreDbDataSource(toUserScoreEntity, toUserScoreDb,
        toUserScoreDbCollection, storIOSQLite);
  }

  @Provides @Singleton Repository<UserScore, RepositorySpecification> provideUserScoreDataSource(
      UserScoreDataSource userScoreDataSource) {
    return userScoreDataSource;
  }

  @Provides @Singleton UserScoreStorageDataSource provideUserScoreStorageDataSource(
      Repository.Storage<UserScoreEntity, UserScoreStorageSpecification> storage) {
    return (UserScoreStorageDataSource) storage;
  }

  @Provides @Singleton UserScoreRepository provideUserScoreRepository(
      Repository<UserScore, RepositorySpecification> userScoreRepository) {
    return (UserScoreRepository) userScoreRepository;
  }

  //region Mapper
  @Provides @Singleton
  Mapper<Optional<UserScoreDb>, Optional<UserScoreEntity>> provideUserScoreDbToUserScoreEntityMapper(
      final Gson gson) {
    return new UserScoreDbToUserScoreEntityMapper(gson);
  }

  @Provides @Singleton
  Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> providerUserScoreEntityToUserScoreDbMapper(
      final Gson gson) {
    return new UserScoreEntityToUserScoreDbMapper(gson);
  }

  @Provides @Singleton
  Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScoreDb>>> providerUserScoreEntityToUserScoreDbCollectionMapper(
      Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb) {
    return new UserScoreEntityToUserScoreDbCollectionMapper(toUserScoreDb);
  }

  @Provides @Singleton
  Mapper<Optional<UserScore>, Optional<UserScoreEntity>> provideUserScoreToUserScoreEntityMapper() {
    return new UserScoreToUserScoreEntityMapper();
  }

  @Provides @Singleton
  Mapper<Optional<UserScoreEntity>, Optional<UserScore>> provideUserScoreEntityToUserScoreMapper() {
    return new UserScoreEntityToUserScoreMapper();
  }
  //endregion

}
