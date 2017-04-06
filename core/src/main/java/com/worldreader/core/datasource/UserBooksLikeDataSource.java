package com.worldreader.core.datasource;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.mapper.user.userbooklike.ListUserBookLikeEntityToListUserBookLikeMapper;
import com.worldreader.core.datasource.mapper.user.userbooklike.ListUserBookLikeToListUserBookLikeEntityMapper;
import com.worldreader.core.datasource.mapper.user.userbooklike.UserBookLikeEntityToUserBookLikeMapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.network.datasource.userbookslike.UserBooksLikeNetworkDataSource;
import com.worldreader.core.datasource.repository.NetworkRepositoryProvider;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.NetworkSpecification;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.userbookslike.GetUserBookLikeStorageSpec;
import com.worldreader.core.datasource.spec.userbookslike.PutUserBookLikeStorageSpec;
import com.worldreader.core.datasource.spec.userbookslike.UserBookLikeStorageSpec;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.repository.UserBooksLikeRepository;
import com.worldreader.core.error.user.GetUserFailException;

import javax.inject.Inject;
import java.util.List;

import static com.worldreader.core.datasource.spec.user.UserStorageSpecification.UserTarget;
import static com.worldreader.core.datasource.spec.user.UserStorageSpecification.target;

public class UserBooksLikeDataSource implements UserBooksLikeRepository {

  private final NetworkRepositoryProvider<UserBooksLikeNetworkDataSource> networkProvider;
  private final Repository.Storage<UserBookLikeEntity, UserBookLikeStorageSpec> storage;
  private final Repository.Storage<UserEntity2, RepositorySpecification> userStorage;

  private final Mapper<Optional<UserBookLikeEntity>, Optional<UserBookLike>> toUserBookLikeMapper;
  private final Mapper<Optional<List<UserBookLikeEntity>>, Optional<List<UserBookLike>>> toUserBookLikeListMapper;
  private final Mapper<Optional<List<UserBookLike>>, Optional<List<UserBookLikeEntity>>> toListUserBookLikeMapper;

  @Inject public UserBooksLikeDataSource(final NetworkRepositoryProvider<UserBooksLikeNetworkDataSource> networkProvider,
      final Storage<UserBookLikeEntity, UserBookLikeStorageSpec> storage, final Storage<UserEntity2, RepositorySpecification> userStorage,
      UserBookLikeEntityToUserBookLikeMapper toUserBookLikeMapper, ListUserBookLikeEntityToListUserBookLikeMapper toUserBookLikeListMapper,
      ListUserBookLikeToListUserBookLikeEntityMapper toListUserBookLikeMapper) {
    this.networkProvider = networkProvider;
    this.storage = storage;
    this.userStorage = userStorage;
    this.toUserBookLikeMapper = toUserBookLikeMapper;
    this.toUserBookLikeListMapper = toUserBookLikeListMapper;
    this.toListUserBookLikeMapper = toListUserBookLikeMapper;
  }

