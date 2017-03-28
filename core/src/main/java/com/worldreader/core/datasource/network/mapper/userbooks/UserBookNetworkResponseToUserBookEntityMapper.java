package com.worldreader.core.datasource.network.mapper.userbooks;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.model.UserBookNetworkResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserBookNetworkResponseToUserBookEntityMapper
    implements Mapper<Optional<UserBookNetworkResponse>, Optional<UserBookEntity>> {

  @Inject public UserBookNetworkResponseToUserBookEntityMapper() {
  }

  @Override public Optional<UserBookEntity> transform(Optional<UserBookNetworkResponse> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookNetworkResponse raw = optional.get();

      final UserBookEntity userBookEntity = new UserBookEntity.Builder().setId(raw.getId())
          .setBookId(raw.getBookId())
          .setUserId(raw.getUserId())
          .setSaveOfflineAt(raw.getSaveOfflineAt())
          .setFinished(raw.isFinished())
          .setLiked(raw.isLiked())
          .setInMyBooks(raw.isInMyBooks())
          .setCollectionIds(UserBookNetworkResponse.mapCollectionIds(raw.getCollectionIds()))
          .setBookmark(raw.getBookmark())
          .setCreatedAt(raw.getCreatedAt())
          .setRating(raw.getRating())
          .setUpdatedAt(raw.getUpdatedAt())
          .setSynchronized(true)
          .build();
      return Optional.of(userBookEntity);
    }
  }

}
