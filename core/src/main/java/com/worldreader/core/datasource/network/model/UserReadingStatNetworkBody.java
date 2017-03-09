package com.worldreader.core.datasource.network.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.worldreader.core.common.annotation.Immutable;

import java.util.*;

@Immutable public class UserReadingStatNetworkBody {

  @SerializedName("start") private final Date start;
  @SerializedName("end") private final Date end;

  public UserReadingStatNetworkBody(@NonNull Date start, @NonNull Date end) {
    this.start = start;
    this.end = end;
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

}
