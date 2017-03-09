package com.worldreader.core.datasource.mapper.user.userbook;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.domain.model.user.UserBook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserBookToListUserBookMapper
    implements Mapper<Optional<List<UserBook>>, Optional<List<UserBookEntity>>> {

  private final Mapper<Optional<UserBook>, Optional<UserBookEntity>> toUserBookMapper;

  @Inject
  public ListUserBookToListUserBookMapper(final UserBookToUserBookEntityMapper toUserBookMapper) {
    this.toUserBookMapper = toUserBookMapper;
  }

  @Override
  public Optional<List<UserBookEntity>> transform(final Optional<List<UserBook>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBook> raw = optional.get();
      final List<UserBookEntity> entities = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBook userBook : raw) {
        final UserBookEntity entity =
            toUserBookMapper.transform(Optional.fromNullable(userBook)).orNull();
        entities.add(entity);
      }
      return Optional.of(entities);
    }
  }

}
