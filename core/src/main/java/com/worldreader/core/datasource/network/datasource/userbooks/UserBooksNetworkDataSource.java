package com.worldreader.core.datasource.network.datasource.userbooks;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.*;

public interface UserBooksNetworkDataSource
    extends Repository.Network<UserBookEntity, RepositorySpecification> {

  void userBooks(Callback<Optional<List<UserBookEntity>>> callback);

  void updateUserBooks(UserBookEntity entity, Callback<Void> callback);

  void userBook(UserBookEntity userBookEntity, Callback<Optional<UserBookEntity>> callback);

  void updateUserBook(UserBookEntity userBookEntity, Callback<Optional<UserBookEntity>> callback);

  void deleteUserBook(UserBookEntity userBookEntity, Callback<Optional<Void>> callback);

  void updateBookReadingStats(UserBookEntity userBookEntity,
      Callback<Optional<UserBookEntity>> callback);

  void markBookAsFavorite(UserBookEntity userBookEntity,
      Callback<Optional<UserBookEntity>> callback);

  void removeBookAsFavorite(UserBookEntity userBookEntity,
      Callback<Optional<UserBookEntity>> callback);

  void isBookLiked(UserBookEntity userBookEntity, Callback<Optional<UserBookEntity>> callback);

  void likeBook(UserBookEntity userBookEntity, Callback<Optional<UserBookEntity>> callback);

  void unlikeBook(UserBookEntity userBookEntity, Callback<Optional<UserBookEntity>> callback);

  void finishBook(UserBookEntity userBookEntity, Callback<Optional<UserBookEntity>> callback);

  void unfinishBook(UserBookEntity userBookEntity, Callback<Optional<UserBookEntity>> callback);

  void assignCollection(String collectionId, UserBookEntity userBookEntity,
      Callback<Optional<UserBookEntity>> callback);

  void unassignCollection(String collectionId, UserBookEntity userBookEntity,
      Callback<Optional<UserBookEntity>> callback);

}
