package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;
import com.worldreader.core.common.annotation.Immutable;

@Immutable public class ResetPasswordNetworkBody {

  @SerializedName("email") private final String email;

  public ResetPasswordNetworkBody(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
