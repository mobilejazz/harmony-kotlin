package com.worldreader.core.datasource.mapper.user.userbooklike;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.domain.model.user.UserBookLike;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserBookLikeEntityToListUserBookLikeMapper
    implements Mapper<Optional<List<UserBookLikeEntity>>, Optional<List<UserBookLike>>> {

  private final Mapper<Optional<UserBookLikeEntity>, Optional<UserBookLike>> toUserBookLikeMapper;

  @Inject public ListUserBookLikeEntityToListUserBookLikeMapper(UserBookLikeEntityToUserBookLikeMapper toUserBookLikeMapper) {
    this.toUserBookLikeMapper = toUserBookLikeMapper;
  }

  @Override public Optional<List<UserBookLike>> transform(final Optional<List<UserBookLikeEntity>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookLikeEntity> raw = optional.get();
      final List<UserBookLike> transformed = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBookLikeEntity rawUserBookEntity : raw) {
        final UserBookLike userBookLike = toUserBookLikeMapper.transform(Optional.fromNullable(rawUserBookEntity)).get();
        transformed.add(userBookLike);
      }
      return Optional.of(transformed);
    }
  }
}
