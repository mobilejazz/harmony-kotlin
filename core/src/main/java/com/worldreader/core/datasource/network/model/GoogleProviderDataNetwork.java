package com.worldreader.core.datasource.network.model;

public class GoogleProviderDataNetwork
    implements RegisterProviderDataNetwork<GoogleProviderDataNetwork.NetworkGoogleRegisterData> {

  private final NetworkGoogleRegisterData googleRegisterData;

  public GoogleProviderDataNetwork(String googleId, String name, String email, String referrerDeviceId, String referrerUserId) {
    this.googleRegisterData = new NetworkGoogleRegisterData(googleId, name, email, referrerDeviceId, referrerUserId);
  }

  @Override public NetworkGoogleRegisterData get() {
    return this.googleRegisterData;
  }

  public static class NetworkGoogleRegisterData extends BaseNetworkRegisterData {

    private final String googleId;
    private final String name;
    private final String email;

    public NetworkGoogleRegisterData(String googleId, String name, String email, String referrerDeviceId, String referrerUserId) {
      super(referrerDeviceId, referrerUserId);
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
