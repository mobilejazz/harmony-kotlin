package com.worldreader.core.datasource.storage.mapper.userbooklikes;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.storage.model.UserBookLikeDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserBookLikeEntityToListUserBookLikeDbMapper
    implements Mapper<Optional<List<UserBookLikeEntity>>, Optional<List<UserBookLikeDb>>> {

  private final Mapper<Optional<UserBookLikeEntity>, Optional<UserBookLikeDb>> toUserBookLikeMapper;

  @Inject public ListUserBookLikeEntityToListUserBookLikeDbMapper(UserBookLikeEntityToUserBookLikeDbMapper toUserBookLikeMapper) {
    this.toUserBookLikeMapper = toUserBookLikeMapper;
  }

  @Override public Optional<List<UserBookLikeDb>> transform(final Optional<List<UserBookLikeEntity>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookLikeEntity> raw = optional.get();
      final List<UserBookLikeDb> transformed = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBookLikeEntity rawUserBookEntity : raw) {
        final UserBookLikeDb userBookLike = toUserBookLikeMapper.transform(Optional.fromNullable(rawUserBookEntity)).get();
        transformed.add(userBookLike);
      }
      return Optional.of(transformed);
    }
  }
}
