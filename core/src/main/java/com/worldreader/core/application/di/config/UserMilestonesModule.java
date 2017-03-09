package com.worldreader.core.application.di.config;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.worldreader.core.application.di.qualifiers.WorldreaderOfflineDb;
import com.worldreader.core.datasource.UserMilestonesDataSource;
import com.worldreader.core.datasource.mapper.user.milestones.ListUserMilestoneEntityToListUserMilestoneMapper;
import com.worldreader.core.datasource.mapper.user.milestones.ListUserMilestoneToListUserMilestoneEntityMapper;
import com.worldreader.core.datasource.mapper.user.milestones.SetMilestoneEntityToMilestoneMapper;
import com.worldreader.core.datasource.mapper.user.milestones.UserMilestoneEntityToUserMilestoneMapper;
import com.worldreader.core.datasource.mapper.user.milestones.UserMilestoneToUserMilestoneEntityMapper;
import com.worldreader.core.datasource.mapper.user.user.UserEntityToUserMapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.network.datasource.milestones.UserMilestonesNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.milestones.UserMilestonesNetworkDataSourceImpl;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.milestones.UserMilestoneStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.milestones.StorIOUserMilestonesDbDataSourceImpl;
import com.worldreader.core.datasource.storage.mapper.milestones.ListUserMilestoneEntityDbToListUserMilestoneEntityMapper;
import com.worldreader.core.datasource.storage.mapper.milestones.ListUserMilestoneEntityToListUserMilestoneDbMapper;
import com.worldreader.core.datasource.storage.mapper.milestones.UserMilestoneDbToUserMilestoneEntityMapper;
import com.worldreader.core.datasource.storage.mapper.milestones.UserMilestoneEntityToUserMilestoneDbMapper;
import com.worldreader.core.domain.repository.UserMilestonesRepository;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module public abstract class UserMilestonesModule {

  @Binds @Singleton abstract UserMilestonesNetworkDataSource provideUserMilestoneNetworkDataSource(
      UserMilestonesNetworkDataSourceImpl impl);

  @Provides @Singleton
  static Repository.Storage<UserMilestoneEntity, UserMilestoneStorageSpecification> provideMilestonesDbRepository(
      @WorldreaderOfflineDb StorIOSQLite storIOSQLite,
      UserMilestoneEntityToUserMilestoneDbMapper toUserMilestoneDbMapper,
      ListUserMilestoneEntityToListUserMilestoneDbMapper toListMilestoneDbMapper,
      UserMilestoneDbToUserMilestoneEntityMapper toUserMilestoneEntityMapper,
      ListUserMilestoneEntityDbToListUserMilestoneEntityMapper toListUserMilestoneEntityMapper) {
    return new StorIOUserMilestonesDbDataSourceImpl(storIOSQLite, toUserMilestoneDbMapper,
        toListMilestoneDbMapper, toUserMilestoneEntityMapper, toListUserMilestoneEntityMapper);
  }

  @Provides @Singleton static UserMilestonesRepository provideUserMilestonesRepository(
      Repository.Storage<UserMilestoneEntity, UserMilestoneStorageSpecification> datasource,
      UserMilestonesNetworkDataSource network,
      Repository.Storage<UserEntity2, RepositorySpecification> userStorage,
      UserMilestoneToUserMilestoneEntityMapper toUserMilestoneEntityMapper,
      ListUserMilestoneToListUserMilestoneEntityMapper toListUserMilestoneEntityMapper,
      UserMilestoneEntityToUserMilestoneMapper toUserMilestoneMapper,
      ListUserMilestoneEntityToListUserMilestoneMapper toListUserMilestoneMapper,
      SetMilestoneEntityToMilestoneMapper toSetMilestoneMapper,
      UserEntityToUserMapper toUserMapper) {
    return new UserMilestonesDataSource(datasource, network, userStorage,
        toUserMilestoneEntityMapper, toListUserMilestoneEntityMapper, toUserMilestoneMapper,
        toListUserMilestoneMapper, toSetMilestoneMapper, toUserMapper);
  }

}
