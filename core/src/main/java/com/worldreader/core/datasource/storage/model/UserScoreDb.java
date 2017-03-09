package com.worldreader.core.datasource.storage.model;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserScoreTable;

@StorIOSQLiteType(table = UserScoreTable.TABLE) public class UserScoreDb {

  @StorIOSQLiteColumn(name = UserScoreTable.COLUMN_USER_ID, key = true) String userId;
  @StorIOSQLiteColumn(name = UserScoreTable.COLUMN_SCORE_ID, key = true) int scoreId;
  @StorIOSQLiteColumn(name = UserScoreTable.COLUMN_SCORE) int score;
  @StorIOSQLiteColumn(name = UserScoreTable.COLUMN_SYNCHRONIZED) boolean sync;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_CREATED_AT) String createdAt;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_UPDATED_AT) String updatedAt;

  public UserScoreDb() {
  }

  public UserScoreDb(final String userId, final int scoreId, final int score, final boolean sync,
      final String createdAt, final String updatedAt) {
    this.userId = userId;
    this.scoreId = scoreId;
    this.score = score;
    this.sync = sync;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getUserId() {
    return userId;
  }

  public int getScoreId() {
    return scoreId;
  }

  public int getScore() {
    return score;
  }

  public boolean isSync() {
    return sync;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public static class Builder {

    private String userId;
    private int scoreId;
    private int score;
    private boolean sync;
    private String createdAt;
    private String updatedAt;

    public Builder setUserId(final String userId) {
      this.userId = userId;
      return this;
    }

    public Builder setScoreId(final int scoreId) {
      this.scoreId = scoreId;
      return this;
    }

    public Builder setScore(final int score) {
      this.score = score;
      return this;
    }

    public Builder setSync(final boolean sync) {
      this.sync = sync;
      return this;
    }

    public Builder setCreatedAt(final String createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder setUpdatedAt(final String updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public UserScoreDb build() {
      return new UserScoreDb(userId, scoreId, score, sync, createdAt, updatedAt);
    }
  }
}
