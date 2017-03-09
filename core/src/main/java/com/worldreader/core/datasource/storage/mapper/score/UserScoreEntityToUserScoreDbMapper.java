package com.worldreader.core.datasource.storage.mapper.score;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.storage.model.UserScoreDb;

import javax.inject.Inject;
import java.util.*;

public class UserScoreEntityToUserScoreDbMapper
    implements Mapper<Optional<UserScoreEntity>, Optional<UserScoreDb>> {

  private final Gson gson;

  @Inject public UserScoreEntityToUserScoreDbMapper(final Gson gson) {
    this.gson = gson;
  }

  @Override
  public Optional<UserScoreDb> transform(final Optional<UserScoreEntity> userScoreEntityOptional) {
    if (userScoreEntityOptional.isPresent()) {
      final UserScoreEntity raw = userScoreEntityOptional.get();
      final UserScoreDb userScoreDb = new UserScoreDb.Builder().setUserId(raw.getUserId())
          .setScore(raw.getScore())
          .setCreatedAt(getIsoDate(raw.getCreatedAt()))
          .setUpdatedAt(getIsoDate(raw.getUpdatedAt()))
          .setSync(raw.isSync())
          .setScoreId(raw.getScoreId())
          .build();

      return Optional.of(userScoreDb);
    } else {
      return Optional.absent();
    }
  }

  private String getIsoDate(final Date createdAt) {
    Date date;
    if (createdAt == null) {
      date = new Date();
    } else {
      date = createdAt;
    }
    return gson.toJson(date).replaceAll("\"", "");
  }
}
