package com.worldreader.core.datasource.storage.datasource.score;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.common.base.Optional;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserScoreTable;
import com.worldreader.core.datasource.storage.model.UserScoreDb;
import com.worldreader.core.error.score.UserScoreStoragePutOperationFailException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

import static com.pushtorefresh.storio.internal.InternalQueries.nullableArrayOfStrings;
import static com.pushtorefresh.storio.internal.InternalQueries.nullableString;

public class StorIOUserScoreDbDataSource implements UserScoreStorageDataSource {

  private final Mapper<Optional<UserScoreDb>, Optional<UserScoreEntity>> toUserScoreEntity;
  private final Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb;
  private final Mapper<Optional<List<UserScoreDb>>, Optional<List<UserScoreEntity>>> toListUserScoreEntity;
  private final Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScoreDb>>> toUserScoreDbCollection;

  private final UserScorePutResolver userScorePutResolver = new UserScorePutResolver();

  private final StorIOSQLite storio;

  @Inject public StorIOUserScoreDbDataSource(final Mapper<Optional<UserScoreDb>, Optional<UserScoreEntity>> toUserScoreEntity,
      final Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb,
      final Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScoreDb>>> toUserScoreDbCollection,
      final Mapper<Optional<List<UserScoreDb>>, Optional<List<UserScoreEntity>>> toListUserScoreEntity, final StorIOSQLite storio) {
    this.toUserScoreEntity = toUserScoreEntity;
    this.toUserScoreDb = toUserScoreDb;
    this.toUserScoreDbCollection = toUserScoreDbCollection;
    this.toListUserScoreEntity = toListUserScoreEntity;
    this.storio = storio;
  }

  @Override public void get(final UserScoreStorageSpecification specification, final Callback<Optional<UserScoreEntity>> callback) {
    final UserScoreDb userScoreDb = storio.get().object(UserScoreDb.class).withQuery(specification.toQuery()).prepare().executeAsBlocking();

    final Optional<UserScoreEntity> userScoreEntityOp = toUserScoreEntity.transform(Optional.fromNullable(userScoreDb));

    if (callback != null) {
      callback.onSuccess(userScoreEntityOp);
    }
  }

  @Override public void getAll(final UserScoreStorageSpecification specification, final Callback<Optional<List<UserScoreEntity>>> callback) {
    throw new UnsupportedOperationException("getAll() not supported");
  }

  @Override public void put(final UserScoreEntity userScoreEntity, final UserScoreStorageSpecification specification,
      final Callback<Optional<UserScoreEntity>> callback) {
    final List<UserScoreEntity> userScoreEntities = Collections.singletonList(userScoreEntity);
    putAll(userScoreEntities, specification, new Callback<Optional<List<UserScoreEntity>>>() {
      @Override public void onSuccess(final Optional<List<UserScoreEntity>> listOptional) {
        if (listOptional.isPresent()) {
          final List<UserScoreEntity> response = listOptional.get();

          if (response.size() > 0) {
            notifySuccessCallback(callback, Optional.fromNullable(response.get(0)));
          } else {
            notifyErrorCallback(callback, new UserScoreStoragePutOperationFailException());
          }
        } else {
          notifyErrorCallback(callback, new UserScoreStoragePutOperationFailException());
        }
      }

      @Override public void onError(final Throwable e) {
        notifyErrorCallback(callback, e);
      }
    });
  }

  @Override public void putAll(final List<UserScoreEntity> userScoreEntities, final UserScoreStorageSpecification specification,
      final Callback<Optional<List<UserScoreEntity>>> callback) {
    final Optional<List<UserScoreDb>> userScoresDb = toUserScoreDbCollection.transform(Optional.fromNullable(userScoreEntities));

    if (userScoresDb.isPresent()) {
      final PutResults<UserScoreDb> userScoreDbPutResults =
          storio.put().objects(userScoresDb.get()).withPutResolver(userScorePutResolver).useTransaction(true).prepare().executeAsBlocking();

      if (userScoreDbPutResults.numberOfInserts() > 0 || userScoreDbPutResults.numberOfUpdates() > 0) {
        List<UserScoreEntity> response = new ArrayList<>();

        for (final UserScoreDb userScoreDb : userScoreDbPutResults.results().keySet()) {
          final Optional<UserScoreEntity> userScoreEntityOp = toUserScoreEntity.transform(Optional.fromNullable(userScoreDb));
          if (userScoreEntityOp.isPresent()) {
            response.add(userScoreEntityOp.get());
          }
        }

        notifySuccessCallback(callback, Optional.of(response));
      } else {
        notifyErrorCallback(callback, new UserScoreStoragePutOperationFailException());
      }
    } else {
      notifyErrorCallback(callback, new UserScoreStoragePutOperationFailException());
    }
  }

