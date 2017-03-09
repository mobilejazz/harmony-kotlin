package com.worldreader.core.error.user;

import com.google.common.base.Preconditions;

public class LoginException extends RuntimeException {

  public enum Kind {
    INVALID_CREDENTIALS, UNKNOWN
  }

  private final Kind kind;

  public LoginException(final Kind kind) {
    this.kind = Preconditions.checkNotNull(kind, "kind == null");
  }

  public Kind getKind() {
    return kind;
  }
}
