package com.worldreader.core.datasource.network.datasource.userbooks;

import android.content.Context;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.datasource.network.model.UserBookNetworkBody;
import com.worldreader.core.datasource.network.model.UserBookNetworkResponse;
import com.worldreader.core.datasource.network.retrofit2.error.WorldreaderErrorAdapter2;
import com.worldreader.core.datasource.network.retrofit2.services.UserBooksApiService;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksLikedStorageSpec;
import com.worldreader.core.datasource.spec.userbooks.GetAllUserBooksNetworkSpec;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.*;
import java.util.*;

public class UserBooksNetworkDataSourceImpl implements UserBooksNetworkDataSource {

  private final UserBooksApiService userBooksApiService;

  private final Mapper<Optional<UserBookNetworkResponse>, Optional<UserBookEntity>>
      toUserBookEntityMapper;
  private final Mapper<Optional<List<UserBookNetworkResponse>>, Optional<List<UserBookEntity>>>
      toUserBookEntityListMapper;
  private final Mapper<Optional<UserBookEntity>, Optional<UserBookNetworkBody>>
      toUserBookNetworkBodyMapper;
  private final Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBookNetworkBody>>>
      toListUserBookNetworkBodyMapper;

  private final ErrorAdapter<Throwable> errorAdapter;

  private final Logger logger;

  @Inject
  public UserBooksNetworkDataSourceImpl(Context context, UserBooksApiService userBooksApiService,
      Mapper<Optional<UserBookNetworkResponse>, Optional<UserBookEntity>> toUserBookEntity,
      Mapper<Optional<List<UserBookNetworkResponse>>, Optional<List<UserBookEntity>>> toUserBookEntityListMapper,
      Mapper<Optional<UserBookEntity>, Optional<UserBookNetworkBody>> toUserBookNetworkBody,
      final Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBookNetworkBody>>> toListUserBookNetworkBodyMapper,
      Logger logger) {
    this.userBooksApiService = userBooksApiService;
    this.toUserBookEntityMapper = toUserBookEntity;
    this.toUserBookEntityListMapper = toUserBookEntityListMapper;
    this.toUserBookNetworkBodyMapper = toUserBookNetworkBody;
    this.toListUserBookNetworkBodyMapper = toListUserBookNetworkBodyMapper;
    this.errorAdapter = new WorldreaderErrorAdapter2(context, new Retrofit2ErrorAdapter(), logger);
    this.logger = logger;
  }

  @Override public void get(RepositorySpecification specification,
      Callback<Optional<UserBookEntity>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    if (specification instanceof GetAllUserBooksNetworkSpec) {
      getAllUserBooks((GetAllUserBooksNetworkSpec) specification, callback);
    } else if (specification instanceof GetAllUserBooksLikedStorageSpec) {
      getAllLikedUserBooks((GetAllUserBooksLikedStorageSpec) specification, callback);
    } else {
      throw new IllegalArgumentException("specification not registered!");
    }
  }

