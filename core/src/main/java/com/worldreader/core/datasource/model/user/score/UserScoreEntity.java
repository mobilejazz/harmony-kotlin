package com.worldreader.core.datasource.model.user.score;

import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

public class UserScoreEntity extends RepositoryModel {

  private final String userId;
  private final int scoreId;
  private final int score;
  private final boolean sync;
  private final Date createdAt;
  private final Date updatedAt;

  public UserScoreEntity(final String userId, final int scoreId, final int score,
      final boolean sync, final Date createdAt, final Date updatedAt) {
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

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  @Override public String getIdentifier() {
    return userId;
  }

  public static class Builder {

    private String userId;
    private int scoreId;
    private int score;
    private boolean sync;
    private Date createdAt;
    private Date updatedAt;

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

    public Builder setCreatedAt(final Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder setUpdatedAt(final Date updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public UserScoreEntity build() {
      return new UserScoreEntity(userId, scoreId, score, sync, createdAt, updatedAt);
    }
  }

}
