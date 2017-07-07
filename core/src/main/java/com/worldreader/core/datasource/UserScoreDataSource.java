package com.worldreader.core.datasource;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.score.UserScoreNetworkSpecification;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.score.UserScoreStorageDataSource;
import com.worldreader.core.domain.model.user.UserScore;
import com.worldreader.core.domain.repository.UserScoreRepository;
import com.worldreader.core.error.score.UserScoreStoragePutOperationFailException;

import javax.inject.Inject;
import java.util.*;

public class UserScoreDataSource implements UserScoreRepository {

  private final Repository.Network<UserScoreEntity, UserScoreNetworkSpecification> network;
  private final UserScoreStorageDataSource storage;
  private final Mapper<Optional<UserScore>, Optional<UserScoreEntity>> toUserScoreEntity;
  private final Mapper<Optional<UserScoreEntity>, Optional<UserScore>> toUserScore;

  @Inject public UserScoreDataSource(
      final Repository.Network<UserScoreEntity, UserScoreNetworkSpecification> network,
      final UserScoreStorageDataSource storage,
      final Mapper<Optional<UserScore>, Optional<UserScoreEntity>> toUserScoreEntity,
      final Mapper<Optional<UserScoreEntity>, Optional<UserScore>> toUserScore) {
    this.network = network;
    this.storage = storage;
    this.toUserScoreEntity = toUserScoreEntity;
    this.toUserScore = toUserScore;
  }

  @Override public void get(final RepositorySpecification specification,
      final Callback<Optional<UserScore>> callback) {
    Preconditions.checkArgument(specification instanceof UserScoreStorageSpecification,
        "get() - specification not supported");

    final UserScoreStorageSpecification storageSpecification =
        (UserScoreStorageSpecification) specification;

    storage.get(storageSpecification, new Callback<Optional<UserScoreEntity>>() {
      @Override public void onSuccess(final Optional<UserScoreEntity> userScoreEntityOptional) {
        final Optional<UserScore> userScoreOp = toUserScore.transform(userScoreEntityOptional);

        notifySuccessCallback(callback, userScoreOp);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserScore>>> callback) {
    throw new UnsupportedOperationException("getAll() not supported");
  }

  @Override public void put(final UserScore userScore, final RepositorySpecification specification,
      final Callback<Optional<UserScore>> callback) {
    final Optional<UserScoreEntity> userScoreEntityOp =
        toUserScoreEntity.transform(Optional.fromNullable(userScore));

    if (userScoreEntityOp.isPresent()) {
      if (specification instanceof UserScoreStorageSpecification) {
        final UserScoreStorageSpecification scoreStorageSpecification =
            (UserScoreStorageSpecification) specification;

        storage.put(userScoreEntityOp.get(), scoreStorageSpecification,
            new Callback<Optional<UserScoreEntity>>() {
              @Override
              public void onSuccess(final Optional<UserScoreEntity> userScoreEntityOptional) {
                final Optional<UserScore> userScoreOp =
                    toUserScore.transform(userScoreEntityOptional);

                notifySuccessCallback(callback, userScoreOp);
              }

              @Override public void onError(final Throwable e) {
                notifyErrorCallback(callback, new UserScoreStoragePutOperationFailException());
              }
            });
      } else if (specification instanceof UserScoreNetworkSpecification) {
        final UserScoreNetworkSpecification networkSpecification =
            (UserScoreNetworkSpecification) specification;
        network.put(userScoreEntityOp.get(), networkSpecification,
            new Callback<Optional<UserScoreEntity>>() {
              @Override
              public void onSuccess(final Optional<UserScoreEntity> userScoreEntityOptional) {
                final Optional<UserScore> userScoreOp =
                    toUserScore.transform(userScoreEntityOptional);

                notifySuccessCallback(callback, userScoreOp);
              }

              @Override public void onError(final Throwable e) {
                notifyErrorCallback(callback, e);
              }
            });
      } else {
        throw new UnsupportedOperationException("put() - specification not supported!");
      }
    } else {
      notifyErrorCallback(callback, new Throwable());
    }
  }

  @Override
  public void putAll(final List<UserScore> userScores, final RepositorySpecification specification,
      final Callback<Optional<List<UserScore>>> callback) {
    throw new UnsupportedOperationException("putAll() not supported");
  }

  @Override
  public void remove(final UserScore userScore, final RepositorySpecification specification,
      final Callback<Optional<UserScore>> callback) {
    throw new UnsupportedOperationException("remove() not supported");
  }

  @Override public void removeAll(final List<UserScore> userScores,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserScore>>> callback) {
    throw new UnsupportedOperationException("removeAll() not supported");
  }

  @Override public void removeAll(final RepositorySpecification specification,
      final Callback<Void> callback) {
    Preconditions.checkArgument(specification instanceof UserScoreStorageSpecification,
        "removeAll() - specification not supported");

    final UserScoreStorageSpecification storageSpecification =
        (UserScoreStorageSpecification) specification;
    storage.removeAll(storageSpecification, new Callback<Void>() {
      @Override public void onSuccess(final Void result) {
        notifySuccessCallback(callback, result);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void getTotalUserScore(final String userId, final Callback<Integer> callback) {
    storage.getTotalUserScore(userId, new Callback<Integer>() {
      @Override public void onSuccess(final Integer result) {
        notifySuccessCallback(callback, result);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void getTotalUserScoreUnsynced(final String userId, final Callback<Integer> callback) {
    storage.getTotalUserScoreUnSynched(userId, new Callback<Integer>() {
      @Override public void onSuccess(final Integer result) {
        notifySuccessCallback(callback, result);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  //region Private methods
  private <T> void notifySuccessCallback(final Callback<T> callback, final T result) {
    if (callback != null) {
      callback.onSuccess(result);
    }
  }

  private <T> void notifyErrorCallback(final Callback<T> callback, final Throwable error) {
    if (callback != null) {
      callback.onError(error);
    }
  }
  //endregion
}
