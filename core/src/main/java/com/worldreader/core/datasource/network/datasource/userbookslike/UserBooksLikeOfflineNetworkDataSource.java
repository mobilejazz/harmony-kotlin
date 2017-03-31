package com.worldreader.core.datasource.network.datasource.userbookslike;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.Date;
import java.util.List;

public class UserBooksLikeOfflineNetworkDataSource implements UserBooksLikeNetworkDataSource {

  public UserBooksLikeOfflineNetworkDataSource() {
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
    final UserBookLikeEntity fakeEntity = new UserBookLikeEntity.Builder()
        .withLiked(true)
        .withSync(false)
        .withLikedAt(new Date())
        .build();
    notifySuccessResponse(callback, fakeEntity);
  }

  @Override public void unlikeBook(final String bookId, final Callback<Optional<UserBookLikeEntity>> callback) {
    final UserBookLikeEntity fakeEntity = new UserBookLikeEntity.Builder()
        .withLiked(false)
        .withSync(false)
        .withLikedAt(new Date())
        .build();
    notifySuccessResponse(callback, fakeEntity);
  }

  //region Private methods
  private void notifySuccessResponse(final Callback<Optional<UserBookLikeEntity>> callback, final UserBookLikeEntity response) {
    if (callback != null) {
      callback.onSuccess(Optional.of(response));
    }
  }
  //endregion
}
