package com.worldreader.core.datasource.storage.datasource.milestones;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.spec.milestones.UserMilestoneStorageSpecification;
import com.worldreader.core.datasource.storage.mapper.milestones.ListUserMilestoneEntityDbToListUserMilestoneEntityMapper;
import com.worldreader.core.datasource.storage.mapper.milestones.ListUserMilestoneEntityToListUserMilestoneDbMapper;
import com.worldreader.core.datasource.storage.mapper.milestones.UserMilestoneDbToUserMilestoneEntityMapper;
import com.worldreader.core.datasource.storage.mapper.milestones.UserMilestoneEntityToUserMilestoneDbMapper;
import com.worldreader.core.datasource.storage.model.UserMilestoneDb;
import com.worldreader.core.error.milestones.DeleteUserMilestoneFailException;
import com.worldreader.core.error.milestones.PutUserMilestoneStorageFailException;

import java.util.*;

public class StorIOUserMilestonesDbDataSourceImpl
    implements Repository.Storage<UserMilestoneEntity, UserMilestoneStorageSpecification> {

  private final StorIOSQLite storio;

  private final Mapper<Optional<UserMilestoneEntity>, Optional<UserMilestoneDb>>
      toMilestoneDbMapper;
  private final Mapper<Optional<List<UserMilestoneEntity>>, Optional<List<UserMilestoneDb>>>
      toListMilestoneDbMapper;

  private final Mapper<Optional<UserMilestoneDb>, Optional<UserMilestoneEntity>>
      toMilestoneEntityMapper;
  private final Mapper<Optional<List<UserMilestoneDb>>, Optional<List<UserMilestoneEntity>>>
      toListUserMilestoneEntityMapper;

  public StorIOUserMilestonesDbDataSourceImpl(final StorIOSQLite storio,
      final UserMilestoneEntityToUserMilestoneDbMapper toMilestoneDbMapper,
      final ListUserMilestoneEntityToListUserMilestoneDbMapper toListMilestoneDbMapper,
      final UserMilestoneDbToUserMilestoneEntityMapper toMilestoneEntityMapper,
      final ListUserMilestoneEntityDbToListUserMilestoneEntityMapper toListUserMilestoneEntityMapper) {
    this.storio = storio;
    this.toMilestoneDbMapper = toMilestoneDbMapper;
    this.toListMilestoneDbMapper = toListMilestoneDbMapper;
    this.toMilestoneEntityMapper = toMilestoneEntityMapper;
    this.toListUserMilestoneEntityMapper = toListUserMilestoneEntityMapper;
  }

  @Override public void get(final UserMilestoneStorageSpecification specification,
      final Callback<Optional<UserMilestoneEntity>> callback) {
    final UserMilestoneDb response = storio.get()
        .object(UserMilestoneDb.class)
        .withQuery(specification.toQuery())
        .prepare()
        .executeAsBlocking();
    final Optional<UserMilestoneEntity> toReturn =
        toMilestoneEntityMapper.transform(Optional.fromNullable(response));
    notifySuccessCallback(callback, toReturn);
  }

  @Override public void getAll(final UserMilestoneStorageSpecification specification,
      final Callback<Optional<List<UserMilestoneEntity>>> callback) {
    final Query query = specification.toQuery();
    final List<UserMilestoneDb> responseGetAll = storio.get()
        .listOfObjects(UserMilestoneDb.class)
        .withQuery(query)
        .prepare()
        .executeAsBlocking();
    final Optional<List<UserMilestoneEntity>> response =
        toListUserMilestoneEntityMapper.transform(Optional.of(responseGetAll));
    notifySuccessCallback(callback, response);
  }

  @Override public void put(final UserMilestoneEntity userMilestoneEntity,
      final UserMilestoneStorageSpecification specification,
      final Callback<Optional<UserMilestoneEntity>> callback) {
    putAll(Collections.singletonList(userMilestoneEntity), specification,
        new Callback<Optional<List<UserMilestoneEntity>>>() {
          @Override public void onSuccess(final Optional<List<UserMilestoneEntity>> optional) {
            final List<UserMilestoneEntity> entities = optional.get();
            final UserMilestoneEntity userMilestoneEntity1 = entities.get(0);
            notifySuccessCallback(callback, Optional.of(userMilestoneEntity1));
          }

          @Override public void onError(final Throwable e) {
            notifyErrorCallback(callback, new PutUserMilestoneStorageFailException());
          }
        });
  }

  @Override public void putAll(final List<UserMilestoneEntity> userMilestoneEntities,
      final UserMilestoneStorageSpecification specification,
      final Callback<Optional<List<UserMilestoneEntity>>> callback) {
    final List<UserMilestoneDb> transformed =
        toListMilestoneDbMapper.transform(Optional.fromNullable(userMilestoneEntities))
            .or(Collections.<UserMilestoneDb>emptyList());
    final PutResults<UserMilestoneDb> putResult =
        storio.put().objects(transformed).useTransaction(true).prepare().executeAsBlocking();
    final int updatedSize = putResult.numberOfUpdates() + putResult.numberOfInserts();
    if (updatedSize == userMilestoneEntities.size()) {
      final List<UserMilestoneDb> milestoneDbs = Lists.newArrayList(putResult.results().keySet());
      final Optional<List<UserMilestoneEntity>> transformedFromDb =
          toListUserMilestoneEntityMapper.transform(Optional.of(milestoneDbs));
      notifySuccessCallback(callback, transformedFromDb);
    } else {
      notifyErrorCallback(callback, new PutUserMilestoneStorageFailException());
    }
  }

  @Override public void remove(final UserMilestoneEntity userMilestoneEntity,
      final UserMilestoneStorageSpecification specification,
      final Callback<Optional<UserMilestoneEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void removeAll(final List<UserMilestoneEntity> userMilestoneEntities,
      final UserMilestoneStorageSpecification specification,
      final Callback<Optional<List<UserMilestoneEntity>>> callback) {
    final DeleteQuery deleteQuery = specification.toDeleteQuery();
    final DeleteResult deleteResult =
        storio.delete().byQuery(deleteQuery).prepare().executeAsBlocking();
    final int rowsDeleted = deleteResult.numberOfRowsDeleted();
    if (rowsDeleted > 0) {
      notifySuccessCallback(callback, Optional.of(userMilestoneEntities)); // Fake optimization
    } else {
      notifyErrorCallback(callback, new DeleteUserMilestoneFailException());
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
