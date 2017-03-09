package com.worldreader.core.datasource.storage.datasource.user;

import com.google.common.base.Optional;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification.UserTarget;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UsersTable;
import com.worldreader.core.datasource.storage.model.User2Db;
import com.worldreader.core.error.user.PutUserFailException;

import java.util.*;

public class StorIOUserDbDataSourceImpl
    implements Repository.Storage<UserEntity2, RepositorySpecification> {

  private final StorIOSQLite storIOSQLite;

  private final Mapper<Optional<UserEntity2>, Optional<User2Db>> toUserDbMapper;
  private final Mapper<Optional<User2Db>, Optional<UserEntity2>> toUserEntityMapper;

  public StorIOUserDbDataSourceImpl(final StorIOSQLite storIOSQLite,
      final Mapper<Optional<UserEntity2>, Optional<User2Db>> toUserDbMapper,
      final Mapper<Optional<User2Db>, Optional<UserEntity2>> toUserEntityMapper) {
    this.storIOSQLite = storIOSQLite;
    this.toUserDbMapper = toUserDbMapper;
    this.toUserEntityMapper = toUserEntityMapper;
  }

  @Override public void get(final RepositorySpecification specification,
      final Callback<Optional<UserEntity2>> callback) {
    if (specification instanceof UserStorageSpecification) {
      final UserStorageSpecification storageSpecification =
          (UserStorageSpecification) specification;
      final Query query = toGetStorIOQuery(storageSpecification.getTarget());
      //final Optional<UserEntity2> optional =
      //    getAsObservable(query).toBlocking().firstOrDefault(Optional.<UserEntity2>absent());

      final User2Db user2Db =
          storIOSQLite.get().object(User2Db.class).withQuery(query).prepare().executeAsBlocking();
      final Optional<UserEntity2> optional =
          toUserEntityMapper.transform(Optional.fromNullable(user2Db));

      notifySuccessCallback(callback, optional);
    } else {
      throw new IllegalArgumentException("Specification not registered!");
    }
  }

  private Query toGetStorIOQuery(final UserTarget target) {
    switch (target) {
      case LOGGED_IN:
        return UsersTable.QUERY_SELECT_LOGGED_USER;
      case ANONYMOUS:
        return UsersTable.QUERY_SELECT_ANONYMOUS_USER;
      case FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS:
        return UsersTable.QUERY_SELECT_ALL_USERS;
    }

    throw new IllegalArgumentException("target is not in the domain range of the enum!");
  }

  //private Observable<Optional<UserEntity2>> getAsObservable(final Query query) {
  //  return storIOSQLite.get()
  //      .listOfObjects(User2Db.class)
  //      .withQuery(query)
  //      .prepare()
  //      .asRxObservable()
  //      .subscribeOn(Schedulers.immediate())
  //      .observeOn(Schedulers.immediate())
  //      .take(1)
  //      .filter(new Func1<List<User2Db>, Boolean>() {
  //        @Override public Boolean call(final List<User2Db> user2Dbs) {
  //          return user2Dbs != null && !user2Dbs.isEmpty();
  //        }
  //      })
  //      .map(new Func1<List<User2Db>, User2Db>() {
  //        @Override public User2Db call(final List<User2Db> user2Dbs) {
  //          return user2Dbs.get(0);
  //        }
  //      })
  //      .map(new Func1<User2Db, Optional<User2Db>>() {
  //        @Override public Optional<User2Db> call(final User2Db user2Db) {
  //          return Optional.of(user2Db);
  //        }
  //      })
  //      .compose(new Observable.Transformer<Optional<User2Db>, Optional<UserEntity2>>() {
  //        @Override public Observable<Optional<UserEntity2>> call(
  //            final Observable<Optional<User2Db>> optionalObservable) {
  //          return optionalObservable.map(new Func1<Optional<User2Db>, Optional<UserEntity2>>() {
  //            @Override public Optional<UserEntity2> call(final Optional<User2Db> optional) {
  //              if (!optional.isPresent()) {
  //                return Optional.absent();
  //              } else {
  //                return toUserEntityMapper.transform(optional);
  //              }
  //            }
  //          });
  //        }
  //      })
  //      .onErrorReturn(new Func1<Throwable, Optional<UserEntity2>>() {
  //        @Override public Optional<UserEntity2> call(final Throwable throwable) {
  //          return Optional.absent();
  //        }
  //      });
  //}

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserEntity2>>> callback) {
    throw new UnsupportedOperationException("Not implemented in this data source!");
  }

  @Override
  public void put(final UserEntity2 userEntity2, final RepositorySpecification specification,
      final Callback<Optional<UserEntity2>> callback) {
    if (specification instanceof UserStorageSpecification) {
      final User2Db user2Db = toUserDbMapper.transform(Optional.fromNullable(userEntity2)).orNull();
      final UserStorageSpecification storageSpecification =
          (UserStorageSpecification) specification;
      //final PutResult result = putAsObservable(user2Db).toBlocking().single();
      final PutResult result = storIOSQLite.put().object(user2Db).prepare().executeAsBlocking();

      if (result.wasInserted() || result.wasUpdated()) {
        final Query query = toGetStorIOQuery(storageSpecification.getTarget());
        final User2Db user2dbUpdated =
            storIOSQLite.get().object(User2Db.class).withQuery(query).prepare().executeAsBlocking();
        final Optional<UserEntity2> optional =
            toUserEntityMapper.transform(Optional.fromNullable(user2dbUpdated));

        //final Optional<UserEntity2> optional =
        //    getAsObservable(query).toBlocking().firstOrDefault(Optional.<UserEntity2>absent());
        notifySuccessCallback(callback, optional);
      } else {
        notifyErrorCallback(callback, new PutUserFailException());
      }
    } else {
      throw new IllegalArgumentException("This specification not registered!");
    }
  }

  //private Observable<PutResult> putAsObservable(final User2Db user2Db) {
  //  return storIOSQLite.put()
  //      .object(user2Db)
  //      .prepare()
  //      .asRxObservable()
  //      .subscribeOn(Schedulers.immediate())
  //      .observeOn(Schedulers.immediate())
  //      .take(1);
  //}

  @Override public void putAll(final List<UserEntity2> userEntity2s,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserEntity2>>> callback) {
    throw new UnsupportedOperationException("Not implemented in this data source!");
  }

  @Override
  public void remove(final UserEntity2 userEntity2, final RepositorySpecification specification,
      final Callback<Optional<UserEntity2>> callback) {
    if (specification instanceof UserStorageSpecification) {
      // First get the user from database
      final UserStorageSpecification storageSpecification =
          (UserStorageSpecification) specification;
      final Query query = toGetStorIOQuery(storageSpecification.getTarget());
      //final Optional<UserEntity2> optional =
      //    getAsObservable(query).toBlocking().firstOrDefault(Optional.<UserEntity2>absent());
      final User2Db user2Db =
          storIOSQLite.get().object(User2Db.class).withQuery(query).prepare().executeAsBlocking();
      final Optional<UserEntity2> optional =
          toUserEntityMapper.transform(Optional.fromNullable(user2Db));

      // Second delete the user from the database
      final DeleteQuery deleteQuery = toDeleteStorIOQuery(storageSpecification.getTarget());
      final DeleteResult deleteResult =
          storIOSQLite.delete().byQuery(deleteQuery).prepare().executeAsBlocking();

      notifySuccessCallback(callback, optional);
    } else {
      throw new IllegalArgumentException("This specification is not registered!");
    }
  }

  private DeleteQuery toDeleteStorIOQuery(final UserTarget target) {
    switch (target) {
      case LOGGED_IN:
        return UsersTable.DELETE_LOGGED_IN_USER;
      case ANONYMOUS:
        return UsersTable.QUERY_DELETE_ANONYMOUS_USER;
      case FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS:
        return UsersTable.QUERY_DELETE_ALL_USERS;
    }

    throw new IllegalArgumentException("Target option is not properly handled!");
  }

  //private Observable<DeleteResult> removeAsObservable(DeleteQuery deleteQuery) {
  //  return storIOSQLite.delete()
  //      .byQuery(deleteQuery)
  //      .prepare()
  //      .asRxObservable()
  //      .subscribeOn(Schedulers.immediate())
  //      .observeOn(Schedulers.immediate())
  //      .take(1);
  //}

  @Override public void removeAll(final List<UserEntity2> userEntity2s,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserEntity2>>> callback) {
    throw new UnsupportedOperationException("Not implemented in this data source!");
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
