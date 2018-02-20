package com.worldreader.core.application.helper.image;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.sromku.simple.storage.Storage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.*;

public class OkHttpImageDownloader implements ImageDownloader {

  private static final String TAG = ImageLoader.class.getSimpleName();

  public static final String IMAGE_CACHE_FOLDER = "image-cache";

  private final Context context;
  private final OkHttpClient okHttpClient;
  private final String endpoint;
  private final Storage storage;
  private final Logger logger;

  public OkHttpImageDownloader(final Context context, final OkHttpClient okHttpClient, final String endpoint, final Storage storage, final Logger logger) {
    this.context = context;
    this.okHttpClient = okHttpClient;
    this.endpoint = endpoint;
    this.storage = storage;
    this.logger = logger;
  }

  @Override public void download(final String key, final String url) {
    final HttpUrl httpUrl = HttpUrl.parse(fixUrl(url));
    final Request r = new Request.Builder()
        .url(httpUrl)
        .build();

    okHttpClient.newCall(r).enqueue(new Callback() {
      @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
        logger.d(TAG, "Problem downloading the image. Exception: " + Throwables.getStackTraceAsString(e));
      }

      @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (!response.isSuccessful()) {
          onFailure(call, new IOException("Response from server has not been successful"));
          return;
        }

        final boolean directoryExists = storage.isDirectoryExists(IMAGE_CACHE_FOLDER);
        if (!directoryExists) {
          logger.d(TAG, "Creating directory for storing images!");
          storage.createDirectory(IMAGE_CACHE_FOLDER, false);
        }

        logger.d(TAG, "Received image stream from server!");
        final BufferedSource source = response.body().source();

        logger.d(TAG, "Storing image in disk!");
        final File imageFile = new File(buildPath(IMAGE_CACHE_FOLDER, getImageFileName(key)));
        final BufferedSink sink = Okio.buffer(Okio.sink(imageFile));
        sink.writeAll(source);
        sink.close();

        logger.d(TAG, "Image stored successfully!");
      }
    });
  }

  @Override public boolean delete(String key) {
    final String fileName = getImageFileName(key);
    return storage.deleteFile(IMAGE_CACHE_FOLDER, fileName);
  }

  @Override public boolean deleteAll() {
    if (storage.isDirectoryExists(IMAGE_CACHE_FOLDER)) {
      storage.deleteDirectory(IMAGE_CACHE_FOLDER);
    }
    return true;
  }

  @Override public File getImage(String key) {
    return storage.getFile(IMAGE_CACHE_FOLDER, getImageFileName(key));
  }

  @Override public boolean hasImage(String key) {
    return false;
  }

  private String getImageFileName(String key) {
    return key + ".jpg";
  }

  private String fixUrl(String url) {
    if (url != null && url.startsWith("/")) {
      if (endpoint.endsWith("/")) {
        url = url.replaceFirst("/", "");
      }
      url = endpoint.concat(url);
    }
    return url;
  }

  private String buildPath(String directoryName, String fileName) {
    return context.getDir(directoryName, Context.MODE_PRIVATE).getAbsolutePath() + File.separator + fileName;
  }
}
