package com.worldreader.core.datasource.network.mapper.userbooks;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.model.UserBookNetworkResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class ListUserBookNetworkResponseToListUserBookEntityMapper
    implements Mapper<Optional<List<UserBookNetworkResponse>>, Optional<List<UserBookEntity>>> {

  private final Mapper<Optional<UserBookNetworkResponse>, Optional<UserBookEntity>>
      toUserBookEntityMapper;

  @Inject public ListUserBookNetworkResponseToListUserBookEntityMapper(
      UserBookNetworkResponseToUserBookEntityMapper toUserBookEntityMapper) {
    this.toUserBookEntityMapper = toUserBookEntityMapper;
  }

  @Override public Optional<List<UserBookEntity>> transform(
      Optional<List<UserBookNetworkResponse>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookNetworkResponse> bookNetworkResponseList = optional.get();
      final List<UserBookEntity> transformed =
          Lists.newArrayListWithCapacity(bookNetworkResponseList.size());
      for (UserBookNetworkResponse response : bookNetworkResponseList) {
        final UserBookEntity userBookEntity =
            toUserBookEntityMapper.transform(Optional.of(response)).orNull();
        transformed.add(userBookEntity);
      }
      return Optional.of(transformed);
    }
  }
}
