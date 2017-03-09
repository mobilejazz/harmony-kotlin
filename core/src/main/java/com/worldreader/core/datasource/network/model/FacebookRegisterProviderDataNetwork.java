package com.worldreader.core.datasource.network.model;

public class FacebookRegisterProviderDataNetwork implements RegisterProviderDataNetwork<String> {

  private final String facebookToken;

  public FacebookRegisterProviderDataNetwork(final String facebookToken) {
    this.facebookToken = facebookToken;
  }

  @Override public String get() {
    return facebookToken;
  }

}
