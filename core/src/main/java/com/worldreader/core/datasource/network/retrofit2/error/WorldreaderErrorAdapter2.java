package com.worldreader.core.datasource.network.retrofit2.error;

import android.content.Context;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.network.exceptions.NetworkErrorException2;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;

import java.net.SocketTimeoutException;

public class WorldreaderErrorAdapter2 implements ErrorAdapter<Throwable> {

  static final int HTTP_UNAUTHORIZED = 401;
  static final int HTTP_NOT_FOUND = 404;
  static final int HTTP_TOKEN_EXPIRED = 400;
  static final int HTTP_CONFLICT = 409;
  static final int HTTP_VALIDATION_FAILED = 422;
  static final int HTTP_SOCIAL_GOOGLE_PLUS_EXCEPTION = 105;
  static final int HTTP_USER_NOT_FOUND_CODE = 404;
  static final int HTTP_SOCIAL_FACEBOOK_EXCEPTION = 104;

  private final Context context;
  private final Retrofit2ErrorAdapter retrofit2ErrorAdapter;
  private final Logger logger;

  public WorldreaderErrorAdapter2(Context context, Retrofit2ErrorAdapter retrofit2ErrorAdapter,
      Logger logger) {
    this.context = context;
    this.retrofit2ErrorAdapter = retrofit2ErrorAdapter;
    this.logger = logger;
  }

  @Override public ErrorCore of(Throwable error) {
    if (error instanceof Retrofit2Error) {
      return retrofit2ErrorAdapter.of(error);
    } else if (error instanceof SocketTimeoutException) {
      return ErrorCore.of(
          NetworkErrorException2.of(NetworkErrorException2.ErrorType.NETWORK, error));
    } else {
      throw new IllegalArgumentException("Not implemented yet!");
    }
  }

}
