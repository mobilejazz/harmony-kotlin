package com.worldreader.core.helper.error;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.network.general.retrofit.error.WorldreaderErrorAdapter2;

public interface ErrorManager {

  class Register {

    public interface OnErrorListener {

      void onErrorReceived();
    }

    public static void listen(final Context context, final OnErrorListener callback) {
      final IntentFilter intentFilter = new IntentFilter(WorldreaderErrorAdapter2.INTENT_ACTION_LOGOUT);
      final BroadcastReceiver errorReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
          if (callback != null) {
            callback.onErrorReceived();
          }
        }
      };
      LocalBroadcastManager.getInstance(context).registerReceiver(errorReceiver, intentFilter);
    }
  }

  void perform(Activity activity, ErrorCore errorCore, int screen);
}
