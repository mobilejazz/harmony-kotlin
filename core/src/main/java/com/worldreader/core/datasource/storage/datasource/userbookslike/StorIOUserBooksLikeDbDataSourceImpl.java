package com.worldreader.core.datasource.storage.datasource.userbookslike;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResults;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.spec.userbookslike.UserBookLikeStorageSpec;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBookLikesTable;
import com.worldreader.core.datasource.storage.model.UserBookLikeDb;
import com.worldreader.core.datasource.storage.model.UserBookLikeDbStorIOSQLitePutResolver;
import com.worldreader.core.error.userbooklike.DeleteUserBookLikeFailException;
import com.worldreader.core.error.userbooklike.PutAllUserBookLikeStorageFailException;
import com.worldreader.core.error.userbooklike.UpdateUserBookLikeFailException;

import java.util.*;

public class StorIOUserBooksLikeDbDataSourceImpl implements Repository.Storage<UserBookLikeEntity, UserBookLikeStorageSpec> {

  private final StorIOSQLite storIOSQLite;

  private final Mapper<Optional<UserBookLikeEntity>, Optional<UserBookLikeDb>> toUserBookLikeDbMapper;
  private final Mapper<Optional<UserBookLikeDb>, Optional<UserBookLikeEntity>> toUserBookLikeEntityMapper;
  private final Mapper<Optional<List<UserBookLikeDb>>, Optional<List<UserBookLikeEntity>>> toUserBookLikeEntityListMapper;
  private final Mapper<Optional<List<UserBookLikeEntity>>, Optional<List<UserBookLikeDb>>> toUserBookLikeDbListMapper;

  public StorIOUserBooksLikeDbDataSourceImpl(final StorIOSQLite storIOSQLite,
      final Mapper<Optional<UserBookLikeEntity>, Optional<UserBookLikeDb>> toUserBookLikeDbMapper,
      final Mapper<Optional<UserBookLikeDb>, Optional<UserBookLikeEntity>> toUserBookLikeEntityMapper,
      final Mapper<Optional<List<UserBookLikeDb>>, Optional<List<UserBookLikeEntity>>> toUserBookLikeEntityListMapper,
      final Mapper<Optional<List<UserBookLikeEntity>>, Optional<List<UserBookLikeDb>>> toUserBookLikeDbListMapper) {
    this.storIOSQLite = storIOSQLite;
    this.toUserBookLikeDbMapper = toUserBookLikeDbMapper;
    this.toUserBookLikeEntityMapper = toUserBookLikeEntityMapper;
    this.toUserBookLikeEntityListMapper = toUserBookLikeEntityListMapper;
    this.toUserBookLikeDbListMapper = toUserBookLikeDbListMapper;
  }

  @Override public void get(final UserBookLikeStorageSpec specification, final Callback<Optional<UserBookLikeEntity>> callback) {
    final UserBookLikeDb responseQuery = storIOSQLite.get()
        .object(UserBookLikeDb.class)
        .withQuery(Query.builder()
            .table(UserBookLikesTable.TABLE)
            .where(UserBookLikesTable.COLUMN_USER_ID + " LIKE ? AND " + UserBookLikesTable.COLUMN_BOOK_ID + " LIKE ?")
            .whereArgs(specification.getUserId(), specification.getBookId())
            .build())
        .prepare()
        .executeAsBlocking();
    final Optional<UserBookLikeEntity> responseOp = toUserBookLikeEntityMapper.transform(Optional.fromNullable(responseQuery));
    notifySuccessCallback(callback, responseOp);
  }

  @Override public void getAll(final UserBookLikeStorageSpec specification, final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    final Query query = specification.toQuery();
    final List<UserBookLikeDb> userBookDbs = storIOSQLite.get().listOfObjects(UserBookLikeDb.class).withQuery(query).prepare().executeAsBlocking();
    final Optional<List<UserBookLikeEntity>> response = toUserBookLikeEntityListMapper.transform(Optional.of(userBookDbs));
    notifySuccessCallback(callback, response);
  }

