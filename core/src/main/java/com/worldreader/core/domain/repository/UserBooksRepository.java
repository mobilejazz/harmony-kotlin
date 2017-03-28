package com.worldreader.core.domain.repository;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.user.UserBook;

public interface UserBooksRepository extends Repository<UserBook, RepositorySpecification> {

  void like(String bookId, Callback<Optional<UserBook>> callback);

  void unlike(String bookId, Callback<Optional<UserBook>> callback);

  void finish(String bookId, Callback<Optional<UserBook>> callback);

  void markInMyBooks(String bookId, Callback<Optional<UserBook>> callback);

  void unmarkInMyBooks(String bookId, Callback<Optional<UserBook>> callback);

  void assignCollection(String bookId, String collectionId, Callback<Optional<UserBook>> callback);

  void unassignCollection(String collectionId, Callback<Void> callback);
}
