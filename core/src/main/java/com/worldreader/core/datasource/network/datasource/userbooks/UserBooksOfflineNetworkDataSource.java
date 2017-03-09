package com.worldreader.core.datasource.network.datasource.userbooks;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.*;

public class UserBooksOfflineNetworkDataSource implements UserBooksNetworkDataSource {

  @Override public void userBooks(final Callback<Optional<List<UserBookEntity>>> callback) {
    throw new UnsupportedOperationException("userBooks() not supported");
  }

  @Override
  public void updateUserBooks(final UserBookEntity entity, final Callback<Void> callback) {
    if (callback != null) {
      callback.onSuccess(null);
    }
  }

  @Override public void userBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    notifySuccessResponse(callback, userBookEntity);
  }

  @Override public void updateUserBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    updateAttributeUpdateAt(userBookEntity);

    notifySuccessResponse(callback, userBookEntity);
  }

  private void updateAttributeUpdateAt(final UserBookEntity userBookEntity) {
    userBookEntity.setUpdatedAt(new Date());
  }

  @Override public void deleteUserBook(final UserBookEntity userBookEntity,
      final Callback<Optional<Void>> callback) {
    throw new UnsupportedOperationException("deleteUserBook() not supported");
  }

  @Override public void updateBookReadingStats(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    updateAttributeUpdateAt(userBookEntity);

    if (callback != null) {
      callback.onSuccess(Optional.fromNullable(userBookEntity));
    }
  }

  @Override public void markBookAsFavorite(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    updateAttributeUpdateAt(userBookEntity);

    final UserBookEntity response =
        new UserBookEntity.Builder(userBookEntity).setFavorite(true).setSynchronized(false).build();

    notifySuccessResponse(callback, response);
  }

  @Override public void removeBookAsFavorite(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    updateAttributeUpdateAt(userBookEntity);

    final UserBookEntity response =
        new UserBookEntity.Builder(userBookEntity).setSynchronized(false)
            .setFavorite(false)
            .build();

    notifySuccessResponse(callback, response);
  }

  @Override public void isBookLiked(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    throw new UnsupportedOperationException("isBookLiked() not supported");
  }

  @Override public void likeBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final UserBookEntity response = new UserBookEntity.Builder(userBookEntity).setLiked(true)
        .setSynchronized(false)
        .setUpdatedAt(new Date())
        .build();

    notifySuccessResponse(callback, response);
  }

  @Override public void unlikeBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final UserBookEntity response =
        new UserBookEntity.Builder(userBookEntity).setSynchronized(false)
            .setLiked(false)
            .setUpdatedAt(new Date())
            .build();

    notifySuccessResponse(callback, response);
  }

  @Override public void finishBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final UserBookEntity response = new UserBookEntity.Builder(userBookEntity).setFinished(true)
        .setUpdatedAt(new Date())
        .setSynchronized(false)
        .build();

    notifySuccessResponse(callback, response);
  }

  @Override public void unfinishBook(final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final UserBookEntity response = new UserBookEntity.Builder(userBookEntity).setFinished(false)
        .setUpdatedAt(new Date())
        .setSynchronized(false)
        .build();

    notifySuccessResponse(callback, response);
  }

  @Override
  public void assignCollection(final String collectionId, final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    List<String> collectionIds = userBookEntity.getCollectionIds();

    if (collectionIds == null) {
      collectionIds = new ArrayList<>();
    }

    collectionIds.add(collectionId);

    final UserBookEntity response =
        new UserBookEntity.Builder(userBookEntity).setCollectionIds(collectionIds)
            .setUpdatedAt(new Date())
            .setSynchronized(false)
            .build();

    notifySuccessResponse(callback, response);
  }

  @Override
  public void unassignCollection(final String collectionId, final UserBookEntity userBookEntity,
      final Callback<Optional<UserBookEntity>> callback) {
    final List<String> collectionIds = userBookEntity.getCollectionIds();
    if (collectionIds != null) {
      collectionIds.remove(collectionId);
    }

    final UserBookEntity response =
        new UserBookEntity.Builder(userBookEntity).setCollectionIds(collectionIds)
            .setUpdatedAt(new Date())
            .setSynchronized(false)
            .build();

    notifySuccessResponse(callback, response);
  }

  @Override public void get(final RepositorySpecification specification,
      final Callback<Optional<UserBookEntity>> callback) {
    throw new UnsupportedOperationException("get() not supported");
  }

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    throw new UnsupportedOperationException("getAll() not supported");
  }

  @Override
  public void put(final UserBookEntity userBookEntity, final RepositorySpecification specification,
      final Callback<Optional<UserBookEntity>> callback) {
    final UserBookEntity response =
        new UserBookEntity.Builder(userBookEntity).setSynchronized(false)
            .setUpdatedAt(new Date())
            .build();

    notifySuccessResponse(callback, response);
  }

  @Override public void putAll(final List<UserBookEntity> userBookEntities,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    List<UserBookEntity> response = new ArrayList<>(userBookEntities.size());
    for (final UserBookEntity userBookEntity : userBookEntities) {
      final UserBookEntity raw = new UserBookEntity.Builder(userBookEntity).setSynchronized(false)
          .setUpdatedAt(new Date())
          .build();

      response.add(raw);
    }

    if (callback != null) {
      callback.onSuccess(Optional.of(response));
    }
  }

  @Override public void remove(final UserBookEntity userBookEntity,
      final RepositorySpecification specification,
      final Callback<Optional<UserBookEntity>> callback) {
    throw new UnsupportedOperationException("remove() not supported");
  }

  @Override public void removeAll(final List<UserBookEntity> userBookEntities,
      final RepositorySpecification specification,
      final Callback<Optional<List<UserBookEntity>>> callback) {
    throw new UnsupportedOperationException("removeAll() not supported");
  }

  //region Private methods
  private void notifySuccessResponse(final Callback<Optional<UserBookEntity>> callback,
      final UserBookEntity response) {
    if (callback != null) {
      callback.onSuccess(Optional.of(response));
    }
  }
  //endregion
}
