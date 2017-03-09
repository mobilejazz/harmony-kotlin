package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponse {

  @SerializedName("status") public boolean status;

  public ResetPasswordResponse() {
  }

  public boolean isStatus() {
    return status;
  }
}
