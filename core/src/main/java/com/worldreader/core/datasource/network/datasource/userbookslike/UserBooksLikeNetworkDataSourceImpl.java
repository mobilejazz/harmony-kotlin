package com.worldreader.core.datasource.network.datasource.userbookslike;

import android.content.Context;
import com.google.common.base.Optional;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.error.WorldreaderErrorAdapter2;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.datasource.network.general.retrofit.services.UserBooksApiService;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class UserBooksLikeNetworkDataSourceImpl implements UserBooksLikeNetworkDataSource {

  private final UserBooksApiService userBooksApiService;

  private final ErrorAdapter<Throwable> errorAdapter;

  private final Logger logger;

  @Inject public UserBooksLikeNetworkDataSourceImpl(Context context, UserBooksApiService userBooksApiService, Logger logger) {
    this.userBooksApiService = userBooksApiService;
    this.errorAdapter = new WorldreaderErrorAdapter2(context, new Retrofit2ErrorAdapter(), logger);
    this.logger = logger;
  }

  @Override public void get(final RepositorySpecification specification, final Callback<Optional<UserBookLikeEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void getAll(final RepositorySpecification specification, final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void put(final UserBookLikeEntity userBookLikeEntity, final RepositorySpecification specification,
      final Callback<Optional<UserBookLikeEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void putAll(final List<UserBookLikeEntity> userBookLikeEntities, final RepositorySpecification specification,
      final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void remove(final UserBookLikeEntity userBookLikeEntity, final RepositorySpecification specification,
      final Callback<Optional<UserBookLikeEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void removeAll(final List<UserBookLikeEntity> userBookLikeEntities, final RepositorySpecification specification,
      final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void likeBook(final String bookId, final Callback<Optional<UserBookLikeEntity>> callback) {
    try {
      final Response<Void> response = userBooksApiService.oldLikeBook(bookId).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookLikeEntity userBookLikeEntity =
            new UserBookLikeEntity.Builder().withLiked(true).withSync(true).withLikedAt(new Date()).build();
        final Optional<UserBookLikeEntity> toReturn = Optional.of(userBookLikeEntity);
        notifySuccessResponse(callback, toReturn);
      } else {
        final int code = response.code();
        if (code == HttpStatus.NOT_FOUND) { // 404 - If user has not liked the book
          notifySuccessResponse(callback, Optional.<UserBookLikeEntity>absent());
        } else {
          final Retrofit2Error httpError = Retrofit2Error.httpError(response);
          final ErrorCore<?> errorCore = mapToErrorCore(httpError);
          notifyErrorResponse(callback, errorCore.getCause());
        }
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void unlikeBook(final String bookId, final Callback<Optional<UserBookLikeEntity>> callback) {
    try {
      final Response<Void> response = userBooksApiService.oldUnlikeBook(bookId).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookLikeEntity userBookLikeEntity =
            new UserBookLikeEntity.Builder().withLiked(false).withSync(true).withLikedAt(new Date()).build();
        final Optional<UserBookLikeEntity> toReturn = Optional.of(userBookLikeEntity);
        notifySuccessResponse(callback, toReturn);
      } else {
        final int code = response.code();
        if (code == HttpStatus.NOT_FOUND) { // 404 - If user has not liked the book
          notifySuccessResponse(callback, Optional.<UserBookLikeEntity>absent());
        } else {
          final Retrofit2Error httpError = Retrofit2Error.httpError(response);
          final ErrorCore<?> errorCore = mapToErrorCore(httpError);
          notifyErrorResponse(callback, errorCore.getCause());
        }
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  //region Private methods
  private <T> void notifySuccessResponse(Callback<T> callback, T response) {
    if (callback != null) {
      callback.onSuccess(response);
    }
  }

  private ErrorCore<?> mapToErrorCore(Throwable throwable) {
    return errorAdapter.of(throwable);
  }

  private void notifyErrorResponse(Callback<?> callback, Throwable error) {
    if (callback != null) {
      callback.onError(error);
    }
  }
  //endregion

}
