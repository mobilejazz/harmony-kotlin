package com.worldreader.core.datasource.network.general.retrofit;

import android.os.Process;
import android.support.annotation.NonNull;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.util.*;
import java.util.concurrent.*;

public interface RetrofitApiManager2 {

  <S> S createService(Class<S> serviceClass);

  class Builder {

    private final HttpUrl endpoint;

    private Executor callbackExecutor;
    private OkHttpClient okHttpClient;
    private List<Converter.Factory> converterFactories = new ArrayList<>();
    private List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();

    public Builder(@NonNull final HttpUrl endpoint) {
      this.endpoint = endpoint;
    }

    public Builder setCallbackExecutor(@NonNull final Executor executor) {
      if (this.callbackExecutor != null) {
        throw new IllegalArgumentException("callbackExecutor already set!");
      }
      this.callbackExecutor = executor;
      return this;
    }

    public Builder setOkHttpClient(@NonNull final OkHttpClient okHttpClient) {
      if (this.okHttpClient != null) {
        throw new IllegalArgumentException("okHttpClient already set!");
      }
      this.okHttpClient = okHttpClient;
      return this;
    }

    public Builder addConverterFactory(@NonNull final Converter.Factory factory) {
      converterFactories.add(factory);
      return this;
    }

    public Builder addCallAdapterFactory(@NonNull final CallAdapter.Factory factory) {
      callAdapterFactories.add(factory);
      return this;
    }

    public Retrofit create() {
      final Retrofit.Builder builder = new Retrofit.Builder();

      // Validate as soon as possible
      builder.validateEagerly(true);

      builder.baseUrl(endpoint);

      if (callbackExecutor != null) {
        builder.callbackExecutor(callbackExecutor);
      }

      if (okHttpClient != null) {
        builder.client(okHttpClient);
      }

      if (converterFactories.size() > 0) {
        for (Converter.Factory factory : converterFactories) {
          builder.addConverterFactory(factory);
        }
      }

      if (callAdapterFactories.size() > 0) {
        for (CallAdapter.Factory factory : callAdapterFactories) {
          builder.addCallAdapterFactory(factory);
        }
      }

      return builder.build();
    }

  }

  class Executors {

    private static final String THREAD_PREFIX = "Retrofit-Callback-";
    private static final String IDLE_THREAD_NAME = THREAD_PREFIX + "Idle";

    public static Executor nonMainThreadExecutor() {
      return java.util.concurrent.Executors.newCachedThreadPool(new ThreadFactory() {
        @Override public Thread newThread(@NonNull final Runnable r) {
          return new Thread(new Runnable() {
            @Override public void run() {
              Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
              r.run();
            }
          }, IDLE_THREAD_NAME);
        }
      });
    }

  }

}
