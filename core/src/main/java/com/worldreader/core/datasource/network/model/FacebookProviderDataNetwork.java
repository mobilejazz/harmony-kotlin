package com.worldreader.core.datasource.network.model;

public class FacebookProviderDataNetwork implements
    RegisterProviderDataNetwork<FacebookProviderDataNetwork.NetworkFacebookRegisterData> {

  private final NetworkFacebookRegisterData facebookRegisterData;

  public FacebookProviderDataNetwork(String facebookToken, String referrerDeviceId, String referrerUserId) {
    this.facebookRegisterData = new NetworkFacebookRegisterData(facebookToken, referrerDeviceId, referrerUserId);
  }

  @Override public NetworkFacebookRegisterData get() {
    return facebookRegisterData;
  }

  public static class NetworkFacebookRegisterData extends BaseNetworkRegisterData {

    private final String facebookToken;

    public NetworkFacebookRegisterData(String facebookToken, String referrerDeviceId, String referrerUserId) {
      super(referrerDeviceId, referrerUserId);
      this.facebookToken = facebookToken;
    }

    public String getFacebookToken() {
      return facebookToken;
    }
  }

}