  @Override public void get(final RepositorySpecification specification, final Callback<Optional<UserBookLike>> callback) {
    if (specification instanceof UserBookLikeStorageSpec) {
      final UserStorageSpecification userStorageSpecification =
          UserStorageSpecification.target(((UserBookLikeStorageSpec) specification).getTarget());
      getConcreteUserEntityId(userStorageSpecification, new Callback<String>() {
        @Override public void onSuccess(final String userId) {
          final UserBookLikeStorageSpec getUserBookLikeStorageSpec = new GetUserBookLikeStorageSpec(userStorageSpecification.getIdentifier(), userId);
          storage.get(getUserBookLikeStorageSpec, new Callback<Optional<UserBookLikeEntity>>() {
            @Override public void onSuccess(final Optional<UserBookLikeEntity> optional) {
              final Optional<UserBookLike> response = toUserBookLikeMapper.transform(optional);
              notifySuccessCallback(callback, response);
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
    } else {
      throw new IllegalArgumentException("Not implemented!");
    }
  }

  @Override public void getAll(final RepositorySpecification specification, final Callback<Optional<List<UserBookLike>>> callback) {
    if (specification instanceof UserBookLikeStorageSpec) {
      final UserStorageSpecification userSpec = UserStorageSpecification.target(((UserBookLikeStorageSpec) specification).getTarget());
      getConcreteUserEntityId(userSpec, new Callback<String>() {
        @Override public void onSuccess(final String userId) {
          final UserBookLikeStorageSpec userBookStorageSpecification = (UserBookLikeStorageSpec) specification;
          userBookStorageSpecification.setUserId(userId);
          storage.getAll(userBookStorageSpecification, new Callback<Optional<List<UserBookLikeEntity>>>() {
            @Override public void onSuccess(final Optional<List<UserBookLikeEntity>> listOptional) {
              final Optional<List<UserBookLike>> response = toUserBookLikeListMapper.transform(listOptional);
              notifySuccessCallback(callback, response);
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
    } else if (specification instanceof NetworkSpecification) {
      networkProvider.getRealNetwork().getAll(specification, new Callback<Optional<List<UserBookLikeEntity>>>() {
        @Override public void onSuccess(final Optional<List<UserBookLikeEntity>> listOptional) {
          final Optional<List<UserBookLike>> response = toUserBookLikeListMapper.transform(listOptional);
          notifySuccessCallback(callback, response);
        }

        @Override public void onError(final Throwable e) {
          notifyErrorCallback(callback, e);
        }
      });
    } else {
      throw new IllegalArgumentException("Not implemented!");
    }
  }

  @Override
  public void put(final UserBookLike userBookLike, final RepositorySpecification specification, final Callback<Optional<UserBookLike>> callback) {

  }

  @Override public void putAll(final List<UserBookLike> userBookLikes, final RepositorySpecification specification,
      final Callback<Optional<List<UserBookLike>>> callback) {
    if (specification instanceof UserBookLikeStorageSpec) {
      final UserStorageSpecification userSpec = UserStorageSpecification.target(((UserBookLikeStorageSpec) specification).getTarget());
      getConcreteUserEntityId(userSpec, new Callback<String>() {
        @Override public void onSuccess(final String userId) {
          final UserBookLikeStorageSpec userBookStorageSpecification = (UserBookLikeStorageSpec) specification;
          userBookStorageSpecification.setUserId(userId);
          final List<UserBookLikeEntity> entities = toListUserBookLikeMapper.transform(Optional.fromNullable(userBookLikes)).get();
          storage.putAll(entities, userBookStorageSpecification, new Callback<Optional<List<UserBookLikeEntity>>>() {
            @Override public void onSuccess(final Optional<List<UserBookLikeEntity>> listOptional) {
              final Optional<List<UserBookLike>> response = toUserBookLikeListMapper.transform(listOptional);
              notifySuccessCallback(callback, response);
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
    } else if (specification instanceof NetworkSpecification) {
      final List<UserBookLikeEntity> entities = toListUserBookLikeMapper.transform(Optional.fromNullable(userBookLikes)).get();
      networkProvider.getRealNetwork().putAll(entities, specification, new Callback<Optional<List<UserBookLikeEntity>>>() {
        @Override public void onSuccess(final Optional<List<UserBookLikeEntity>> optional) {
          final Optional<List<UserBookLike>> response = toUserBookLikeListMapper.transform(optional);
          notifySuccessCallback(callback, response);
        }

        @Override public void onError(final Throwable e) {
          notifyErrorCallback(callback, e);
        }
      });
    } else {
      throw new IllegalArgumentException("Not implemented!");
    }
  }

  @Override
  public void remove(final UserBookLike userBookLike, final RepositorySpecification specification, final Callback<Optional<UserBookLike>> callback) {

  }

  @Override public void removeAll(final List<UserBookLike> userBookLikes, final RepositorySpecification specification,
      final Callback<Optional<List<UserBookLike>>> callback) {

  }

  private void getConcreteUserEntityId(UserStorageSpecification spec, final Callback<String> callback) {
    userStorage.get(spec, new Callback<Optional<UserEntity2>>() {
      @Override public void onSuccess(final Optional<UserEntity2> optional) {
        if (optional.isPresent()) {
          notifySuccessCallback(callback, optional.get().getId());
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

  @Override public void like(final String bookId, final Callback<Optional<UserBookLike>> callback) {
    networkProvider.get().likeBook(bookId, new Callback<Optional<UserBookLikeEntity>>() {
      @Override public void onSuccess(final Optional<UserBookLikeEntity> userBookLikeEntityOptional) {

        final UserStorageSpecification userStorageSpecification = target(UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);

        getConcreteUserEntityId(userStorageSpecification, new Callback<String>() {
          @Override public void onSuccess(final String userId) {
            final PutUserBookLikeStorageSpec spec = new PutUserBookLikeStorageSpec(bookId, userId);
            final UserBookLikeEntity userBookLikeEntity = userBookLikeEntityOptional.get();
            final UserBookLikeEntity userBookLikeEntityToUpdate = new UserBookLikeEntity.Builder(userBookLikeEntity).withUserId(userId).build();
            storage.put(userBookLikeEntityToUpdate, spec, new Callback<Optional<UserBookLikeEntity>>() {
              @Override public void onSuccess(final Optional<UserBookLikeEntity> userBookLikeEntityOptional) {
                final Optional<UserBookLike> response = toUserBookLikeMapper.transform(userBookLikeEntityOptional);
                notifySuccessCallback(callback, response);
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

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void like(final List<String> bookIds, final Callback<Optional<List<UserBookLike>>> callback) {
  }

  @Override public void unlike(final String bookId, final Callback<Optional<UserBookLike>> callback) {
    networkProvider.get().unlikeBook(bookId, new Callback<Optional<UserBookLikeEntity>>() {
      @Override public void onSuccess(final Optional<UserBookLikeEntity> userBookLikeEntityOptional) {

        final UserStorageSpecification userStorageSpecification = target(UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);

        getConcreteUserEntityId(userStorageSpecification, new Callback<String>() {
          @Override public void onSuccess(final String userId) {
            final PutUserBookLikeStorageSpec spec = new PutUserBookLikeStorageSpec(bookId, userId);

            final UserBookLikeEntity userBookLikeEntity = userBookLikeEntityOptional.get();
            final UserBookLikeEntity userBookLikeEntityToUpdate = new UserBookLikeEntity.Builder(userBookLikeEntity).withUserId(userId).build();

            storage.put(userBookLikeEntityToUpdate, spec, new Callback<Optional<UserBookLikeEntity>>() {
              @Override public void onSuccess(final Optional<UserBookLikeEntity> userBookLikeEntityOptional) {
                final Optional<UserBookLike> response = toUserBookLikeMapper.transform(userBookLikeEntityOptional);
                notifySuccessCallback(callback, response);
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

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void unlike(final List<String> bookIds, final Callback<Optional<List<UserBookLike>>> callback) {

  }
}
