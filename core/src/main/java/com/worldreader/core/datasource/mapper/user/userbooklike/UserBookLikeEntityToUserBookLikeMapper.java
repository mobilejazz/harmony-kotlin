package com.worldreader.core.datasource.mapper.user.userbooklike;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.domain.model.user.UserBookLike;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserBookLikeEntityToUserBookLikeMapper implements Mapper<Optional<UserBookLikeEntity>, Optional<UserBookLike>> {

  @Inject public UserBookLikeEntityToUserBookLikeMapper() {
  }

  @Override public Optional<UserBookLike> transform(Optional<UserBookLikeEntity> optionalEntity) {
    if (!optionalEntity.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookLikeEntity entity = optionalEntity.get();
      final UserBookLike userBook = new UserBookLike.Builder()
          .withBookId(entity.getBookId())
          .withUserId(entity.getUserId())
          .withLiked(entity.isLiked())
          .withSync(entity.isSync())
          .withLikedAt(entity.getLikedAt())
          .build();
      return Optional.of(userBook);
    }
  }

}
