package com.worldreader.core.datasource.mapper.user.userbook;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.domain.model.user.UserBook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserBookEntityToListUserBookMapper
    implements Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBook>>> {

  private final Mapper<Optional<UserBookEntity>, Optional<UserBook>> toUserBookMapper;

  @Inject
  public ListUserBookEntityToListUserBookMapper(UserBookEntityToUserBookMapper toUserBookMapper) {
    this.toUserBookMapper = toUserBookMapper;
  }

  @Override
  public Optional<List<UserBook>> transform(final Optional<List<UserBookEntity>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookEntity> raw = optional.get();
      final List<UserBook> transformed = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBookEntity rawUserBookEntity : raw) {
        final UserBook userBook =
            toUserBookMapper.transform(Optional.fromNullable(rawUserBookEntity)).get();
        transformed.add(userBook);
      }
      return Optional.of(transformed);
    }
  }
}
