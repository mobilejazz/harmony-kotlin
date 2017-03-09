package com.worldreader.core.datasource.network.mapper.userbooks;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.model.UserBookNetworkBody;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserBookEntityToListUserBookNetworkBodyMapper
    implements Mapper<Optional<List<UserBookEntity>>, Optional<List<UserBookNetworkBody>>> {

  private final UserBookEntityToUserBookNetworkBodyMapper mapper;

  @Inject public ListUserBookEntityToListUserBookNetworkBodyMapper(
      UserBookEntityToUserBookNetworkBodyMapper mapper) {
    this.mapper = mapper;
  }

  @Override public Optional<List<UserBookNetworkBody>> transform(
      final Optional<List<UserBookEntity>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookEntity> raw = optional.get();
      final List<UserBookNetworkBody> toReturn = Lists.newArrayListWithCapacity(raw.size());
      for (final UserBookEntity entity : raw) {
        final UserBookNetworkBody transformed =
            mapper.transform(Optional.fromNullable(entity)).orNull();
        toReturn.add(transformed);
      }
      return Optional.of(toReturn);
    }
  }

}
