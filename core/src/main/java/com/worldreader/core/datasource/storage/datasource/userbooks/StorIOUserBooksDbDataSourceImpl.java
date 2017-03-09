package com.worldreader.core.datasource.storage.datasource.userbooks;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResults;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.spec.userbooks.UserBookStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;
import com.worldreader.core.datasource.storage.model.UserBookDb;
import com.worldreader.core.error.userbook.DeleteUserBookFailException;
import com.worldreader.core.error.userbook.PutAllUserBookStorageFailException;
import com.worldreader.core.error.userbook.UpdateUserBookFailException;

import java.util.*;

public class StorIOUserBooksDbDataSourceImpl
    implements Repository.Storage<UserBookEntity, UserBookStorageSpecification> {

  private final StorIOSQLite storIOSQLite;

  private final Mapper<Optional<UserBookEntity>, Optional<UserBookDb>> toUserBookDbMapper;
  private final Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> toUserBookEntityMapper;
  private final Mapper<Optional<List<UserBookDb>>, Optional<List<UserBookEntity>>>
      toUserBookEntityListMapper;
  private final Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBookDb>>>
      toUserBookDbListMapper;

  public StorIOUserBooksDbDataSourceImpl(final StorIOSQLite storIOSQLite,
      final Mapper<Optional<UserBookEntity>, Optional<UserBookDb>> toUserBookDbMapper,
      final Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> toUserBookEntityMapper,
      final Mapper<Optional<List<UserBookDb>>, Optional<List<UserBookEntity>>> toUserBookEntityListMapper,
      final Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBookDb>>> toUserBookDbListMapper) {
    this.storIOSQLite = storIOSQLite;
    this.toUserBookDbMapper = toUserBookDbMapper;
    this.toUserBookEntityMapper = toUserBookEntityMapper;
    this.toUserBookEntityListMapper = toUserBookEntityListMapper;
    this.toUserBookDbListMapper = toUserBookDbListMapper;
  }

  @Override public void get(final UserBookStorageSpecification specification,
      final Callback<Optional<UserBookEntity>> callback) {
    final UserBookDb responseQuery = storIOSQLite.get()
        .object(UserBookDb.class)
        .withQuery(Query.builder()
            .table(UserBooksTable.TABLE)
            .where(UserBooksTable.COLUMN_USER_ID
                + " LIKE ? AND "
                + UserBooksTable.COLUMN_BOOK_ID
                + " LIKE ?")
            .whereArgs(specification.getUserId(), specification.getBookId())
            .build())
        .prepare()
        .executeAsBlocking();

    final Optional<UserBookEntity> responseOp =
        toUserBookEntityMapper.transform(Optional.fromNullable(responseQuery));

    notifySuccessCallback(callback, responseOp);
  }

  @Override public void getAll(final UserBookStorageSpecification specification,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    final Query query = specification.toQuery();

    final List<UserBookDb> userBookDbs = storIOSQLite.get()
        .listOfObjects(UserBookDb.class)
        .withQuery(query)
        .prepare()
        .executeAsBlocking();

    final Optional<List<UserBookEntity>> response =
        toUserBookEntityListMapper.transform(Optional.of(userBookDbs));
    notifySuccessCallback(callback, response);
  }

  @Override public void put(final UserBookEntity userBookEntity,
      final UserBookStorageSpecification specification,
      final Callback<Optional<UserBookEntity>> callback) {
    final List<UserBookEntity> userBookEntities = Collections.singletonList(userBookEntity);

    putAll(userBookEntities, specification, new Callback<Optional<List<UserBookEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserBookEntity>> response) {
        if (response.isPresent()) {
          final List<UserBookEntity> collection = response.get();
          if (collection.size() > 0) {
            notifySuccessCallback(callback, Optional.fromNullable(collection.get(0)));
          } else {
            notifyErrorCallback(callback, new UpdateUserBookFailException());
          }
        } else {
          notifyErrorCallback(callback, new UpdateUserBookFailException());
        }
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, new UpdateUserBookFailException());
      }
    });
  }

  @Override public void putAll(final List<UserBookEntity> userBookEntities,
      final UserBookStorageSpecification spec,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    final Optional<List<UserBookDb>> userBooksDbOptional =
        toUserBookDbListMapper.transform(Optional.fromNullable(userBookEntities));
    if (userBooksDbOptional.isPresent()) {
      final List<UserBookDb> userBookDbs = userBooksDbOptional.get();
      final PutResults<UserBookDb> transactionResult = storIOSQLite.put()
          .objects(userBookDbs)
          .useTransaction(true)
          .prepare()
          .executeAsBlocking();
      final int resultCount =
          transactionResult.numberOfInserts() + transactionResult.numberOfUpdates();
      if (resultCount == userBookEntities.size()) {
        final List<UserBookDb> results = Lists.newArrayList(transactionResult.results().keySet());
        final Optional<List<UserBookEntity>> toReturn =
            toUserBookEntityListMapper.transform(Optional.of(results));
        notifySuccessCallback(callback, toReturn);
      } else {
        notifyErrorCallback(callback, new PutAllUserBookStorageFailException());
      }
    } else {
      notifyErrorCallback(callback, new PutAllUserBookStorageFailException());
    }
  }

  @Override public void remove(final UserBookEntity userBookEntity,
      final UserBookStorageSpecification specification,
      final Callback<Optional<UserBookEntity>> callback) {
    final List<UserBookEntity> userBookEntities = Collections.singletonList(userBookEntity);

    removeAll(userBookEntities, specification, new Callback<Optional<List<UserBookEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserBookEntity>> listOptional) {
        if (listOptional.isPresent()) {
          final List<UserBookEntity> collection = listOptional.get();
          if (collection.size() > 0) {
            notifySuccessCallback(callback, Optional.fromNullable(collection.get(0)));
          } else {
            notifyErrorCallback(callback, new DeleteUserBookFailException());
          }
        } else {
          notifyErrorCallback(callback, new DeleteUserBookFailException());
        }
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void removeAll(final List<UserBookEntity> userBookEntities,
      final UserBookStorageSpecification specification,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    final List<UserBookEntity> casted = Lists.newArrayList(userBookEntities);
    final Optional<List<UserBookDb>> userBooksDbOp =
        toUserBookDbListMapper.transform(Optional.of(casted));

    if (userBooksDbOp.isPresent()) {
      final List<UserBookDb> userBooksDb = userBooksDbOp.get();

      final DeleteResults<UserBookDb> deleteResult = storIOSQLite.delete()
          .objects(userBooksDb)
          .useTransaction(true)
          .prepare()
          .executeAsBlocking();

      final Map<UserBookDb, DeleteResult> deleteOperationResult = deleteResult.results();
      boolean allDeleted = true;
      for (final DeleteResult resultOperation : deleteOperationResult.values()) {
        if (resultOperation.numberOfRowsDeleted() == 0) {
          allDeleted = false;
        }
      }

      if (allDeleted) {
        notifySuccessCallback(callback, Optional.of(userBookEntities));
      } else {
        notifyErrorCallback(callback, new DeleteUserBookFailException());
      }

    } else {
      notifyErrorCallback(callback, new DeleteUserBookFailException());
    }
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
