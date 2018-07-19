package com.worldreader.core.datasource;

import com.google.common.base.Optional;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.annotation.NetworkOnly;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardPeriodEntity;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.model.user.UserReadingStatsEntity;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.network.datasource.user.UserNetworkDataSource2;
import com.worldreader.core.datasource.network.model.FacebookProviderDataNetwork;
import com.worldreader.core.datasource.network.model.GoogleProviderDataNetwork;
import com.worldreader.core.datasource.network.model.RegisterProviderNetwork;
import com.worldreader.core.datasource.network.model.WorldreaderProviderDataNetwork;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.NetworkSpecification;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.repository.spec.StorageSpecification;
import com.worldreader.core.datasource.spec.user.UpdateUserCategoriesSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.LeaderboardStat;
import com.worldreader.core.domain.model.Referrer;
import com.worldreader.core.domain.model.user.GoogleProviderData;
import com.worldreader.core.domain.model.user.ReadToKidsProviderData;
import com.worldreader.core.domain.model.user.RegisterProvider;
import com.worldreader.core.domain.model.user.RegisterProviderData;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserReadingStats;
import com.worldreader.core.domain.model.user.WorldreaderProviderData;
import com.worldreader.core.domain.repository.UserRepository;

import java.util.*;

public class UserDataSource2 implements UserRepository {

  private final UserNetworkDataSource2 userNetworkDataSource;
  private final Repository<UserEntity2, RepositorySpecification> userDbDataSource;

  private final Mapper<Optional<User2>, Optional<UserEntity2>> toUserEntityMapper;
  private final Mapper<Optional<UserEntity2>, Optional<User2>> toUserMapper;
  private final Mapper<Optional<LeaderboardPeriod>, Optional<LeaderboardPeriodEntity>> toLeaderboardPeriodEntityMapper;
  private final Mapper<Optional<LeaderboardStatEntity>, Optional<LeaderboardStat>> toLeaderboardStatMapper;
  private final Mapper<Optional<UserReadingStatsEntity>, Optional<UserReadingStats>> toUserReadingStatsMapper;

  private final Logger logger;

  public UserDataSource2(UserNetworkDataSource2 userNetworkDataSource, Repository<UserEntity2, RepositorySpecification> userDbDataSource,
      Mapper<Optional<UserEntity2>, Optional<User2>> toUserMapper, Mapper<Optional<User2>, Optional<UserEntity2>> toUserEntityMapper,
      Mapper<Optional<LeaderboardPeriod>, Optional<LeaderboardPeriodEntity>> toLeaderboardPeriodEntityMapper,
      Mapper<Optional<LeaderboardStatEntity>, Optional<LeaderboardStat>> toLeaderboardStatMapper,
      final Mapper<Optional<UserReadingStatsEntity>, Optional<UserReadingStats>> toUserReadingStatsMapper, Logger logger) {
    this.userNetworkDataSource = userNetworkDataSource;
    this.userDbDataSource = userDbDataSource;
    this.toUserMapper = toUserMapper;
    this.toUserEntityMapper = toUserEntityMapper;
    this.toLeaderboardPeriodEntityMapper = toLeaderboardPeriodEntityMapper;
    this.toLeaderboardStatMapper = toLeaderboardStatMapper;
    this.toUserReadingStatsMapper = toUserReadingStatsMapper;
    this.logger = logger;
  }

  @NetworkOnly @Override
  public void register(RegisterProvider provider, RegisterProviderData<?> registerProviderData, Referrer referrer, Callback<Optional<User2>> callback) {
    final Object rawData = registerProviderData.get();
    switch (provider) {
      case FACEBOOK:
        registerUserWithFacebook(rawData, referrer, callback);
        break;
      case GOOGLE:
        registerUserWithGoogle(rawData, referrer, callback);
        break;
      case WORLDREADER:
        registerUserWithWorldreader(rawData, referrer, callback);
        break;
    }
  }

  @NetworkOnly @Override public void resetPassword(String email, final Callback<Optional<Boolean>> callback) {
    userNetworkDataSource.resetPassword(email, new Callback<Optional<Boolean>>() {
      @Override public void onSuccess(Optional<Boolean> optional) {
        notifySuccessCallback(callback, optional);
      }

      @Override public void onError(Throwable error) {
        notifyErrorCallback(callback, error);
      }
    });
  }

