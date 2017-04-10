package com.worldreader.core.datasource.network.general.retrofit.error;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.network.exceptions.NetworkErrorException2;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.error.user.UnAuthorizedUserException;
import retrofit2.Response;

import java.net.SocketTimeoutException;

public class WorldreaderErrorAdapter2 implements ErrorAdapter<Throwable> {

  public static final String INTENT_ACTION_LOGOUT = "com.worldreader.app.action.error";

  private final LocalBroadcastManager localBroadcastManager;
  private final Retrofit2ErrorAdapter retrofit2ErrorAdapter;
  private final Logger logger;

  public WorldreaderErrorAdapter2(final Context context, final Retrofit2ErrorAdapter retrofit2ErrorAdapter, final Logger logger) {
    this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    this.retrofit2ErrorAdapter = retrofit2ErrorAdapter;
    this.logger = logger;
  }

  @Override public ErrorCore of(Throwable error) {
    if (error instanceof Retrofit2Error) {
      final Retrofit2Error retrofitError = (Retrofit2Error) error;
      final Response response = retrofitError.getResponse();
      if (retrofitError.getKind() == Retrofit2Error.Kind.HTTP && response != null) {
        final int code = response.code();
        if (code == HttpStatus.UNAUTHORIZED) {
          localBroadcastManager.sendBroadcast(new Intent(INTENT_ACTION_LOGOUT));
          return ErrorCore.of(new UnAuthorizedUserException());
        } else {
          return retrofit2ErrorAdapter.of(error);
        }
      } else {
        return retrofit2ErrorAdapter.of(error);
      }
    } else if (error instanceof SocketTimeoutException) {
      return ErrorCore.of(NetworkErrorException2.of(NetworkErrorException2.ErrorType.NETWORK, error));
    } else {
      throw new IllegalArgumentException("Add implementation logic if special case arises!");
    }
  }

}
