package com.worldreader.core.datasource.mapper.user.score;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.domain.model.user.UserScore;

import javax.inject.Inject;

public class UserScoreToUserScoreEntityMapper
    implements Mapper<Optional<UserScore>, Optional<UserScoreEntity>> {

  @Inject public UserScoreToUserScoreEntityMapper() {
  }

  @Override
  public Optional<UserScoreEntity> transform(final Optional<UserScore> userScoreOptional) {
    if (userScoreOptional.isPresent()) {
      final UserScore raw = userScoreOptional.get();
      final UserScoreEntity userScoreEntity =
          new UserScoreEntity.Builder().setUserId(raw.getUserId())
              .setScore(raw.getScore())
              .setCreatedAt(raw.getCreatedAt())
              .setUpdatedAt(raw.getUpdatedAt())
              .setSync(raw.isSync())
              .setScoreId(raw.getScoreId())
              .build();

      return Optional.of(userScoreEntity);
    } else {
      return Optional.absent();
    }
  }
}
