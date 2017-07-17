package com.worldreader.core.datasource.mapper.user.score;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.domain.model.user.UserScore;

import javax.inject.Inject;

public class UserScoreEntityToUserScoreMapper
    implements Mapper<Optional<UserScoreEntity>, Optional<UserScore>> {

  @Inject public UserScoreEntityToUserScoreMapper() {
  }

  @Override
  public Optional<UserScore> transform(final Optional<UserScoreEntity> userScoreEntityOptional) {
    if (userScoreEntityOptional.isPresent()) {
      final UserScoreEntity raw = userScoreEntityOptional.get();
      final UserScore userScore = new UserScore.Builder()
          .setUserId(raw.getUserId())
          .setBookId(raw.getBookId())
          .setScore(raw.getScore())
          .setPages(raw.getPages())
          .setCreatedAt(raw.getCreatedAt())
          .setUpdatedAt(raw.getUpdatedAt())
          .setSync(raw.isSync())
          .setScoreId(raw.getScoreId())
          .build();

      return Optional.of(userScore);
    } else {
      return Optional.absent();
    }
  }
}
