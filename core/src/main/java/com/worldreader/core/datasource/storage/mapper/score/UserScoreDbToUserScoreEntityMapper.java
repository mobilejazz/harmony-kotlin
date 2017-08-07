package com.worldreader.core.datasource.storage.mapper.score;

import android.text.TextUtils;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.storage.model.UserScoreDb;

import javax.inject.Inject;
import java.util.*;

public class UserScoreDbToUserScoreEntityMapper
    implements Mapper<Optional<UserScoreDb>, Optional<UserScoreEntity>> {

  private final Gson gson;

  @Inject public UserScoreDbToUserScoreEntityMapper(final Gson gson) {
    this.gson = gson;
  }

  @Override public Optional<UserScoreEntity> transform(final Optional<UserScoreDb> userScoreDbOp) {
    if (userScoreDbOp.isPresent()) {
      final UserScoreDb raw = userScoreDbOp.get();
      final UserScoreEntity userScoreDb = new UserScoreEntity.Builder()
          .setUserId(raw.getUserId())
          .setBookId(raw.getBookId())
          .setScore(raw.getScore())
          .setPages(raw.getPages())
          .setCreatedAt(toDate(raw.getCreatedAt()))
          .setUpdatedAt(toDate(raw.getUpdatedAt()))
          .setSync(raw.isSync())
          .setScoreId(raw.getScoreId())
          .build();

      return Optional.of(userScoreDb);
    } else {
      return Optional.absent();
    }
  }

  private Date toDate(final String createdAt) {
    if (TextUtils.isEmpty(createdAt)) {
      return null;
    }

    final String date = "\"" + createdAt + "\""; // Restore previous removed comma

    return gson.fromJson(date, Date.class);
  }
}
