package com.worldreader.core.datasource.network.mapper.userbooks;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooklikes.UserBookLikeEntity;
import com.worldreader.core.datasource.network.model.UserBookLikeNetworkResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ListUserBookLikeNetworkResponseToListUserBookLikeEntityMapper
    implements Mapper<Optional<List<UserBookLikeNetworkResponse>>, Optional<List<UserBookLikeEntity>>> {

  @Inject public ListUserBookLikeNetworkResponseToListUserBookLikeEntityMapper() {
  }

  @Override public Optional<List<UserBookLikeEntity>> transform(final Optional<List<UserBookLikeNetworkResponse>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserBookLikeNetworkResponse> raw = optional.get();
      final List<UserBookLikeEntity> entities = new ArrayList<>(raw.size());
      for (final UserBookLikeNetworkResponse response : raw) {
        final UserBookLikeEntity entity = new UserBookLikeEntity.Builder().withBookId(response.getBookId())
            .withSync(true)
            .withLiked(true)
            .withLikedAt(response.getLikedAt())
            .build();
        entities.add(entity);
      }
      return Optional.of(entities);
    }
  }

}
