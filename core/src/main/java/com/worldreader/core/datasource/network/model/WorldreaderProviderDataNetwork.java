package com.worldreader.core.datasource.network.model;

public class WorldreaderProviderDataNetwork implements
    RegisterProviderDataNetwork<WorldreaderProviderDataNetwork.NetworkWorldreaderRegisterData> {

  private final NetworkWorldreaderRegisterData registerData;

  public WorldreaderProviderDataNetwork(String username, String password, String email,
      String activatorCode, int gender, int age) {
    this.registerData =
        new NetworkWorldreaderRegisterData(username, password, email, activatorCode, gender, age);
  }

  public WorldreaderProviderDataNetwork(String username, String password, String email) {
    this.registerData = new NetworkWorldreaderRegisterData(username, password, email, null, 0, 0);
  }

  @Override public NetworkWorldreaderRegisterData get() {
    return registerData;
  }

  public static class NetworkWorldreaderRegisterData {

    private final String username;
    private final String password;
    private final String email;
    private final String activatorCode;
    private final int gender;
    private final int age;

    public NetworkWorldreaderRegisterData(String username, String password, String email,
        final String activatorCode, final int gender, final int age) {
      this.username = username;
      this.password = password;
      this.email = email;
      this.activatorCode = activatorCode;
      this.gender = gender;
      this.age = age;
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

    public String getActivatorCode() {
      return activatorCode;
    }

    public int getGender() {
      return gender;
    }

    public int getAge() {
      return age;
    }
  }
}
