package com.worldreader.core.datasource.spec.user;

import android.support.annotation.NonNull;
import com.google.common.base.Preconditions;
import com.worldreader.core.datasource.repository.spec.StorageSpecification;

public class UserStorageSpecification extends StorageSpecification {

  public enum UserTarget {
    LOGGED_IN, ANONYMOUS, FIRST_LOGGED_IN_FALLBACK_TO_ANONYMOUS
  }

  private final UserTarget target;

  public static UserStorageSpecification target(@NonNull UserTarget target) {
    return new UserStorageSpecification(target);
  }

  public UserStorageSpecification(final UserTarget target) {
    this.target = Preconditions.checkNotNull(target, "target == null");
  }

  public UserTarget getTarget() {
    return target;
  }

}
