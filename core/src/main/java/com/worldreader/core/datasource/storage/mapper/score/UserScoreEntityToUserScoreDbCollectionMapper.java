package com.worldreader.core.datasource.storage.mapper.score;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.storage.model.UserScoreDb;

import javax.inject.Inject;
import java.util.*;

public class UserScoreEntityToUserScoreDbCollectionMapper
    implements Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScoreDb>>> {

  private final Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb;

  @Inject public UserScoreEntityToUserScoreDbCollectionMapper(
      final Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> toUserScoreDb) {
    this.toUserScoreDb = toUserScoreDb;
  }

  @Override
  public Optional<List<UserScoreDb>> transform(final Optional<List<UserScoreEntity>> toMapOp) {
    if (toMapOp.isPresent()) {
      final List<UserScoreEntity> userScoreEntities = toMapOp.get();
      final List<UserScoreDb> userScoreDbs = new ArrayList<>(userScoreEntities.size());

      for (final UserScoreEntity userScoreEntity : userScoreEntities) {
        final Optional<UserScoreDb> userScoreDbOp =
            toUserScoreDb.transform(Optional.fromNullable(userScoreEntity));

        if (userScoreDbOp.isPresent()) {
          userScoreDbs.add(userScoreDbOp.get());
        }
      }

      return Optional.of(userScoreDbs);
    } else {
      return Optional.absent();
    }
  }
}