  @Override public void remove(final UserScoreEntity userScoreEntity, final UserScoreStorageSpecification specification,
      final Callback<Optional<UserScoreEntity>> callback) {
    throw new UnsupportedOperationException("remove() not supported");
  }

  @Override public void removeAll(final List<UserScoreEntity> userScoreEntities, final UserScoreStorageSpecification specification,
      final Callback<Optional<List<UserScoreEntity>>> callback) {

    throw new UnsupportedOperationException("removeAll() not supported");
  }

  @Override public void removeAll(final UserScoreStorageSpecification specification, final Callback<Void> callback) {
    final DeleteResult deleteResult = storio.delete().byQuery(specification.toDeleteQuery()).prepare().executeAsBlocking();

    if (callback != null) {
      callback.onSuccess(null);
    }
  }

  @Override public void getTotalUserScore(final String userId, final Callback<Integer> callback) {
    final Cursor cursor = storio.get()
        .cursor()
        .withQuery(RawQuery.builder()
            .query("SELECT sum("
                + UserScoreTable.COLUMN_SCORE
                + ") FROM "
                + UserScoreTable.TABLE
                + " WHERE "
                + UserScoreTable.COLUMN_USER_ID
                + " LIKE ?")
            .args(userId)
            .build())
        .prepare()
        .executeAsBlocking();

    if (cursor.moveToFirst()) {
      final int total = cursor.getInt(0);
      notifySuccessCallback(callback, total);
    } else {
      notifySuccessCallback(callback, 0);
    }

    cursor.close();
  }

  @Override public void getTotalUserScoreUnSynced(final String userId, final Callback<Integer> callback) {
    final Cursor cursor = storio.get()
        .cursor()
        .withQuery(RawQuery.builder()
            .query("SELECT sum("
                + UserScoreTable.COLUMN_SCORE
                + ") FROM "
                + UserScoreTable.TABLE
                + " WHERE "
                + UserScoreTable.COLUMN_USER_ID
                + " LIKE ? AND "
                + UserScoreTable.COLUMN_SYNCHRONIZED
                + " = ?")
            .args(userId, 0/*unsynced*/)
            .build())
        .prepare()
        .executeAsBlocking();

    if (cursor.moveToFirst()) {
      final int total = cursor.getInt(0);
      notifySuccessCallback(callback, total);
    } else {
      notifySuccessCallback(callback, 0);
    }

    cursor.close();
  }

  @Override public void getBookPagesUserScore(final String userId, final Callback<List<UserScoreEntity>> callback) {
    final List<UserScoreDb> userScores = storio.get()
        .listOfObjects(UserScoreDb.class)
        .withQuery(RawQuery.builder()
            .query("SELECT * FROM "
                + UserScoreTable.TABLE
                + " WHERE "
                + UserScoreTable.COLUMN_USER_ID
                + " LIKE ? AND "
                + UserScoreTable.COLUMN_SYNCHRONIZED
                + " = ? AND "
                + UserScoreTable.COLUMN_BOOK_ID
                + " IS NOT NULL")
            .args(userId, 0/*unsynced*/)
            .build())
        .prepare()
        .executeAsBlocking();

    final List<UserScoreEntity> entities = toListUserScoreEntity.transform(Optional.fromNullable(userScores)).get();

    notifySuccessCallback(callback, entities);
  }

  //region Private methods
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
  //endregion

  private class UserScorePutResolver extends DefaultPutResolver<UserScoreDb> {

    @NonNull @Override protected InsertQuery mapToInsertQuery(@NonNull final UserScoreDb object) {
      return InsertQuery.builder().table(UserScoreTable.TABLE).build();
    }

