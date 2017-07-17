package com.worldreader.core.datasource.network.datasource.score;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.network.general.retrofit.services.UserApiService2;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.spec.score.UserScoreNetworkSpecification;
import java.util.List;
import javax.inject.Inject;

public class UserScoreNetworkDataSource implements Repository.Network<UserScoreEntity, UserScoreNetworkSpecification> {

  private final UserApiService2 apiService;
  private final ErrorAdapter<Throwable> errorAdapter;

  @Inject public UserScoreNetworkDataSource(final UserApiService2 apiService, final ErrorAdapter<Throwable> errorAdapter) {
    this.apiService = apiService;
    this.errorAdapter = errorAdapter;
  }

  @Override public void get(final UserScoreNetworkSpecification specification, final Callback<Optional<UserScoreEntity>> callback) {
    throw new UnsupportedOperationException("get() not supported");
  }

  @Override public void getAll(final UserScoreNetworkSpecification specification, final Callback<Optional<List<UserScoreEntity>>> callback) {
    throw new UnsupportedOperationException("getAll() not supported");
  }

  @Override public void put(final UserScoreEntity userScoreEntity, final UserScoreNetworkSpecification specification, final Callback<Optional<UserScoreEntity>> callback) {
    //final UserPointsNetworkBody body = new UserPointsNetworkBody(userScoreEntity.getScore());
    //try {
    //  final Response<UserPointsNetworkResponse> response = apiService.updatePoints(body).execute();
    //  final boolean successful = response.isSuccessful();
    //  if (successful) {
    //    final UserScoreEntity responseNetwork =
    //        new UserScoreEntity.Builder().setScore(response.body().getScore()).build();
    //
    //    notifySuccessResponse(callback, Optional.of(responseNetwork));
    //  } else {
    //    final Retrofit2Error httpError = Retrofit2Error.httpError(response);
    //    final ErrorCore<?> errorCore = mapToErrorCore(httpError);
    //    notifyErrorResponse(callback, errorCore.getCause());
    //  }
    //} catch (IOException e) {
    //  final ErrorCore<?> errorCore = mapToErrorCore(e);
    //  notifyErrorResponse(callback, errorCore.getCause());
    //}
  }

  @Override public void putAll(final List<UserScoreEntity> userScoreEntities, final UserScoreNetworkSpecification specification,
      final Callback<Optional<List<UserScoreEntity>>> callback) {
    throw new UnsupportedOperationException("putAll() not supported");
  }

  @Override public void remove(final UserScoreEntity userScoreEntity, final UserScoreNetworkSpecification specification,
      final Callback<Optional<UserScoreEntity>> callback) {
    throw new UnsupportedOperationException("remove() not supported");
  }

  @Override public void removeAll(final List<UserScoreEntity> userScoreEntities, final UserScoreNetworkSpecification specification,
      final Callback<Optional<List<UserScoreEntity>>> callback) {
    throw new UnsupportedOperationException("removeAll() not supported");
  }

  //region Private methods
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
  //endregion
}
