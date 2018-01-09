package com.worldreader.core.domain.model.user;

public class FacebookProviderData implements RegisterProviderData<String> {

  private final String facebookToken;

  public FacebookProviderData(final String facebookToken) {
    this.facebookToken = facebookToken;
  }

  @Override public String get() {
    return facebookToken;
  }

}
