package com.worldreader.core.datasource.network.model;

public class WorldreaderProviderDataNetwork implements
    RegisterProviderDataNetwork<WorldreaderProviderDataNetwork.NetworkWorldreaderRegisterData> {

  private final NetworkWorldreaderRegisterData registerData;

  public WorldreaderProviderDataNetwork(String username, String password, String email) {
    this.registerData = new NetworkWorldreaderRegisterData(username, password, email);
  }

  @Override public NetworkWorldreaderRegisterData get() {
    return registerData;
  }

  public static class NetworkWorldreaderRegisterData {

    private final String username;
    private final String password;
    private final String email;

    public NetworkWorldreaderRegisterData(String username, String password, String email) {
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
