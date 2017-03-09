package com.worldreader.core.domain.model.user;

public class FacebookRegisterProviderData implements RegisterProviderData<String> {

  private final String facebookToken;

  public FacebookRegisterProviderData(final String facebookToken) {
    this.facebookToken = facebookToken;
  }

  @Override public String get() {
    return facebookToken;
  }

}
