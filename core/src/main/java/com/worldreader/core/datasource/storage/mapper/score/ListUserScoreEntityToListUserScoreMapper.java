package com.worldreader.core.datasource.storage.mapper.score;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.domain.model.user.UserScore;

import javax.inject.Inject;
import java.util.*;

public class ListUserScoreEntityToListUserScoreMapper implements Mapper<Optional<List<UserScoreEntity>>, Optional<List<UserScore>>> {

  private final Mapper<Optional<UserScoreEntity>, Optional<UserScore>> mapper;

  @Inject public ListUserScoreEntityToListUserScoreMapper(Mapper<Optional<UserScoreEntity>, Optional<UserScore>> mapper) {
    this.mapper = mapper;
  }

  @Override public Optional<List<UserScore>> transform(final Optional<List<UserScoreEntity>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserScoreEntity> raw = optional.get();
      final List<UserScore> toReturn = new ArrayList<>(raw.size());

      for (final UserScoreEntity entity : raw) {
        final UserScore userScore = mapper.transform(Optional.fromNullable(entity)).get();
        toReturn.add(userScore);
      }

      return Optional.fromNullable(toReturn);
    }
  }
}
