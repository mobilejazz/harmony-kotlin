package com.worldreader.core.datasource.network.general.okhttp;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.*;

public class EnhancedOkHttpBuilder {

  public static final int DEFAULT_CONNECTION_POOL_MAX_IDLE_CONNECTIONS = 5;

  public static final long DEFAULT_CONNECTION_POOL_KEEP_ALIVE_CONNECTIONS = TimeUnit.SECONDS.toMillis(1);

  public enum LogLevel {
    NONE(HttpLoggingInterceptor.Level.NONE), BASIC(HttpLoggingInterceptor.Level.BASIC), HEADERS(HttpLoggingInterceptor.Level.HEADERS), BODY(
        HttpLoggingInterceptor.Level.BODY);

    private HttpLoggingInterceptor.Level level;

    LogLevel(HttpLoggingInterceptor.Level level) {
      this.level = level;
    }

    HttpLoggingInterceptor.Level toInterceptorLevel() {
      return level;
    }
  }

  private OkHttpClient.Builder delegate;
  private boolean enableLog;
  private LogLevel loglevel;
  private HttpLoggingInterceptor loggingInterceptor;

  // This connection pool is created intentionally for Worldreader for allowing the cancellation of the requests
  // without having problems with the Interactors (i.e when downloading a book)
  public static ConnectionPool createWorldreaderConnectionPool() {
    return new ConnectionPool(DEFAULT_CONNECTION_POOL_MAX_IDLE_CONNECTIONS, DEFAULT_CONNECTION_POOL_KEEP_ALIVE_CONNECTIONS, TimeUnit.MILLISECONDS);
  }

  public EnhancedOkHttpBuilder() {
    delegate = new OkHttpClient.Builder();
  }

  public EnhancedOkHttpBuilder cache(Cache cache) {
    delegate.cache(cache);
    return this;
  }

  public EnhancedOkHttpBuilder addInterceptor(@NonNull final Interceptor interceptor) {
    if (interceptor instanceof HttpLoggingInterceptor) {
      throw new IllegalArgumentException("Enable logging using the logging method");
    }
    delegate.addInterceptor(interceptor);
    return this;
  }

  public EnhancedOkHttpBuilder retryOnConnectionFailure(boolean retry) {
    delegate.retryOnConnectionFailure(retry);
    return this;
  }

  public EnhancedOkHttpBuilder connectionPool(final ConnectionPool connectionPool) {
    delegate.connectionPool(connectionPool);
    return this;
  }

  public EnhancedOkHttpBuilder setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
    delegate.hostnameVerifier(hostnameVerifier);

    if (hostnameVerifier instanceof AcceptAllHostnamesVerifier) { // Let's disable any possible SSL verification (mainly for Charles Proxy)
      try {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
              @Override public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
              }

              @Override public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
              }

              @Override public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
              }
            }
        };

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        delegate.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return this;
  }

  public <T extends HttpLoggingInterceptor> EnhancedOkHttpBuilder logging(final boolean enable, LogLevel loglevel, final T interceptor) {
    this.enableLog = true;
    this.loglevel = loglevel;
    this.loggingInterceptor = interceptor;
    return this;
  }

  public OkHttpClient build() {
    if (enableLog) {
      loggingInterceptor.setLevel(loglevel.toInterceptorLevel());
      delegate.addInterceptor(loggingInterceptor);
    }

    return delegate.build();
  }

  public static class CacheBuilder {

    public static final String DEFAULT_CACHE_NAME = "HttpCache";
    public static final int DEFAULT_CACHE_SIZE = 50 * 1024 * 1024;

    private String cacheName;
    private String cachePath;
    private int cacheSize;

    public CacheBuilder path(@NonNull final String path) {
      if (TextUtils.isEmpty(path)) {
        throw new IllegalArgumentException("path is empty or null");
      }
      this.cachePath = path;
      return this;
    }

    public CacheBuilder name(@NonNull final String name) {
      if (TextUtils.isEmpty(name)) {
        throw new IllegalArgumentException("name is empty or null");
      }
      this.cacheName = name;
      return this;
    }

    public CacheBuilder size(final int size) {
      if (size <= 0) {
        throw new IllegalArgumentException("size must be higher than 0");
      }
      this.cacheSize = size;
      return this;
    }

    public Cache build() {
      final File path = new File(this.cachePath, this.cacheName);
      return new Cache(path, this.cacheSize);
    }

  }

  // Used mainly for debugging purposes
  public static class AcceptAllHostnamesVerifier implements HostnameVerifier {

    @Override public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }

}
