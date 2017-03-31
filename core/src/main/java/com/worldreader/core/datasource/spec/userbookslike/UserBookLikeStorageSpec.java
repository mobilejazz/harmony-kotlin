package com.worldreader.core.datasource.spec.userbookslike;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.repository.spec.StorageSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;

public abstract class UserBookLikeStorageSpec extends StorageSpecification {

  private UserStorageSpecification.UserTarget target;

  private String bookId;
  private String userId;

  public UserBookLikeStorageSpec(final String bookId, final String userId) {
    this.bookId = bookId;
    this.userId = userId;
    this.target = null;
  }

  public UserBookLikeStorageSpec(final String userId) {
    this.userId = userId;
  }

  public UserBookLikeStorageSpec(UserStorageSpecification.UserTarget target) {
    this.target = target;
  }

  public UserBookLikeStorageSpec() {
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
    return UserBookLikeStorageSpec.class.getSimpleName();
  }
}
