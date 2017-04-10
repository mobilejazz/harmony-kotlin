package com.worldreader.core.datasource.network.datasource.userbookslike;

import com.google.common.base.Optional;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.datasource.network.general.retrofit.services.UserBooksApiService;
import com.worldreader.core.datasource.network.mapper.userbooks.ListUserBookLikeNetworkResponseToListUserBookLikeEntityMapper;
import com.worldreader.core.datasource.network.model.UserBookLikeNetworkResponse;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserBooksLikeNetworkDataSourceImpl implements UserBooksLikeNetworkDataSource {

  private final UserBooksApiService userBooksApiService;

  private final Mapper<Optional<List<UserBookLikeNetworkResponse>>, Optional<List<UserBookLikeEntity>>> toListUserBookLikeEntityMapper;

  private final ErrorAdapter<Throwable> errorAdapter;

  private final Logger logger;

  @Inject public UserBooksLikeNetworkDataSourceImpl(ErrorAdapter<Throwable> errorAdapter, UserBooksApiService userBooksApiService,
      ListUserBookLikeNetworkResponseToListUserBookLikeEntityMapper toListUserBookLikeEntityMapper, Logger logger) {
    this.userBooksApiService = userBooksApiService;
    this.toListUserBookLikeEntityMapper = toListUserBookLikeEntityMapper;
    this.errorAdapter = errorAdapter;
    this.logger = logger;
  }

  @Override public void get(final RepositorySpecification specification, final Callback<Optional<UserBookLikeEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void getAll(final RepositorySpecification specification, final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    try {
      final Response<List<UserBookLikeNetworkResponse>> response = userBooksApiService.likes().execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final List<UserBookLikeNetworkResponse> userBooksLikeResponse = response.body();
        final Optional<List<UserBookLikeEntity>> toReturn = toListUserBookLikeEntityMapper.transform(Optional.fromNullable(userBooksLikeResponse));
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

  @Override public void put(final UserBookLikeEntity userBookLikeEntity, final RepositorySpecification specification,
      final Callback<Optional<UserBookLikeEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void putAll(final List<UserBookLikeEntity> userBookLikeEntities, final RepositorySpecification specification,
      final Callback<Optional<List<UserBookLikeEntity>>> callback) {
    final List<UserBookLikeEntity> responses = new ArrayList<>(userBookLikeEntities.size());

    for (final UserBookLikeEntity entity : userBookLikeEntities) {
      final String bookId = entity.getBookId();
      final boolean liked = entity.isLiked();

      final Callback<Optional<UserBookLikeEntity>> responseCallback = new Callback<Optional<UserBookLikeEntity>>() {
        @Override public void onSuccess(final Optional<UserBookLikeEntity> optional) {
          final UserBookLikeEntity userBookLikeEntity = optional.get();
          responses.add(userBookLikeEntity);
        }

        @Override public void onError(final Throwable e) {
          throw new RuntimeException(e);
        }
      };

      if (liked) {
        likeBook(bookId, responseCallback);
      } else {
        unlikeBook(bookId, responseCallback);
      }
    }

    notifySuccessResponse(callback, Optional.of(responses));
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
            new UserBookLikeEntity.Builder().withLiked(true).withSync(true).withLikedAt(new Date()).withBookId(bookId).build();
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
            new UserBookLikeEntity.Builder().withLiked(false).withSync(true).withLikedAt(new Date()).withBookId(bookId).build();
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
