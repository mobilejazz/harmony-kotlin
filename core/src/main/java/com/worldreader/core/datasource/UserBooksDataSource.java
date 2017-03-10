package com.worldreader.core.datasource;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.datasource.userbooks.UserBooksNetworkDataSource;
import com.worldreader.core.datasource.repository.NetworkRepositoryProvider;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksCollectionIdsStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.GetUserBookStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.PutUserBookStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.RemoveUserBookStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.UserBookNetworkSpecification;
import com.worldreader.core.datasource.spec.userbooks.UserBookStorageSpecification;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.repository.UserBooksRepository;
import com.worldreader.core.error.user.GetUserFailException;
import com.worldreader.core.error.userbook.UserBookNotFoundException;

import javax.inject.Inject;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserBooksDataSource implements UserBooksRepository {

  private final NetworkRepositoryProvider<UserBooksNetworkDataSource> networkProvider;
  private final Repository.Storage<UserBookEntity, UserBookStorageSpecification> storage;
  private final Repository.Storage<UserEntity2, RepositorySpecification> userStorage;

  private final Mapper<Optional<UserBookEntity>, Optional<UserBook>> toUserBookMapper;
  private final Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBook>>>
      toUserBookListMapper;
  private final Mapper<Optional<UserBook>, Optional<UserBookEntity>> toUserBookEntityMapper;
  private final Mapper<Optional<List<UserBook>>, Optional<List<UserBookEntity>>>
      toListUserBookEntityMapper;

  @Inject public UserBooksDataSource(
      final NetworkRepositoryProvider<UserBooksNetworkDataSource> networkProvider,
      final Repository.Storage<UserBookEntity, UserBookStorageSpecification> storage,
      final Repository.Storage<UserEntity2, RepositorySpecification> userStorage,
      final Mapper<Optional<UserBookEntity>, Optional<UserBook>> toUserBookMapper,
      final Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBook>>> toUserBookListMapper,
      final Mapper<Optional<UserBook>, Optional<UserBookEntity>> toUserBookEntityMapper,
      final Mapper<Optional<List<UserBook>>, Optional<List<UserBookEntity>>> toListUserBookEntityMapper) {
    this.networkProvider = networkProvider;
    this.storage = storage;
    this.userStorage = userStorage;
    this.toUserBookMapper = toUserBookMapper;
    this.toUserBookListMapper = toUserBookListMapper;
    this.toUserBookEntityMapper = toUserBookEntityMapper;
    this.toListUserBookEntityMapper = toListUserBookEntityMapper;
  }

  @Override public void like(final String bookId, final Callback<Optional<UserBook>> callback) {
    getUserBookByBookIdFromStorage(bookId, new Callback<UserBookEntity>() {
      @Override public void onSuccess(final UserBookEntity userBookEntity) {
        // Send like book to the networkProvider
        networkProvider.get().likeBook(userBookEntity, new Callback<Optional<UserBookEntity>>() {
          @Override public void onSuccess(final Optional<UserBookEntity> responseNetwork) {
            final PutUserBookStorageSpec spec =
                new PutUserBookStorageSpec(userBookEntity.getBookId(), userBookEntity.getUserId());

            // Save the UserBookEntity to the storage
            storage.put(responseNetwork.get(), spec, new Callback<Optional<UserBookEntity>>() {
              @Override
              public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {

                // Return the UserBookLikeEntity
                if (callback != null) {
                  callback.onSuccess(toUserBookMapper.transform(userBookEntityOptional));
                }
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

  @Override public void unlike(String bookId, final Callback<Optional<UserBook>> callback) {
    getUserBookByBookIdFromStorage(bookId, new Callback<UserBookEntity>() {
      @Override public void onSuccess(final UserBookEntity userBookEntity) {
        networkProvider.get().unlikeBook(userBookEntity, new Callback<Optional<UserBookEntity>>() {
          @Override public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
            final PutUserBookStorageSpec spec =
                new PutUserBookStorageSpec(userBookEntity.getBookId(), userBookEntity.getUserId());

            storage.put(userBookEntityOptional.get(), spec,
                new Callback<Optional<UserBookEntity>>() {
                  @Override
                  public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
                    final Optional<UserBook> response =
                        toUserBookMapper.transform(userBookEntityOptional);

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

  @Override public void finish(String bookId, final Callback<Optional<UserBook>> callback) {
    getUserBookByBookIdFromStorage(bookId, new Callback<UserBookEntity>() {
      @Override public void onSuccess(final UserBookEntity userBookEntity) {
        networkProvider.get().finishBook(userBookEntity, new Callback<Optional<UserBookEntity>>() {
          @Override public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
            final PutUserBookStorageSpec spec =
                new PutUserBookStorageSpec(userBookEntity.getBookId(), userBookEntity.getUserId());

            storage.put(userBookEntityOptional.get(), spec,
                new Callback<Optional<UserBookEntity>>() {
                  @Override public void onSuccess(final Optional<UserBookEntity> ubeSavedOp) {
                    final Optional<UserBook> response = toUserBookMapper.transform(ubeSavedOp);

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

  @Override public void favorite(String bookId, final Callback<Optional<UserBook>> callback) {
    getUserBookByBookIdFromStorage(bookId, new Callback<UserBookEntity>() {
      @Override public void onSuccess(final UserBookEntity userBookEntity) {
        networkProvider.get()
            .markBookAsFavorite(userBookEntity, new Callback<Optional<UserBookEntity>>() {
              @Override
              public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
                final PutUserBookStorageSpec spec =
                    new PutUserBookStorageSpec(userBookEntity.getBookId(),
                        userBookEntity.getUserId());

                storage.put(userBookEntityOptional.get(), spec,
                    new Callback<Optional<UserBookEntity>>() {
                      @Override public void onSuccess(final Optional<UserBookEntity> ubeSavedOp) {
                        final Optional<UserBook> response = toUserBookMapper.transform(ubeSavedOp);

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

  @Override public void unfavorite(String bookId, final Callback<Optional<UserBook>> callback) {
    getUserBookByBookIdFromStorage(bookId, new Callback<UserBookEntity>() {
      @Override public void onSuccess(final UserBookEntity userBookEntity) {
        networkProvider.get()
            .removeBookAsFavorite(userBookEntity, new Callback<Optional<UserBookEntity>>() {
              @Override
              public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
                final PutUserBookStorageSpec spec =
                    new PutUserBookStorageSpec(userBookEntity.getBookId(),
                        userBookEntity.getUserId());

                storage.put(userBookEntityOptional.get(), spec,
                    new Callback<Optional<UserBookEntity>>() {
                      @Override public void onSuccess(final Optional<UserBookEntity> ubeSavedOp) {
                        final Optional<UserBook> response = toUserBookMapper.transform(ubeSavedOp);

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

  @Override public void assignCollection(final String bookId, final String collectionId,
      final Callback<Optional<UserBook>> callback) {
    getUserBookByBookIdFromStorage(bookId, new Callback<UserBookEntity>() {
      @Override public void onSuccess(final UserBookEntity userBookEntity) {
        networkProvider.get()
            .assignCollection(collectionId, userBookEntity,
                new Callback<Optional<UserBookEntity>>() {
                  @Override
                  public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
                    final PutUserBookStorageSpec spec =
                        new PutUserBookStorageSpec(userBookEntity.getBookId(),
                            userBookEntity.getUserId());

                    storage.put(userBookEntityOptional.get(), spec,
                        new Callback<Optional<UserBookEntity>>() {
                          @Override
                          public void onSuccess(final Optional<UserBookEntity> ubeSavedOp) {
                            final Optional<UserBook> response =
                                toUserBookMapper.transform(ubeSavedOp);

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

  @Override
  public void unassignCollection(final String collectionId, final Callback<Void> callback) {
    getFirstFoundUseEntityId(new Callback<String>() {
      @Override public void onSuccess(final String userId) {
        final GetAllUserBooksCollectionIdsStorageSpec spec =
            new GetAllUserBooksCollectionIdsStorageSpec(userId);

        storage.getAll(spec, new Callback<Optional<List<UserBookEntity>>>() {
          @Override public void onSuccess(final Optional<List<UserBookEntity>> listOptional) {
            if (listOptional.isPresent()) {
              final List<UserBookEntity> userBookEntities = listOptional.get();
              final List<UserBookEntity> responseNetwork = new ArrayList<>();
              final List<Throwable> responseNetworkError = new ArrayList<>();

              for (final UserBookEntity userBookEntity : userBookEntities) {
                networkProvider.get()
                    .unassignCollection(collectionId, userBookEntity,
                        new Callback<Optional<UserBookEntity>>() {
                          @Override public void onSuccess(
                              final Optional<UserBookEntity> userBookEntityOptional) {
                            if (userBookEntityOptional.isPresent()) {
                              responseNetwork.add(userBookEntityOptional.get());
                            } else {
                              responseNetworkError.add(new Throwable());
                            }

                            final PutUserBookStorageSpec spec =
                                new PutUserBookStorageSpec(userBookEntity.getBookId(),
                                    userBookEntity.getUserId());

                            // TODO: 09/02/2017 @Jose should we improve the implementation, if we can combine the network and the storage as a batch request.
                            storage.put(userBookEntity, spec, null);

                            if ((responseNetwork.size() + responseNetworkError.size()
                                == userBookEntities.size())) {
                              notifySuccessCallback(callback, null);
                            }
                          }

                          @Override public void onError(final Throwable e) {
                            if ((responseNetwork.size() + responseNetworkError.size()
                                == userBookEntities.size())) {
                              notifyErrorCallback(callback, e);
                            }
                          }
                        });
              }

            } else {
              notifyErrorCallback(callback, new Throwable());
            }
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

  @Override public void get(RepositorySpecification specification,
      final Callback<Optional<UserBook>> callback) {
    if (specification instanceof RepositorySpecification.SimpleRepositorySpecification) {
      final RepositorySpecification.SimpleRepositorySpecification spec =
          (RepositorySpecification.SimpleRepositorySpecification) specification;
      getFirstFoundUseEntityId(new Callback<String>() {
        @Override public void onSuccess(final String userId) {
          final GetUserBookStorageSpec getUserBookStorageSpec =
              new GetUserBookStorageSpec(spec.getIdentifier(), userId);

          storage.get(getUserBookStorageSpec, new Callback<Optional<UserBookEntity>>() {
            @Override public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
              final Optional<UserBook> response =
                  toUserBookMapper.transform(userBookEntityOptional);
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
    } else if (specification instanceof UserStorageSpecification) {
      final UserStorageSpecification userStorageSpecification =
          (UserStorageSpecification) specification;
      getConcreteUserEntityId(userStorageSpecification, new Callback<String>() {
        @Override public void onSuccess(final String userId) {
          final GetUserBookStorageSpec getUserBookStorageSpec =
              new GetUserBookStorageSpec(userStorageSpecification.getIdentifier(), userId);
          storage.get(getUserBookStorageSpec, new Callback<Optional<UserBookEntity>>() {
            @Override public void onSuccess(final Optional<UserBookEntity> optional) {
              final Optional<UserBook> response = toUserBookMapper.transform(optional);
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

  }

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserBook>>> callback) {
    checkNotNull(specification, "specification != null");
    if (specification instanceof UserBookStorageSpecification) {
      getAllUserBooksFromStorage((UserBookStorageSpecification) specification, callback);
    } else if (specification instanceof UserBookNetworkSpecification) {
      getAllUserBooksFromNetwork((UserBookNetworkSpecification) specification, callback);
    } else {
      throw new IllegalArgumentException("specification not registered!");
    }
  }

  private void getAllUserBooksFromStorage(final UserBookStorageSpecification specification,
      final Callback<Optional<List<UserBook>>> callback) {
    final UserStorageSpecification userSpec =
        UserStorageSpecification.target(specification.getTarget());
    getConcreteUserEntityId(userSpec, new Callback<String>() {
      @Override public void onSuccess(final String userId) {
        specification.setUserId(userId);
        storage.getAll(specification, new Callback<Optional<List<UserBookEntity>>>() {
          @Override public void onSuccess(final Optional<List<UserBookEntity>> listOptional) {
            final Optional<List<UserBook>> response = toUserBookListMapper.transform(listOptional);
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

  private void getAllUserBooksFromNetwork(final UserBookNetworkSpecification spec,
      final Callback<Optional<List<UserBook>>> callback) {
    networkProvider.getRealNetwork().getAll(spec, new Callback<Optional<List<UserBookEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserBookEntity>> optional) {
        final Optional<List<UserBook>> response = toUserBookListMapper.transform(optional);
        notifySuccessCallback(callback, response);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void put(UserBook model, RepositorySpecification specification,
      Callback<Optional<UserBook>> callback) {
    throw new UnsupportedOperationException("put() not supported");
  }

  @Override public void putAll(List<UserBook> userBooks, RepositorySpecification specification,
      final Callback<Optional<List<UserBook>>> callback) {
    if (specification instanceof UserBookStorageSpecification) {
      putAllUserBooksFromStorage(userBooks, (UserBookStorageSpecification) specification, callback);
    } else if (specification instanceof UserBookNetworkSpecification) {
      putAllUserBooksFromNetwork(userBooks, (UserBookNetworkSpecification) specification, callback);
    } else {
      throw new UnsupportedOperationException("specification not registered!");
    }
  }

  private void putAllUserBooksFromStorage(final List<UserBook> userBooks,
      final UserBookStorageSpecification spec, final Callback<Optional<List<UserBook>>> callback) {
    final List<UserBookEntity> entities =
        toListUserBookEntityMapper.transform(Optional.fromNullable(userBooks)).orNull();
    storage.putAll(entities, spec, new Callback<Optional<List<UserBookEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserBookEntity>> optional) {
        final Optional<List<UserBook>> response = toUserBookListMapper.transform(optional);
        notifySuccessCallback(callback, response);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });

  }

  private void putAllUserBooksFromNetwork(final List<UserBook> userBooks,
      final UserBookNetworkSpecification specification,
      final Callback<Optional<List<UserBook>>> callback) {
    final List<UserBookEntity> entities =
        toListUserBookEntityMapper.transform(Optional.fromNullable(userBooks)).orNull();
    networkProvider.getRealNetwork()
        .putAll(entities, specification, new Callback<Optional<List<UserBookEntity>>>() {
          @Override public void onSuccess(final Optional<List<UserBookEntity>> optional) {
            final Optional<List<UserBook>> response = toUserBookListMapper.transform(optional);
            notifySuccessCallback(callback, response);
          }

          @Override public void onError(final Throwable e) {
            notifyErrorCallback(callback, e);
          }
        });
  }

  @Override public void remove(UserBook model, RepositorySpecification specification,
      final Callback<Optional<UserBook>> callback) {
    final Optional<UserBookEntity> userBookEntityOp =
        toUserBookEntityMapper.transform(Optional.fromNullable(model));

    if (userBookEntityOp.isPresent()) {
      final UserBookEntity userBookEntity = userBookEntityOp.get();
      networkProvider.get()
          .remove(userBookEntity, RepositorySpecification.NONE,
              new Callback<Optional<UserBookEntity>>() {
                @Override
                public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
                  final RemoveUserBookStorageSpec spec =
                      new RemoveUserBookStorageSpec(userBookEntity.getBookId(),
                          userBookEntity.getUserId());

                  storage.remove(userBookEntity, spec, new Callback<Optional<UserBookEntity>>() {
                    @Override
                    public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
                      notifySuccessCallback(callback,
                          toUserBookMapper.transform(userBookEntityOptional));
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
      notifyErrorCallback(callback, new UserBookNotFoundException());
    }
  }

  @Override public void removeAll(List<UserBook> userBooks, RepositorySpecification specification,
      final Callback<Optional<List<UserBook>>> callback) {
    throw new UnsupportedOperationException("removeAll() not supported");
  }

  //region Private methods

  private void getConcreteUserEntityId(UserStorageSpecification spec,
      final Callback<String> callback) {
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

  /**
   * Get the user entity directly from the storage.
   * @param callback with the user id
   */
  private void getFirstFoundUseEntityId(final Callback<String> callback) {
    final UserStorageSpecification spec = UserStorageSpecification.target(
        UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS);
    userStorage.get(spec, new Callback<Optional<UserEntity2>>() {
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

  private void getUserBookByBookIdFromStorage(final String bookId,
      final Callback<UserBookEntity> callback) {
    getFirstFoundUseEntityId(new Callback<String>() {
      @Override public void onSuccess(final String userId) {
        final GetUserBookStorageSpec spec = new GetUserBookStorageSpec(bookId, userId);

        storage.get(spec, new Callback<Optional<UserBookEntity>>() {
          @Override public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
            final UserBookEntity userBookEntityToSend =
                userBookEntityOptional.isPresent() ? userBookEntityOptional.get()
                                                   : new UserBookEntity.Builder().setBookId(bookId)
                    .setUserId(userId)
                    .build();

            notifySuccessCallback(callback, userBookEntityToSend);
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
