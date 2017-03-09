package com.worldreader.core.domain.model.user;

import com.worldreader.core.datasource.repository.model.RepositoryModel;

import java.util.*;

public class UserMilestone extends RepositoryModel {

  public static final int STATE_PENDING = 0;
  public static final int STATE_IN_PROGRESS = 1;
  public static final int STATE_DONE = 2;

  private final String userId;
  private final String milestoneId;
  private final int score;
  private final boolean sync;
  private final int state;
  private final Date createdAt;
  private final Date updatedAt;

  private UserMilestone(final String userId, final String milestoneId, final int score,
      final boolean sync, final int state, final Date createdAt, final Date updatedAt) {
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

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  @Override public String getIdentifier() {
    return this.getClass().getSimpleName();
  }

  public static final class Builder {

    private String userId;
    private String milestoneId;
    private int score;
    private boolean sync;
    private int state;
    private Date createdAt;
    private Date updatedAt;

    public Builder() {

    }

    public Builder(final UserMilestone userMilestone) {
      this.userId = userMilestone.getUserId();
      this.milestoneId = userMilestone.getMilestoneId();
      this.score = userMilestone.getScore();
      this.sync = userMilestone.isSync();
      this.state = userMilestone.getState();
      this.createdAt = userMilestone.getCreatedAt();
      this.updatedAt = userMilestone.getUpdatedAt();
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

    public Builder withCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder withUpdatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public UserMilestone build() {
      return new UserMilestone(userId, milestoneId, score, sync, state, createdAt, updatedAt);
    }
  }

}
