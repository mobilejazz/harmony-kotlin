package com.worldreader.core.datasource.storage.mapper.userbooklikes;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.storage.model.UserBookLikeDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton public class ListUserBookLikeDbToListUserBookLikeEntityMapper
    implements Mapper<Optional<List<UserBookLikeDb>>, Optional<List<UserBookLikeEntity>>> {

  private final Mapper<Optional<UserBookLikeDb>, Optional<UserBookLikeEntity>> toUserBookLikeMapper;

  @Inject public ListUserBookLikeDbToListUserBookLikeEntityMapper(final UserBookLikeDbToUserBookEntityLikeMapper toUserBookLikeMapper) {
    this.toUserBookLikeMapper = toUserBookLikeMapper;
  }

  @Override public Optional<List<UserBookLikeEntity>> transform(final Optional<List<UserBookLikeDb>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookLikeDb> raw = optional.get();
      final List<UserBookLikeEntity> entities = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBookLikeDb userBook : raw) {
        final UserBookLikeEntity entity = toUserBookLikeMapper.transform(Optional.fromNullable(userBook)).orNull();
        entities.add(entity);
      }
      return Optional.of(entities);
    }
  }

}
