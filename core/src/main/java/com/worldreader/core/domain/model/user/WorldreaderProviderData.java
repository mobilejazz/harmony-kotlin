package com.worldreader.core.domain.model.user;

public class WorldreaderProviderData
    implements RegisterProviderData<WorldreaderProviderData.DomainWorldreaderData> {

  private final DomainWorldreaderData registerData;

  public WorldreaderProviderData(String username, String password, String email) {
    this.registerData = new DomainWorldreaderData(username, password, email);
  }

  public WorldreaderProviderData(String username, String password) {
    this.registerData = new DomainWorldreaderData(username, password, null);
  }

  @Override public DomainWorldreaderData get() {
    return registerData;
  }

  public static class DomainWorldreaderData {

    private final String username;
    private final String password;
    private final String email;

    public DomainWorldreaderData(String username, String password, String email) {
      this.username = username;
      this.password = password;
      this.email = email;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }

    public String getEmail() {
      return email;
    }
  }
}
