package com.worldreader.core.datasource.storage.mapper.userbooks;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.storage.model.UserBookDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserBookDbToUserBookEntityListMapper
    implements Mapper<Optional<List<UserBookDb>>, Optional<List<UserBookEntity>>> {

  private final Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> toUserBookEntityMapper;

  @Inject public UserBookDbToUserBookEntityListMapper(
      final Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> toUserBookEntityMapper) {
    this.toUserBookEntityMapper = toUserBookEntityMapper;
  }

  @Override
  public Optional<List<UserBookEntity>> transform(final Optional<List<UserBookDb>> listOptional) {
    if (listOptional.isPresent()) {
      final List<UserBookDb> userBookDbs = listOptional.get();
      final List<UserBookEntity> userBookEntities = new ArrayList<>(userBookDbs.size());

      for (final UserBookDb userBookDb : userBookDbs) {
        final Optional<UserBookEntity> userBookEntityOptional =
            toUserBookEntityMapper.transform(Optional.fromNullable(userBookDb));

        if (userBookEntityOptional.isPresent()) {
          userBookEntities.add(userBookEntityOptional.get());
        }
      }

      return Optional.fromNullable(userBookEntities);
    } else {
      return Optional.absent();
    }
  }
}
