package com.worldreader.core.datasource.network.general.retrofit.interceptor;

import android.util.Log;
import com.mobilejazz.logger.library.Logger;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLoggingInterceptors {

  private static final String TAG = "HttpLogging";

  public static HttpLoggingInterceptor android() {
    return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
      @Override public void log(String message) {
        Log.d(TAG, message);
      }
    });
  }

  // Avoid by any means using this logger as is a facade for using the Android logger or the bugfender logger
  // this could leak to send important and encrypted user information
  public static HttpLoggingInterceptor logger(final Logger logger) {
    return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
      @Override public void log(String message) {
        logger.d(TAG, message);
      }
    });
  }

}
