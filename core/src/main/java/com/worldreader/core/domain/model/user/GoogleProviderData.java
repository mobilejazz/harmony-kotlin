package com.worldreader.core.domain.model.user;

public class GoogleProviderData
    implements RegisterProviderData<GoogleProviderData.DomainGoogleRegisterData> {

  private final DomainGoogleRegisterData domainGoogleRegisterData;

  public GoogleProviderData(String googleId, String name, String email) {
    this.domainGoogleRegisterData = new DomainGoogleRegisterData(googleId, name, email);
  }

  public GoogleProviderData(String googleId, String email) {
    this.domainGoogleRegisterData = new DomainGoogleRegisterData(googleId, null, email);
  }

  @Override public DomainGoogleRegisterData get() {
    return this.domainGoogleRegisterData;
  }

  public static class DomainGoogleRegisterData {

    private final String googleId;
    private final String name;
    private final String email;

    public DomainGoogleRegisterData(String googleId, String name, String email) {
      this.googleId = googleId;
      this.name = name;
      this.email = email;
    }

    public String getGoogleId() {
      return googleId;
    }

    public String getName() {
      return name;
    }

    public String getEmail() {
      return email;
    }
  }
}
