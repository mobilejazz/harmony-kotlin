package com.worldreader.core.datasource.mapper.user.userbooklike;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.domain.model.user.UserBookLike;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton public class ListUserBookLikeToListUserBookLikeMapper implements Mapper<Optional<List<UserBookLike>>, Optional<List<UserBookLikeEntity>>> {

  private final Mapper<Optional<UserBookLike>, Optional<UserBookLikeEntity>> toUserBookLikeMapper;

  @Inject public ListUserBookLikeToListUserBookLikeMapper(final UserBookLikeToUserBookEntityLikeMapper toUserBookLikeMapper) {
    this.toUserBookLikeMapper = toUserBookLikeMapper;
  }

  @Override public Optional<List<UserBookLikeEntity>> transform(final Optional<List<UserBookLike>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookLike> raw = optional.get();
      final List<UserBookLikeEntity> entities = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBookLike userBook : raw) {
        final UserBookLikeEntity entity = toUserBookLikeMapper.transform(Optional.fromNullable(userBook)).orNull();
        entities.add(entity);
      }
      return Optional.of(entities);
    }
  }

}
