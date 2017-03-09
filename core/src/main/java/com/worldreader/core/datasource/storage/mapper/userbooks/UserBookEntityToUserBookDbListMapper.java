package com.worldreader.core.datasource.storage.mapper.userbooks;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.storage.model.UserBookDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserBookEntityToUserBookDbListMapper
    implements Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBookDb>>> {

  private final Mapper<Optional<UserBookEntity>, Optional<UserBookDb>> toUserBookDbMapper;

  @Inject public UserBookEntityToUserBookDbListMapper(
      final Mapper<Optional<UserBookEntity>, Optional<UserBookDb>> toUserBookDbMapper) {
    this.toUserBookDbMapper = toUserBookDbMapper;
  }

  @Override
  public Optional<List<UserBookDb>> transform(final Optional<List<UserBookEntity>> listOptional) {
    if (listOptional.isPresent()) {
      final List<UserBookEntity> userBookEntities = listOptional.get();
      final List<UserBookDb> userBookDbs = new ArrayList<>(userBookEntities.size());

      for (final UserBookEntity userBookEntity : userBookEntities) {
        final Optional<UserBookDb> userBookDbOp =
            toUserBookDbMapper.transform(Optional.fromNullable(userBookEntity));

        if (userBookDbOp.isPresent()) {
          userBookDbs.add(userBookDbOp.get());
        }
      }

      return Optional.of(userBookDbs);

    } else {
      return Optional.absent();
    }
  }
}
