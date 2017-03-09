package com.worldreader.core.datasource.network.mapper.userbooks;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.model.UserBookNetworkBody;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserBookEntityToUserBookNetworkBodyMapper
    implements Mapper<Optional<UserBookEntity>, Optional<UserBookNetworkBody>> {

  @Inject public UserBookEntityToUserBookNetworkBodyMapper() {
  }

  @Override public Optional<UserBookNetworkBody> transform(Optional<UserBookEntity> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookEntity raw = optional.get();
      final UserBookNetworkBody userNetworkBody =
          new UserBookNetworkBody.Builder().setId(raw.getId())
              .setUserId(raw.getUserId())
              .setBookId(raw.getBookId())
              .setFinished(raw.isFinished())
              .setUpdatedAt(raw.getUpdatedAt())
              .setCreatedAt(raw.getCreatedAt())
              .setRating(raw.getRating())
              .setBookmark(raw.getBookmark())
              .setFavorite(raw.isFavorite())
              .setLiked(raw.isLiked())
              .setSavedOfflineAt(raw.getSaveOfflineAt())
              .build();
      return Optional.of(userNetworkBody);
    }
  }

}