  @Override public void put(final UserBookLikeEntity entity, final UserBookLikeStorageSpec specification,
      final Callback<Optional<UserBookLikeEntity>> callback) {
    final List<UserBookLikeEntity> userBookEntities = Collections.singletonList(entity);
    putAll(userBookEntities, specification, new Callback<Optional<List<UserBookLikeEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserBookLikeEntity>> response) {
        if (response.isPresent()) {
          final List<UserBookLikeEntity> collection = response.get();
          if (collection.size() > 0) {
            notifySuccessCallback(callback, Optional.fromNullable(collection.get(0)));
          } else {
            notifyErrorCallback(callback, new UpdateUserBookLikeFailException());
          }
        } else {
          notifyErrorCallback(callback, new UpdateUserBookLikeFailException());
        }
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, new UpdateUserBookLikeFailException());
      }
    });
  }

  @Override public void putAll(final List<UserBookLikeEntity> userBookLikeEntities, final UserBookLikeStorageSpec specification,
      final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    final Optional<List<UserBookLikeDb>> userBooksDbOptional = toUserBookLikeDbListMapper.transform(Optional.fromNullable(userBookLikeEntities));
    if (userBooksDbOptional.isPresent()) {
      final List<UserBookLikeDb> userBookDbs = userBooksDbOptional.get();
      final PutResults<UserBookLikeDb> transactionResult = storIOSQLite.put()
          .objects(userBookDbs)
          .withPutResolver(new ExtendedUserBookLikeDbStorIOSQLitePutResolver(specification.getUserId()))
          .useTransaction(true)
          .prepare()
          .executeAsBlocking();
      final int resultCount = transactionResult.numberOfInserts() + transactionResult.numberOfUpdates();
      if (resultCount == userBookLikeEntities.size()) {
        final List<UserBookLikeDb> results = Lists.newArrayList(transactionResult.results().keySet());
        final Optional<List<UserBookLikeEntity>> toReturn = toUserBookLikeEntityListMapper.transform(Optional.of(results));
        notifySuccessCallback(callback, toReturn);
      } else {
        notifyErrorCallback(callback, new PutAllUserBookLikeStorageFailException());
      }
    } else {
      notifyErrorCallback(callback, new PutAllUserBookLikeStorageFailException());
    }
  }

  @Override public void remove(final UserBookLikeEntity entity, final UserBookLikeStorageSpec specification,
      final Callback<Optional<UserBookLikeEntity>> callback) {
    final List<UserBookLikeEntity> userBookEntities = Collections.singletonList(entity);
    removeAll(userBookEntities, specification, new Callback<Optional<List<UserBookLikeEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserBookLikeEntity>> listOptional) {
        if (listOptional.isPresent()) {
          final List<UserBookLikeEntity> collection = listOptional.get();
          if (collection.size() > 0) {
            notifySuccessCallback(callback, Optional.fromNullable(collection.get(0)));
          } else {
            notifyErrorCallback(callback, new DeleteUserBookLikeFailException());
          }
        } else {
          notifyErrorCallback(callback, new DeleteUserBookLikeFailException());
        }
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void removeAll(final List<UserBookLikeEntity> userBookLikeEntities, final UserBookLikeStorageSpec specification,
      final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    final List<UserBookLikeEntity> casted = Lists.newArrayList(userBookLikeEntities);
    final Optional<List<UserBookLikeDb>> userBooksDbOp = toUserBookLikeDbListMapper.transform(Optional.of(casted));

    if (userBooksDbOp.isPresent()) {
      final List<UserBookLikeDb> userBooksDb = userBooksDbOp.get();

      final DeleteResults<UserBookLikeDb> deleteResult =
          storIOSQLite.delete().objects(userBooksDb).useTransaction(true).prepare().executeAsBlocking();

      final Map<UserBookLikeDb, DeleteResult> deleteOperationResult = deleteResult.results();
      boolean allDeleted = true;
      for (final DeleteResult resultOperation : deleteOperationResult.values()) {
        if (resultOperation.numberOfRowsDeleted() == 0) {
          allDeleted = false;
        }
      }

      if (allDeleted) {
        notifySuccessCallback(callback, Optional.of(userBookLikeEntities));
      } else {
        notifyErrorCallback(callback, new DeleteUserBookLikeFailException());
      }

    } else {
      notifyErrorCallback(callback, new DeleteUserBookLikeFailException());
    }
  }

  private <T> void notifyErrorCallback(Callback<T> callback, Throwable error) {
    if (callback != null) {
      callback.onError(error);
    }
  }

  private <T> void notifySuccessCallback(Callback<T> callback, T result) {
    if (callback != null) {
      callback.onSuccess(result);
    }
  }

  private static class ExtendedUserBookLikeDbStorIOSQLitePutResolver extends UserBookLikeDbStorIOSQLitePutResolver {

    private final String userId;

    public ExtendedUserBookLikeDbStorIOSQLitePutResolver(final String userId) {
      this.userId = userId;
    }

    @NonNull @Override public InsertQuery mapToInsertQuery(@NonNull final UserBookLikeDb object) {
      object.userId = userId;
      return super.mapToInsertQuery(object);
    }

    @NonNull @Override public UpdateQuery mapToUpdateQuery(@NonNull final UserBookLikeDb object) {
      object.userId = userId;
      return super.mapToUpdateQuery(object);
    }

    @NonNull @Override public ContentValues mapToContentValues(@NonNull final UserBookLikeDb object) {
      object.userId = userId;
      return super.mapToContentValues(object);
    }
  }
}
