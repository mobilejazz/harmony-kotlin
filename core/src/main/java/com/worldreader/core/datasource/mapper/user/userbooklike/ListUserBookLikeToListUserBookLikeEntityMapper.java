package com.worldreader.core.datasource.mapper.user.userbooklike;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.domain.model.user.UserBookLike;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton public class ListUserBookLikeToListUserBookLikeEntityMapper
    implements Mapper<Optional<List<UserBookLike>>, Optional<List<UserBookLikeEntity>>> {

  private final Mapper<Optional<UserBookLike>, Optional<UserBookLikeEntity>> toUserBookLikeEntityMapper;

  @Inject public ListUserBookLikeToListUserBookLikeEntityMapper(UserBookLikeToUserBookEntityLikeMapper toUserBookLikeEntityMapper) {
    this.toUserBookLikeEntityMapper = toUserBookLikeEntityMapper;
  }

  @Override public Optional<List<UserBookLikeEntity>> transform(final Optional<List<UserBookLike>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookLike> raw = optional.get();
      final List<UserBookLikeEntity> transformed = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBookLike rawUserBookEntity : raw) {
        final UserBookLikeEntity userBookLike = toUserBookLikeEntityMapper.transform(Optional.fromNullable(rawUserBookEntity)).get();
        transformed.add(userBookLike);
      }
      return Optional.of(transformed);
    }
  }
}
