package com.worldreader.core.error.user;

import com.worldreader.datasource.api.model.error.WorldreaderError;

import java.util.Map;

public class ConflictException extends RuntimeException {

  private static final String INVALID_EMAIL = "email";
  private static final String USERNAME_ALREADY_TAKEN = "username";

  private final Map<String, WorldreaderError.ErrorField> errors;

  public ConflictException(Map<String, WorldreaderError.ErrorField> errors) {
    this.errors = errors;
  }

  public boolean isEmailModificationInvalid() {
    return errors != null && errors.containsKey(INVALID_EMAIL);
  }

  public boolean isUsernameAlreadyTaken() {
    return errors != null && errors.containsKey(USERNAME_ALREADY_TAKEN);
  }
}
