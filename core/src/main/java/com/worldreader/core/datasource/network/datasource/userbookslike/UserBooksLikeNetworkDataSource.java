package com.worldreader.core.datasource.network.datasource.userbookslike;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

public interface UserBooksLikeNetworkDataSource extends Repository.Network<UserBookLikeEntity, RepositorySpecification> {

  void likeBook(String bookId, Callback<Optional<UserBookLikeEntity>> callback);

  void unlikeBook(String bookId, Callback<Optional<UserBookLikeEntity>> callback);

}