  @Override public void updateGoals(int pagesPerDay, int minChildAge, int maxChildAge, final Callback<Optional<User2>> callback) {
    userNetworkDataSource.updateGoals(pagesPerDay, minChildAge, maxChildAge, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @NetworkOnly @Override public void leaderboardStats(final LeaderboardPeriod period, final Callback<Optional<LeaderboardStat>> callback) {
    final LeaderboardPeriodEntity leaderboardPeriodEntity = toLeaderboardPeriodEntityMapper.transform(Optional.fromNullable(period)).get();
    userNetworkDataSource.leaderboardStats(leaderboardPeriodEntity, new Callback<Optional<LeaderboardStatEntity>>() {
      @Override public void onSuccess(Optional<LeaderboardStatEntity> optional) {
        final Optional<LeaderboardStat> toReturn = toLeaderboardStatMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void readingStats(Date from, Date to, final Callback<Optional<UserReadingStats>> callback) {
    userNetworkDataSource.readingStats(from, to, new Callback<Optional<UserReadingStatsEntity>>() {
      @Override public void onSuccess(final Optional<UserReadingStatsEntity> optional) {
        final Optional<UserReadingStats> toReturn = toUserReadingStatsMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void updateReadingStats(String bookId, int readPages, Date when, final Callback<Optional<Boolean>> callback) {
    userNetworkDataSource.updateReadingStats(bookId, readPages, when, new Callback<Optional<Boolean>>() {
      @Override public void onSuccess(Optional<Boolean> optional) {
        notifySuccessCallback(callback, optional);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void updateProfilePicture(final String profilePictureId, final Callback<Void> callback) {
    userNetworkDataSource.updateProfilePicture(profilePictureId, new Callback<Void>() {
      @Override public void onSuccess(final Void aVoid) {
        notifySuccessCallback(callback, null);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void updateBirthdate(final Date birthDate, final Callback<Void> callback) {
    userNetworkDataSource.updateBirthdate(birthDate, new Callback<Void>() {
      @Override public void onSuccess(final Void aVoid) {
        notifySuccessCallback(callback, null);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void updateEmail(final String email, final Callback<Void> callback) {
    userNetworkDataSource.updateEmail(email, new Callback<Void>() {
      @Override public void onSuccess(final Void aVoid) {
        notifySuccessCallback(callback, null);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void updateName(final String name, final Callback<Void> callback) {
    userNetworkDataSource.updateName(name, new Callback<Void>() {
      @Override public void onSuccess(final Void aVoid) {
        notifySuccessCallback(callback, null);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void sendLocalLibrary(final String localLibrary, final Callback<Boolean> callback) {
    userNetworkDataSource.sendLocalLibrary(localLibrary, new Callback<Void>() {
      @Override public void onSuccess(final Void aVoid) {
        notifySuccessCallback(callback, true);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private void registerUserWithFacebook(Object data, Referrer referrer, final Callback<Optional<User2>> callback) {
    final String facebookToken = (String) data;
    final FacebookProviderDataNetwork facebookProviderData = new FacebookProviderDataNetwork(facebookToken, referrer.getDeviceId(), referrer.getUserId());
    userNetworkDataSource.register(RegisterProviderNetwork.FACEBOOK, facebookProviderData, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private void registerUserWithGoogle(Object data, Referrer referrer, final Callback<Optional<User2>> callback) {
    final GoogleProviderData.DomainGoogleRegisterData registerData = (GoogleProviderData.DomainGoogleRegisterData) data;
    final GoogleProviderDataNetwork googleProviderData =
        new GoogleProviderDataNetwork(registerData.getGoogleTokenId(), registerData.getGoogleId(), registerData.getName(), registerData.getEmail(), referrer
            .getDeviceId(),
            referrer.getUserId());
    userNetworkDataSource.register(RegisterProviderNetwork.GOOGLE, googleProviderData, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private void registerUserWithWorldreader(Object data, Referrer referrer, final Callback<Optional<User2>> callback) {
    final WorldreaderProviderDataNetwork worldreaderProviderData;

    if (data instanceof WorldreaderProviderData.DomainWorldreaderData) {
      final WorldreaderProviderData.DomainWorldreaderData registerData = (WorldreaderProviderData.DomainWorldreaderData) data;
      worldreaderProviderData = new WorldreaderProviderDataNetwork(registerData.getUsername(), registerData.getPassword(), registerData.getEmail(), referrer
          .getDeviceId(), referrer.getUserId());
    } else if (data instanceof ReadToKidsProviderData.DomainReadToKidsData) {
      final ReadToKidsProviderData.DomainReadToKidsData readToKidsData = (ReadToKidsProviderData.DomainReadToKidsData) data;

      worldreaderProviderData =
          new WorldreaderProviderDataNetwork(readToKidsData.getUsername(), readToKidsData.getPassword(), readToKidsData.getEmail(),
              readToKidsData.getActivatorCode(), readToKidsData.getGender(), readToKidsData.getAge(), referrer.getDeviceId(), referrer.getUserId());
    } else {
      throw new UnsupportedOperationException("Provider data not supported");
    }

    userNetworkDataSource.register(RegisterProviderNetwork.WORLDREADER, worldreaderProviderData, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void get(RepositorySpecification specification, final Callback<Optional<User2>> callback) {
    if (specification instanceof NetworkSpecification) {
      getUserFromNetworkDataSource(specification, callback);
    } else if (specification instanceof UserStorageSpecification) {
      getUserFromUserDbDataSource((UserStorageSpecification) specification, callback);
    } else {
      getUserFromNetworkDataSource(specification, callback);
    }
  }

  @Override public void getAll(final RepositorySpecification specification, final Callback<Optional<List<User2>>> callback) {
    throw new IllegalStateException("Not implemented for this data source!");
  }

  @Override public void put(User2 model, RepositorySpecification specification, final Callback<Optional<User2>> callback) {
    if (specification instanceof UserStorageSpecification) {
      updateUserDbDataSource(model, specification, callback);
    } else if (specification instanceof UpdateUserCategoriesSpecification) {
      updateUserCategories(((UpdateUserCategoriesSpecification) specification), callback);
    }
  }

  @Override public void putAll(List<User2> user2s, RepositorySpecification specification, Callback<Optional<List<User2>>> callback) {
    throw new IllegalStateException("Not implemented for this data source!");
  }

  @Override public void remove(User2 model, RepositorySpecification specification, final Callback<Optional<User2>> callback) {
    if (specification instanceof StorageSpecification) {
      final UserEntity2 userEntity = toUserEntityMapper.transform(Optional.fromNullable(model)).orNull();
      userDbDataSource.remove(userEntity, specification, new Callback<Optional<UserEntity2>>() {
        @Override public void onSuccess(Optional<UserEntity2> optional) {
          final Optional<User2> toReturn = toUserMapper.transform(optional);
          notifySuccessCallback(callback, toReturn);
        }

        @Override public void onError(Throwable e) {
          notifyErrorCallback(callback, e);
        }
      });
    } else {
      throw new IllegalArgumentException("Incorrect specification requested!");
    }
  }

  @Override public void removeAll(List<User2> users, RepositorySpecification specification, Callback<Optional<List<User2>>> callback) {
    throw new IllegalStateException("Not implemented for this data source!");
  }

  private void getUserFromNetworkDataSource(final RepositorySpecification ignored, final Callback<Optional<User2>> callback) {
    userNetworkDataSource.get(RepositorySpecification.NONE, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private void getUserFromUserDbDataSource(final UserStorageSpecification specification, final Callback<Optional<User2>> callback) {
    userDbDataSource.get(specification, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(final Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private void updateUserDbDataSource(final User2 model, final RepositorySpecification specification, final Callback<Optional<User2>> callback) {
    final UserEntity2 userEntity = toUserEntityMapper.transform(Optional.fromNullable(model)).orNull();
    userDbDataSource.put(userEntity, specification, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(final Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private void updateUserCategories(UpdateUserCategoriesSpecification specification, final Callback<Optional<User2>> callback) {
    userNetworkDataSource.put(null, specification, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(Optional<UserEntity2> optional) {
        final Optional<User2> toReturn = toUserMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private <T> void notifySuccessCallback(Callback<T> callback, T result) {
    if (callback != null) {
      callback.onSuccess(result);
    }
  }

  private <T> void notifyErrorCallback(Callback<T> callback, Throwable error) {
    if (callback != null) {
      callback.onError(error);
    }
  }

}
