package com.worldreader.core.datasource.storage.model;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable;

import static com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserMilestonesTable.TABLE;

@StorIOSQLiteType(table = TABLE) public class UserMilestoneDb {

  public static final int STATE_PENDING = 0;
  public static final int STATE_IN_PROGRESS = 1;
  public static final int STATE_DONE = 2;

  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_USER_ID, key = true) String userId;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_MILESTONE_ID, key = true) String
      milestoneId;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_SCORE) int score;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_SYNCHRONIZED) boolean sync;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_STATUS) int state;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_CREATED_AT) String createdAt;
  @StorIOSQLiteColumn(name = UserMilestonesTable.COLUMN_UPDATED_AT) String updatedAt;

  public UserMilestoneDb() {
  }

  public UserMilestoneDb(final String userId, final String milestoneId, final int score,
      final boolean sync, final int state, final String createdAt, final String updatedAt) {
    this.userId = userId;
    this.milestoneId = milestoneId;
    this.score = score;
    this.sync = sync;
    this.state = state;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getUserId() {
    return userId;
  }

  public String getMilestoneId() {
    return milestoneId;
  }

  public int getScore() {
    return score;
  }

  public boolean isSync() {
    return sync;
  }

  public int getState() {
    return state;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public static final class Builder {

    private String userId;
    private String milestoneId;
    private int score;
    private boolean sync;
    private int state;
    private String createdAt;
    private String updatedAt;

    public Builder() {
    }

    public Builder withUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder withMilestoneId(String milestoneId) {
      this.milestoneId = milestoneId;
      return this;
    }

    public Builder withScore(int score) {
      this.score = score;
      return this;
    }

    public Builder withSync(boolean sync) {
      this.sync = sync;
      return this;
    }

    public Builder withState(int state) {
      this.state = state;
      return this;
    }

    public Builder withCreatedAt(String createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder withUpdatedAt(String updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public UserMilestoneDb build() {
      return new UserMilestoneDb(userId, milestoneId, score, sync, state, createdAt, updatedAt);
    }
  }
}
