package com.worldreader.core.datasource.network.datasource.milestones;

import android.content.Context;
import com.google.common.base.Optional;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.error.WorldreaderErrorAdapter2;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.datasource.network.general.retrofit.services.UserApiService2;
import com.worldreader.core.datasource.network.mapper.user.UserNetworkResponseToUserEntityMapper;
import com.worldreader.core.datasource.network.model.MilestonesNetworkBody;
import com.worldreader.core.datasource.network.model.UserNetworkResponse;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.*;
import java.util.*;

public class UserMilestonesNetworkDataSourceImpl implements UserMilestonesNetworkDataSource {

  private final UserApiService2 apiService;

  private final Mapper<Optional<UserNetworkResponse>, Optional<UserEntity2>> toUserEntityMapper;

  private final ErrorAdapter<Throwable> errorAdapter;

  @Inject public UserMilestonesNetworkDataSourceImpl(final Context context,
      final UserApiService2 apiService,
      final UserNetworkResponseToUserEntityMapper toUserEntityMapper, final Logger logger) {
    this.apiService = apiService;
    this.toUserEntityMapper = toUserEntityMapper;
    this.errorAdapter = new WorldreaderErrorAdapter2(context, new Retrofit2ErrorAdapter(), logger);
  }

  @Override public void get(final RepositorySpecification specification,
      final Callback<Optional<UserMilestoneEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserMilestoneEntity>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override
  public void put(final UserMilestoneEntity entity, final RepositorySpecification specification,
      final Callback<Optional<UserMilestoneEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void putAll(final List<UserMilestoneEntity> userMilestoneEntities,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserMilestoneEntity>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override
  public void remove(final UserMilestoneEntity entity, final RepositorySpecification specification,
      final Callback<Optional<UserMilestoneEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void removeAll(final List<UserMilestoneEntity> userMilestoneEntities,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserMilestoneEntity>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void updateMilestone(final UserMilestoneEntity entity,
      final Callback<Optional<UserEntity2>> callback) {
    updateMilestones(Collections.singletonList(entity), callback);
  }

  @Override public void updateMilestones(final List<UserMilestoneEntity> entities,
      final Callback<Optional<UserEntity2>> callback) {
    final MilestonesNetworkBody body = MilestonesNetworkBody.of(entities);
    try {
      final Response<UserNetworkResponse> response = apiService.updateMilestones(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserNetworkResponse userNetwork = response.body();
        final Optional<UserEntity2> toReturn =
            toUserEntityMapper.transform(Optional.fromNullable(userNetwork));
        notifySuccessResponse(callback, toReturn);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  private <T> void notifySuccessResponse(Callback<T> callback, T response) {
    if (callback != null) {
      callback.onSuccess(response);
    }
  }

  private void notifyErrorResponse(Callback<?> callback, Throwable error) {
    if (callback != null) {
      callback.onError(error);
    }
  }

  private ErrorCore<?> mapToErrorCore(Throwable throwable) {
    return errorAdapter.of(throwable);
  }

}
