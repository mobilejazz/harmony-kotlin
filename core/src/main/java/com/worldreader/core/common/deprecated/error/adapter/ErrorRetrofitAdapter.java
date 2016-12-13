package com.worldreader.core.common.deprecated.error.adapter;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.exception.NetworkErrorException;
import retrofit.RetrofitError;

public final class ErrorRetrofitAdapter implements ErrorAdapter<RetrofitError> {

  public ErrorRetrofitAdapter() {
  }

  @Override public ErrorCore of(RetrofitError error) {
    switch (error.getKind()) {
      case CONVERSION:
        return ErrorCore.of(NetworkErrorException.of(NetworkErrorException.ErrorType.CONVERSION),
            NetworkErrorException.ErrorType.CONVERSION.getErrorCause());
      case HTTP:
        return ErrorCore.of(NetworkErrorException.of(NetworkErrorException.ErrorType.HTTP),
            NetworkErrorException.ErrorType.HTTP.getErrorCause());
      case NETWORK:
        return ErrorCore.of(NetworkErrorException.of(NetworkErrorException.ErrorType.NETWORK),
            NetworkErrorException.ErrorType.NETWORK.getErrorCause());
      default:
        return ErrorCore.of(NetworkErrorException.of(NetworkErrorException.ErrorType.UNEXPECTED),
            NetworkErrorException.ErrorType.UNEXPECTED.getErrorCause());
    }
  }
}
