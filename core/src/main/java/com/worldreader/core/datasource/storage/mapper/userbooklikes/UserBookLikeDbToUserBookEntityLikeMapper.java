package com.worldreader.core.datasource.storage.mapper.userbooklikes;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.storage.model.UserBookLikeDb;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserBookLikeDbToUserBookEntityLikeMapper implements Mapper<Optional<UserBookLikeDb>, Optional<UserBookLikeEntity>> {

  @Inject public UserBookLikeDbToUserBookEntityLikeMapper() {
  }

  @Override public Optional<UserBookLikeEntity> transform(final Optional<UserBookLikeDb> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookLikeDb raw = optional.get();
      final UserBookLikeEntity entity = new UserBookLikeEntity.Builder()
          .withBookId(raw.getBookId())
          .withUserId(raw.getUserId())
          .withLiked(raw.isLiked())
          .withSync(raw.isSync())
          .withLikedAt(raw.getLikedAt())
          .build();
      return Optional.of(entity);
    }
  }

}
