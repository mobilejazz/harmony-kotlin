package com.worldreader.core.datasource.network.general.retrofit.adapter;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.deprecated.error.exception.NetworkErrorException;
import com.worldreader.core.datasource.network.exceptions.NetworkErrorException2;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;

public class Retrofit2ErrorAdapter implements ErrorAdapter<Throwable> {

  @Override public ErrorCore of(Throwable throwable) {
    if (throwable instanceof Retrofit2Error) {
      return handleRetrofit2Error((Retrofit2Error) throwable);
    } else {
      return ErrorCore.of(Retrofit2Error.unexpectedError(throwable));
    }
  }

  private ErrorCore handleRetrofit2Error(Retrofit2Error error) {
    switch (error.getKind()) {
      case CONVERSION:
        return ErrorCore.of(
            NetworkErrorException2.of(NetworkErrorException2.ErrorType.CONVERSION, error));
      case HTTP:
        return ErrorCore.of(
            NetworkErrorException2.of(NetworkErrorException2.ErrorType.HTTP, error));
      case NETWORK:
        return ErrorCore.of(
            NetworkErrorException2.of(NetworkErrorException2.ErrorType.NETWORK, error));
      default:
        return ErrorCore.of(NetworkErrorException.of(NetworkErrorException.ErrorType.UNEXPECTED),
            NetworkErrorException.ErrorType.UNEXPECTED.getErrorCause());
    }
  }

}
