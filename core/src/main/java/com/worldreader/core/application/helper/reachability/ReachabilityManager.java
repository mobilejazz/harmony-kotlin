package com.worldreader.core.application.helper.reachability;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

public class ReachabilityManager implements Reachability {

  private final Context context;

  @Inject public ReachabilityManager(Context context) {
    Preconditions.checkNotNull(context, "Context must be not null");
    this.context = context;
  }

  @Override public boolean isReachable() {
    final ConnectivityManager connectivityManager =
        ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

    final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }
}
