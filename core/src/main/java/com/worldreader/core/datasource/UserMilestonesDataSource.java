package com.worldreader.core.datasource;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.MilestoneEntity;
import com.worldreader.core.datasource.model.user.milestones.MilestonesEntityFactory;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.network.datasource.milestones.UserMilestonesNetworkDataSource;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.milestones.GetAllUserMilestoneStorageSpec;
import com.worldreader.core.datasource.spec.milestones.GetUserMilestoneStorageSpec;
import com.worldreader.core.datasource.spec.milestones.UserMilestoneStorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.Milestone;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.repository.UserMilestonesRepository;
import com.worldreader.core.error.user.GetUserFailException;

import javax.inject.Inject;
import java.util.*;

public class UserMilestonesDataSource implements UserMilestonesRepository {

  private final Repository<UserMilestoneEntity, UserMilestoneStorageSpecification> storage;
  private final UserMilestonesNetworkDataSource network;

  private final Repository.Storage<UserEntity2, RepositorySpecification> userStorage;

  private final Mapper<Optional<UserMilestone>, Optional<UserMilestoneEntity>>
      toUserMilestoneEntityMapper;
  private final Mapper<Optional<List<UserMilestone>>, Optional<List<UserMilestoneEntity>>>
      toListUserMilestoneEntityMapper;

  private final Mapper<Optional<UserMilestoneEntity>, Optional<UserMilestone>>
      toUserMilestoneMapper;
  private final Mapper<Optional<List<UserMilestoneEntity>>, Optional<List<UserMilestone>>>
      toListUserMilestoneMapper;

  private final Mapper<Set<MilestoneEntity>, Set<Milestone>> toSetMilestoneMapper;

  private final Mapper<Optional<UserEntity2>, Optional<User2>> toUserMapper;

  @Inject public UserMilestonesDataSource(
      final Repository<UserMilestoneEntity, UserMilestoneStorageSpecification> storage,
      final UserMilestonesNetworkDataSource network,
      final Repository.Storage<UserEntity2, RepositorySpecification> userStorage,
      final Mapper<Optional<UserMilestone>, Optional<UserMilestoneEntity>> toUserMilestoneEntityMapper,
      final Mapper<Optional<List<UserMilestone>>, Optional<List<UserMilestoneEntity>>> toListUserMilestoneEntityMapper,
      final Mapper<Optional<UserMilestoneEntity>, Optional<UserMilestone>> toMilestoneMapper,
      final Mapper<Optional<List<UserMilestoneEntity>>, Optional<List<UserMilestone>>> toListUserMilestoneMapper,
      final Mapper<Set<MilestoneEntity>, Set<Milestone>> toSetMilestoneMapper,
      final Mapper<Optional<UserEntity2>, Optional<User2>> toUserMapper) {
    this.storage = storage;
    this.network = network;
    this.userStorage = userStorage;
    this.toUserMilestoneEntityMapper = toUserMilestoneEntityMapper;
    this.toListUserMilestoneEntityMapper = toListUserMilestoneEntityMapper;
    this.toUserMilestoneMapper = toMilestoneMapper;
    this.toListUserMilestoneMapper = toListUserMilestoneMapper;
    this.toSetMilestoneMapper = toSetMilestoneMapper;
    this.toUserMapper = toUserMapper;
  }

  @Override public void get(final RepositorySpecification specification,
      final Callback<Optional<UserMilestone>> callback) {
    if (specification instanceof UserMilestoneStorageSpecification) {
      getFromUserMilestoneStorage((UserMilestoneStorageSpecification) specification, callback);
    } else {
      throw new IllegalArgumentException("Specification is not registered!");
    }
  }

