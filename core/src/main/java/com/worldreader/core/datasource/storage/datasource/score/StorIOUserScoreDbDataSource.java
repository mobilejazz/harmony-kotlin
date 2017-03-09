package com.worldreader.core.datasource.storage.datasource.score;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import com.google.common.base.Optional;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.spec.score.UserScoreStorageSpecification;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserScoreTable;
import com.worldreader.core.datasource.storage.model.UserScoreDb;
import com.worldreader.core.error.score.UserScoreStoragePutOperationFailException;

import javax.inject.Inject;
import java.util.*;

public class StorIOUserScoreDbDataSource implements UserScoreStorageDataSource {

  private final Mapper<Optional<UserScoreDb>, Optional<UserScoreEntity>> toUserScoreEntity;
  private final Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb;
  private final Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScoreDb>>>
      toUserScoreDbCollection;
  private final StorIOSQLite storio;

  @Inject public StorIOUserScoreDbDataSource(
      final Mapper<Optional<UserScoreDb>, Optional<UserScoreEntity>> toUserScoreEntity,
      final Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb,
      final Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScoreDb>>> toUserScoreDbCollection,
      final StorIOSQLite storio) {
    this.toUserScoreEntity = toUserScoreEntity;
    this.toUserScoreDb = toUserScoreDb;
    this.toUserScoreDbCollection = toUserScoreDbCollection;
    this.storio = storio;
  }

  @Override public void get(final UserScoreStorageSpecification specification,
      final Callback<Optional<UserScoreEntity>> callback) {
    final UserScoreDb userScoreDb = storio.get()
        .object(UserScoreDb.class)
        .withQuery(specification.toQuery())
        .prepare()
        .executeAsBlocking();

    final Optional<UserScoreEntity> userScoreEntityOp =
        toUserScoreEntity.transform(Optional.fromNullable(userScoreDb));

    if (callback != null) {
      callback.onSuccess(userScoreEntityOp);
    }
  }

  @Override public void getAll(final UserScoreStorageSpecification specification,
      final Callback<Optional<List<UserScoreEntity>>> callback) {
    throw new UnsupportedOperationException("getAll() not supported");
  }

  @Override public void put(final UserScoreEntity userScoreEntity,
      final UserScoreStorageSpecification specification,
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

  @Override public void putAll(final List<UserScoreEntity> userScoreEntities,
      final UserScoreStorageSpecification specification,
      final Callback<Optional<List<UserScoreEntity>>> callback) {
    final Optional<List<UserScoreDb>> userScoresDb =
        toUserScoreDbCollection.transform(Optional.fromNullable(userScoreEntities));

    if (userScoresDb.isPresent()) {
      final PutResults<UserScoreDb> userScoreDbPutResults = storio.put()
          .objects(userScoresDb.get())
          .withPutResolver(new DefaultPutResolver<UserScoreDb>() {
            @NonNull @Override
            protected InsertQuery mapToInsertQuery(@NonNull final UserScoreDb object) {
              return InsertQuery.builder().table("userscore").build();
            }

            @NonNull @Override
            protected UpdateQuery mapToUpdateQuery(@NonNull final UserScoreDb object) {
              return UpdateQuery.builder()
                  .table("userscore")
                  .where("scoreId = ? AND userId LIKE ?")
                  .whereArgs(object.getScoreId(), object.getUserId())
                  .build();
            }

            @NonNull @Override
            protected ContentValues mapToContentValues(@NonNull final UserScoreDb object) {
              ContentValues contentValues = new ContentValues(6);

              if (object.getScoreId() == 0) {
                contentValues.putNull(UserScoreTable.COLUMN_SCORE_ID);
              } else {
                contentValues.put(UserScoreTable.COLUMN_SCORE_ID, object.getScoreId());
              }

              contentValues.put("score", object.getScore());
              contentValues.put("synchronized", object.isSync());
              contentValues.put("createdAt", object.getCreatedAt());
              contentValues.put("userId", object.getUserId());
              contentValues.put("updatedAt", object.getUpdatedAt());

              return contentValues;
            }
          })
          .useTransaction(true)
          .prepare()
          .executeAsBlocking();

      if (userScoreDbPutResults.numberOfInserts() > 0
          || userScoreDbPutResults.numberOfUpdates() > 0) {
        List<UserScoreEntity> response = new ArrayList<>();

        for (final UserScoreDb userScoreDb : userScoreDbPutResults.results().keySet()) {
          final Optional<UserScoreEntity> userScoreEntityOp =
              toUserScoreEntity.transform(Optional.fromNullable(userScoreDb));
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

  @Override public void remove(final UserScoreEntity userScoreEntity,
      final UserScoreStorageSpecification specification,
      final Callback<Optional<UserScoreEntity>> callback) {
    throw new UnsupportedOperationException("remove() not supported");
  }

  @Override public void removeAll(final List<UserScoreEntity> userScoreEntities,
      final UserScoreStorageSpecification specification,
      final Callback<Optional<List<UserScoreEntity>>> callback) {

    throw new UnsupportedOperationException("removeAll() not supported");
  }

  @Override public void removeAll(final UserScoreStorageSpecification specification,
      final Callback<Void> callback) {
    final DeleteResult deleteResult =
        storio.delete().byQuery(specification.toDeleteQuery()).prepare().executeAsBlocking();

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

  @Override
  public void getTotalUserScoreUnSynched(final String userId, final Callback<Integer> callback) {
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

}
