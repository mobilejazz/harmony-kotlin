package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

public class LocalLibraryNetworkBody {

  @SerializedName("local_library") String localLibrary;

  public LocalLibraryNetworkBody(final String localLibrary) {
    this.localLibrary = localLibrary;
  }

}
