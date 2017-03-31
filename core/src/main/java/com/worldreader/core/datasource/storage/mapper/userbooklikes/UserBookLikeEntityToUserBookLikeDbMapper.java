package com.worldreader.core.datasource.storage.mapper.userbooklikes;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.storage.model.UserBookLikeDb;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserBookLikeEntityToUserBookLikeDbMapper implements Mapper<Optional<UserBookLikeEntity>, Optional<UserBookLikeDb>> {

  @Inject public UserBookLikeEntityToUserBookLikeDbMapper() {
  }

  @Override public Optional<UserBookLikeDb> transform(Optional<UserBookLikeEntity> optionalEntity) {
    if (!optionalEntity.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookLikeEntity entity = optionalEntity.get();
      final UserBookLikeDb userBook = new UserBookLikeDb.Builder()
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
