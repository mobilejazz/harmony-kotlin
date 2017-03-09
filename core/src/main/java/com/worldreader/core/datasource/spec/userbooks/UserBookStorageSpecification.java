package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.repository.spec.StorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;

public abstract class UserBookStorageSpecification extends StorageSpecification {

  private UserStorageSpecification.UserTarget target;

  private String bookId;
  private String userId;

  public UserBookStorageSpecification(final String bookId, final String userId) {
    this.bookId = bookId;
    this.userId = userId;
    this.target = null;
  }

  public UserBookStorageSpecification(final String userId) {
    this.userId = userId;
  }

  public UserBookStorageSpecification(UserStorageSpecification.UserTarget target) {
    this.target = target;
  }

  public UserBookStorageSpecification() {
    this.target = UserStorageSpecification.UserTarget.FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS;
  }

  public String getUserId() {
    return userId;
  }

  public String getBookId() {
    return bookId;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }

  public void setBookId(final String bookId) {
    this.bookId = bookId;
  }

  public void setTarget(final UserStorageSpecification.UserTarget target) {
    this.target = target;
  }

  public UserStorageSpecification.UserTarget getTarget() {
    return target;
  }

  public abstract Query toQuery();

  public abstract DeleteQuery toDeleteQuery();

  @Override public String getIdentifier() {
    return UserBookStorageSpecification.class.getSimpleName();
  }
}
