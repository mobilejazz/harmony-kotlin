package com.worldreader.core.domain.repository;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.user.UserBookLike;

import java.util.*;

public interface UserBooksLikeRepository extends Repository<UserBookLike, RepositorySpecification> {

  void like(String bookId, Callback<Optional<UserBookLike>> callback);

  void like(List<String> bookIds, Callback<Optional<List<UserBookLike>>> callback);

  void unlike(String bookId, Callback<Optional<UserBookLike>> callback);

  void unlike(List<String> bookIds, Callback<Optional<List<UserBookLike>>> callback);
}