    @NonNull @Override protected UpdateQuery mapToUpdateQuery(@NonNull final UserScoreDb object) {
      return UpdateQuery.builder()
          .table(UserScoreTable.TABLE)
          .where(!TextUtils.isEmpty(object.getBookId()) ? UserScoreTable.COLUMN_BOOK_ID + " LIKE ? AND " + UserScoreTable.COLUMN_USER_ID + " LIKE ?"
                                                        : UserScoreTable.COLUMN_SCORE_ID + " LIKE ? AND " + UserScoreTable.COLUMN_USER_ID + " LIKE ?")
          .whereArgs(!TextUtils.isEmpty(object.getBookId()) ? object.getBookId() : object.getScoreId(), object.getUserId())
          .build();
    }

    @NonNull @Override protected ContentValues mapToContentValues(@NonNull final UserScoreDb toConvert) {
      // Not implemented as we are using mapToContentValues(T object, Cursor cursor) method
      throw new IllegalStateException("Not implemented!");
    }

    @NonNull @Override public PutResult performPut(@NonNull final StorIOSQLite storIOSQLite, @NonNull final UserScoreDb object) {
      final UpdateQuery updateQuery = mapToUpdateQuery(object);

      // for data consistency in concurrent environment, encapsulate Put Operation into transaction
      storIOSQLite.internal().beginTransaction();

      try {
        final Cursor cursor = storIOSQLite.internal()
            .query(Query.builder()
                .table(updateQuery.table())
                .where(nullableString(updateQuery.where()))
                .whereArgs((Object[]) nullableArrayOfStrings(updateQuery.whereArgs()))
                .build());

        final PutResult putResult;

        try {
          final ContentValues contentValues = mapToContentValues(object, cursor);

          if (cursor.getCount() == 0) {
            final InsertQuery insertQuery = mapToInsertQuery(object);
            final long insertedId = storIOSQLite.internal().insert(insertQuery, contentValues);
            putResult = PutResult.newInsertResult(insertedId, insertQuery.table());
          } else {
            final int numberOfRowsUpdated = storIOSQLite.internal().update(updateQuery, contentValues);
            putResult = PutResult.newUpdateResult(numberOfRowsUpdated, updateQuery.table());
          }
        } finally {
          cursor.close();
        }

        // everything okay
        storIOSQLite.internal().setTransactionSuccessful();

        return putResult;
      } finally {
        // in case of bad situations, db won't be affected
        storIOSQLite.internal().endTransaction();
      }
    }

    private ContentValues mapToContentValues(@NonNull final UserScoreDb toConvert, final Cursor rawUpdateCursor) {
      final ContentValues contentValues = new ContentValues();

      final int pagesRead;

      // We're going to update the UserScore by bookId
      if (!TextUtils.isEmpty(toConvert.getBookId())) {
        //contentValues.putNull(UserScoreTable.COLUMN_SCORE_ID);
        if (rawUpdateCursor.getCount() == 1) {
          rawUpdateCursor.moveToFirst();
          final int columnIndex = rawUpdateCursor.getColumnIndex(UserScoreTable.COLUMN_PAGES);
          final int rawPages = rawUpdateCursor.getInt(columnIndex);
          pagesRead = rawPages + toConvert.getPages();
        } else {
          pagesRead = toConvert.getPages();
        }
      } else {
        contentValues.put(UserScoreTable.COLUMN_SCORE_ID, toConvert.getScoreId());
        pagesRead = toConvert.getPages();
      }

      contentValues.put(UserScoreTable.COLUMN_USER_ID, toConvert.getUserId());
      contentValues.put(UserScoreTable.COLUMN_BOOK_ID, toConvert.getBookId());
      contentValues.put(UserScoreTable.COLUMN_SCORE, toConvert.getScore());
      contentValues.put(UserScoreTable.COLUMN_PAGES, pagesRead);
      contentValues.put(UserScoreTable.COLUMN_SYNCHRONIZED, toConvert.isSync());
      contentValues.put(UserScoreTable.COLUMN_CREATED_AT, toConvert.getCreatedAt());
      contentValues.put(UserScoreTable.COLUMN_UPDATED_AT, toConvert.getUpdatedAt());

      return contentValues;
    }
  }

}