  private void getAllUserBooks(final GetAllUserBooksNetworkSpec ignored,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    try {
      final Response<List<UserBookNetworkResponse>> response =
          userBooksApiService.userBooks().execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final List<UserBookNetworkResponse> userBooksLikeResponse = response.body();
        final Optional<List<UserBookEntity>> toReturn =
            toUserBookEntityListMapper.transform(Optional.fromNullable(userBooksLikeResponse));
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

  private void getAllLikedUserBooks(final GetAllUserBooksLikedStorageSpec specification,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    try {
      final Response<List<UserBookNetworkResponse>> response =
          userBooksApiService.liked().execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final List<UserBookNetworkResponse> userBooksLikeResponse = response.body();
        final Optional<List<UserBookEntity>> toReturn =
            toUserBookEntityListMapper.transform(Optional.fromNullable(userBooksLikeResponse));
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

  @Override public void put(UserBookEntity model, RepositorySpecification specification,
      Callback<Optional<UserBookEntity>> callback) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public void putAll(List<UserBookEntity> userBookEntities, RepositorySpecification specification,
      Callback<Optional<List<UserBookEntity>>> callback) {
    final List<UserBookNetworkBody> userBookNetworkBodies =
        toListUserBookNetworkBodyMapper.transform(Optional.fromNullable(userBookEntities)).orNull();
    try {
      final Response<List<UserBookNetworkResponse>> response =
          userBooksApiService.userBooks(userBookNetworkBodies).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final List<UserBookNetworkResponse> userBookResponse = response.body();
        final Optional<List<UserBookEntity>> toReturn =
            toUserBookEntityListMapper.transform(Optional.fromNullable(userBookResponse));
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

  @Override public void remove(UserBookEntity model, RepositorySpecification specification,
      Callback<Optional<UserBookEntity>> callback) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override public void removeAll(List<UserBookEntity> userBookEntities,
      RepositorySpecification specification, Callback<Optional<List<UserBookEntity>>> callback) {

  }

  @Override public void userBooks(final Callback<Optional<List<UserBookEntity>>> callback) {
    try {
      final Response<List<UserBookNetworkResponse>> response =
          userBooksApiService.userBooks().execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final List<UserBookNetworkResponse> userBookResponse = response.body();
        final Optional<List<UserBookEntity>> toReturn =
            toUserBookEntityListMapper.transform(Optional.fromNullable(userBookResponse));
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

  @Override public void updateUserBooks(UserBookEntity entity, Callback<Void> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void userBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.userBook(userBookEntity.getBookId()).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse userBookResponse = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(userBookResponse));
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

  @Override public void updateUserBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final UserBookNetworkBody body =
        toUserBookNetworkBodyMapper.transform(Optional.fromNullable(userBookEntity)).orNull();
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.updateUserBook(userBookEntity.getBookId(), body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, Optional.<UserBookEntity>absent());
      } else {
        final int code = response.code();
        if (code == HttpStatus.NOT_FOUND) { // User does not have a book entry for the id
          // TODO: 15/11/2016 Should I treat this as big error?
        } else if (code == HttpStatus.BAD_REQUEST) { // Invalid model state
          // TODO: 15/11/2016 This should be a big error
        }
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void deleteUserBook(final UserBookEntity userBookEntity,
      final Callback<Optional<Void>> callback) {
    try {
      final Response<Void> response =
          userBooksApiService.deleteUserBook(userBookEntity.getBookId()).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, Optional.<Void>absent());
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

  @Override public void updateBookReadingStats(final UserBookEntity userBookEntity,
      Callback<Optional<UserBookEntity>> callback) {
  }

  @Override public void markBookAsFavorite(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.markBookAsFavorite(userBookEntity.getBookId()).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse body = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(body));
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

  @Override public void removeBookAsFavorite(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.removeBookAsFavorite(userBookEntity.getBookId()).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse body = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(body));
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

  @Override public void isBookLiked(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.isBookLiked(userBookEntity.getBookId()).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse body = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(body));
        notifySuccessResponse(callback, toReturn);
      } else {
        final int code = response.code();
        if (code
            == HttpStatus.NOT_FOUND) { // User does not have liked this particular book a book entry for the id
          notifySuccessResponse(callback, Optional.<UserBookEntity>absent());
          return;
        }
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void likeBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final String bookId = userBookEntity.getBookId();
    final UserBookNetworkBody body = UserBookNetworkBody.likeBook(bookId);
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.likeBook(userBookEntity.getBookId(), body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse responseBody = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(responseBody));
        notifySuccessResponse(callback, toReturn);
      } else {
        final int code = response.code();
        if (code == HttpStatus.NOT_FOUND) { // 404 - If user has not liked the book
          notifySuccessResponse(callback, Optional.<UserBookEntity>absent());
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

  @Override public void unlikeBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final String bookId = userBookEntity.getBookId();
    final UserBookNetworkBody body = UserBookNetworkBody.unlikeBook(bookId);
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.likeBook(userBookEntity.getBookId(), body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse responseBody = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(responseBody));
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

  @Override public void finishBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final String bookId = userBookEntity.getBookId();
    final UserBookNetworkBody body = UserBookNetworkBody.finishBook(bookId);
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.finishReadBook(bookId, body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse responseBody = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(responseBody));
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

  @Override public void unfinishBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final String bookId = userBookEntity.getBookId();
    final UserBookNetworkBody body = UserBookNetworkBody.unfinishBook(bookId);
    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.finishReadBook(bookId, body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse responseBody = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(responseBody));
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

  @Override
  public void assignCollection(final String collectionId, final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    // Add a collection id to the collection ids
    final ArrayList<String> collectionIds = Lists.newArrayList(userBookEntity.getCollectionIds());
    collectionIds.add(collectionId);
    final UserBookEntity userBookEntityUpdated =
        new UserBookEntity.Builder(userBookEntity).setCollectionIds(collectionIds).build();

    updateCollectionids(userBookEntityUpdated, new Callback<Optional<UserBookEntity>>() {
      @Override public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
        notifySuccessResponse(callback, userBookEntityOptional);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorResponse(callback, e);
      }
    });
  }

  @Override
  public void unassignCollection(final String collectionId, final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final List<String> collectionIds = userBookEntity.getCollectionIds();
    collectionIds.remove(collectionId);
    final UserBookEntity userBookEntityUpdated =
        new UserBookEntity.Builder(userBookEntity).setCollectionIds(collectionIds).build();

    updateCollectionids(userBookEntityUpdated, new Callback<Optional<UserBookEntity>>() {
      @Override public void onSuccess(final Optional<UserBookEntity> userBookEntityOptional) {
        notifySuccessResponse(callback, userBookEntityOptional);
      }

      @Override public void onError(final Throwable e) {
        notifyErrorResponse(callback, e);
      }
    });

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

  private void updateCollectionids(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final String bookId = userBookEntity.getBookId();
    final UserBookNetworkBody body =
        UserBookNetworkBody.updateCollectionIds(bookId, userBookEntity.getCollectionIds());

    try {
      final Response<UserBookNetworkResponse> response =
          userBooksApiService.updateCollectionIds(bookId, body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserBookNetworkResponse raw = response.body();
        final Optional<UserBookEntity> toReturn =
            toUserBookEntityMapper.transform(Optional.fromNullable(raw));
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

  private ErrorCore<?> mapToErrorCore(Throwable throwable) {
    return errorAdapter.of(throwable);
  }
  //endregion

}