  private void getFromUserMilestoneStorage(final UserMilestoneStorageSpecification spec,
      final Callback<Optional<UserMilestone>> callback) {
    getUserEntityId(spec, new Callback<String>() {
      @Override public void onSuccess(final String userId) {
        final GetUserMilestoneStorageSpec getUserMilestoneStorageSpec =
            new GetUserMilestoneStorageSpec(spec.getMilestoneId(), userId);

        storage.get(getUserMilestoneStorageSpec, new Callback<Optional<UserMilestoneEntity>>() {
          @Override public void onSuccess(final Optional<UserMilestoneEntity> optional) {
            final Optional<UserMilestone> toReturn = toUserMilestoneMapper.transform(optional);
            notifySuccessCallback(callback, toReturn);
          }

          @Override public void onError(final Throwable e) {
            notifyErrorCallback(callback, e);
          }
        });
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserMilestone>>> callback) {
    if (specification instanceof GetAllUserMilestoneStorageSpec) {
      getAllFromUserMilestoneStorage((UserMilestoneStorageSpecification) specification, callback);
    } else {
      throw new IllegalArgumentException("Specification is not registered!");
    }
  }

  private void getAllFromUserMilestoneStorage(final UserMilestoneStorageSpecification specification,
      final Callback<Optional<List<UserMilestone>>> callback) {
    getUserEntityId(specification, new Callback<String>() {
      @Override public void onSuccess(final String userId) {
        specification.setUserId(userId);
        storage.getAll(specification, new Callback<Optional<List<UserMilestoneEntity>>>() {
          @Override public void onSuccess(final Optional<List<UserMilestoneEntity>> optional) {
            final Optional<List<UserMilestone>> toReturn =
                toListUserMilestoneMapper.transform(optional);
            notifySuccessCallback(callback, toReturn);
          }

          @Override public void onError(final Throwable e) {
            notifyErrorCallback(callback, e);
          }
        });
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override
  public void put(final UserMilestone milestone, final RepositorySpecification specification,
      final Callback<Optional<UserMilestone>> callback) {
    if (specification instanceof UserMilestoneStorageSpecification) {
      final UserMilestoneEntity entity =
          toUserMilestoneEntityMapper.transform(Optional.fromNullable(milestone)).orNull();
      putFromUserMilestoneStorage(entity, (UserMilestoneStorageSpecification) specification,
          callback);
    } else {
      throw new IllegalArgumentException("Specification is not registered!");
    }
  }

  private void putFromUserMilestoneStorage(final UserMilestoneEntity entity,
      final UserMilestoneStorageSpecification specification,
      final Callback<Optional<UserMilestone>> callback) {
    storage.put(entity, specification, new Callback<Optional<UserMilestoneEntity>>() {
      @Override public void onSuccess(final Optional<UserMilestoneEntity> optional) {
        final Optional<UserMilestone> toReturn = toUserMilestoneMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void putAll(final List<UserMilestone> milestones,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserMilestone>>> callback) {
    if (specification instanceof UserMilestoneStorageSpecification) {
      final List<UserMilestoneEntity> entities =
          toListUserMilestoneEntityMapper.transform(Optional.fromNullable(milestones)).orNull();
      putAllFromUserMilestoneStorage((UserMilestoneStorageSpecification) specification, callback,
          entities);
    } else {
      throw new IllegalArgumentException("Specification is not registered!");
    }
  }

  private void putAllFromUserMilestoneStorage(final UserMilestoneStorageSpecification specification,
      final Callback<Optional<List<UserMilestone>>> callback,
      final List<UserMilestoneEntity> entities) {
    storage.putAll(entities, specification, new Callback<Optional<List<UserMilestoneEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserMilestoneEntity>> optional) {
        final Optional<List<UserMilestone>> toReturn =
            toListUserMilestoneMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override
  public void remove(final UserMilestone milestone, final RepositorySpecification specification,
      final Callback<Optional<UserMilestone>> callback) {
    if (specification instanceof UserMilestoneStorageSpecification) {
      final UserMilestoneEntity entity =
          toUserMilestoneEntityMapper.transform(Optional.fromNullable(milestone)).orNull();
      removeFromUserMilestoneStorage((UserMilestoneStorageSpecification) specification, callback,
          entity);
    } else {
      throw new IllegalArgumentException("Specification is not registered!");
    }
  }

  private void removeFromUserMilestoneStorage(final UserMilestoneStorageSpecification specification,
      final Callback<Optional<UserMilestone>> callback, final UserMilestoneEntity entity) {
    storage.remove(entity, specification, new Callback<Optional<UserMilestoneEntity>>() {
      @Override public void onSuccess(final Optional<UserMilestoneEntity> optional) {
        final Optional<UserMilestone> toReturn = toUserMilestoneMapper.transform(optional);
        notifySuccessCallback(callback, toReturn);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void removeAll(final List<UserMilestone> milestones,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserMilestone>>> callback) {
    if (specification instanceof UserMilestoneStorageSpecification) {
      removeAllFromUserMilestoneStorage(milestones,
          (UserMilestoneStorageSpecification) specification, callback);
    } else {
      throw new IllegalArgumentException("Specification is not registered!");
    }
  }

  private void removeAllFromUserMilestoneStorage(final List<UserMilestone> milestones,
      final UserMilestoneStorageSpecification spec,
      final Callback<Optional<List<UserMilestone>>> callback) {
    getUserEntityId(spec, new Callback<String>() {
      @Override public void onSuccess(final String userId) {
        spec.setUserId(userId);
        final List<UserMilestoneEntity> entities =
            toListUserMilestoneEntityMapper.transform(Optional.fromNullable(milestones)).orNull();
        storage.removeAll(entities, spec, new Callback<Optional<List<UserMilestoneEntity>>>() {
          @Override public void onSuccess(final Optional<List<UserMilestoneEntity>> optional) {
            final Optional<List<UserMilestone>> toReturn =
                toListUserMilestoneMapper.transform(optional);
            notifySuccessCallback(callback, toReturn);
          }

          @Override public void onError(final Throwable e) {
            notifyErrorCallback(callback, e);
          }
        });
      }

      @Override public void onError(final Throwable e) {
        callback.onError(e);
      }
    });
  }

  @Override public void getAllUserMilestones(final Callback<Set<Milestone>> callback) {
    final Set<MilestoneEntity> milestones = MilestonesEntityFactory.createAllMilestones();
    final Set<Milestone> transform = toSetMilestoneMapper.transform(milestones);
    notifySuccessCallback(callback, transform);
  }

  @Override public void updateMilestones(final List<UserMilestone> entities,
      final Callback<Optional<User2>> callback) {
    final List<UserMilestoneEntity> transformed =
        toListUserMilestoneEntityMapper.transform(Optional.fromNullable(entities)).orNull();
    network.updateMilestones(transformed, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(final Optional<UserEntity2> optional) {
        final Optional<User2> transform = toUserMapper.transform(optional);
        notifySuccessCallback(callback, transform);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  private void getUserEntityId(final UserMilestoneStorageSpecification spec,
      final Callback<String> callback) {
    final UserStorageSpecification userSpec = UserStorageSpecification.target(spec.getUserTarget());
    userStorage.get(userSpec, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(final Optional<UserEntity2> userEntity2Optional) {
        if (userEntity2Optional.isPresent()) {
          notifySuccessCallback(callback, userEntity2Optional.get().getId());
        } else {
          notifyErrorCallback(callback, new GetUserFailException());
        }
      }

      @Override public void onError(final Throwable e) {
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
