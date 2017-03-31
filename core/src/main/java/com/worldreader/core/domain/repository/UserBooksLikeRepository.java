package com.worldreader.core.domain.repository;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.user.UserBookLike;

import java.util.List;

public interface UserBooksLikeRepository extends Repository<UserBookLike, RepositorySpecification> {

  void like(String bookId, Callback<Optional<UserBookLike>> callback);

  void like(List<String> bookIds, Callback<Optional<List<UserBookLike>>> callback);

  // TODO: 29/03/2017 Si el network provider es real, no borramos
  // TODO: 29/03/2017 SI no es el real, sync a false y no se borra (se borrara por el user sync process)
  void unlike(String bookId, Callback<Optional<UserBookLike>> callback);

  void unlike(List<String> bookIds, Callback<Optional<List<UserBookLike>>> callback);
}
