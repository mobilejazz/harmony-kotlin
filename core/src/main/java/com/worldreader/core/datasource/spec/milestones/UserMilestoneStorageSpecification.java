package com.worldreader.core.datasource.spec.milestones;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.repository.spec.StorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;

public abstract class UserMilestoneStorageSpecification extends StorageSpecification {

  private UserStorageSpecification.UserTarget userTarget;

  private String userId;
  private String milestoneid;

  public UserMilestoneStorageSpecification(final String milestoneId, final String userId) {
    this.milestoneid = milestoneId;
    this.userId = userId;
  }

  public UserMilestoneStorageSpecification(final String userId) {
    this.userId = userId;
  }

  public UserMilestoneStorageSpecification(UserStorageSpecification.UserTarget target) {
    this.userTarget = target;
  }

  public UserMilestoneStorageSpecification() {
    this.userTarget = UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS;
  }

  public UserStorageSpecification.UserTarget getUserTarget() {
    return userTarget;
  }

  public String getUserId() {
    return userId;
  }

  public String getMilestoneId() {
    return milestoneid;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }

  public void setMilestoneid(final String milestoneid) {
    this.milestoneid = milestoneid;
  }

  public abstract Query toQuery();

  public abstract DeleteQuery toDeleteQuery();

  @Override public String getIdentifier() {
    return this.getClass().getSimpleName();
  }
}
