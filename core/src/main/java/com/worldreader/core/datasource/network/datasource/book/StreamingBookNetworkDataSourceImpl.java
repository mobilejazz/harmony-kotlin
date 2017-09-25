package com.worldreader.core.datasource.network.datasource.book;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.worldreader.core.application.di.qualifiers.WorldReaderServer;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.datasource.model.ContentOpfLocationEntity;
import com.worldreader.core.datasource.model.ResourcesCredentialsEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.javatuples.Pair;
import org.simpleframework.xml.Serializer;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class StreamingBookNetworkDataSourceImpl implements StreamingBookNetworkDataSource {

  public static final String TAG = StreamingBookNetworkDataSource.class.getSimpleName();

  private static final String RESOURCES_CREDENTIALS_KEY = "resources.credentials.key";

  private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^[^?]*\\.(gif|png|jpe|jpeg|jpg|jps|bmp|webp).*", Pattern.CASE_INSENSITIVE);

  private static final String GET_BOOK_RESOURCE_URL = "/books/%s/latest/content/%s";

  private final HttpUrl resourceEndpointUrl;
  private final OkHttpClient httpClient;
  private final Gson gson;
  private final Serializer xmlSerializer;
  private final ErrorAdapter<Throwable> errorAdapter = new Retrofit2ErrorAdapter();

  private final Cache<String, ResourcesCredentialsEntity> cache;

  @Inject public StreamingBookNetworkDataSourceImpl(HttpUrl resourceEndpointUrl, @WorldReaderServer final OkHttpClient httpClient, Gson gson,
      Serializer xmlSerializer) {
    this.resourceEndpointUrl = resourceEndpointUrl;
    this.httpClient = httpClient;
    this.gson = gson;
    this.xmlSerializer = xmlSerializer;
    this.cache = CacheBuilder.newBuilder().maximumSize(1).expireAfterAccess(60, TimeUnit.MINUTES).build();
  }

  @Override public void retrieveBookMetadata(final String bookId, final String version,
      final com.worldreader.core.common.callback.Callback<Pair<BookMetadataEntity, InputStream>> callback) {
    // Obtain ContentOpfLocation, so we create the url needed to do so
    final HttpUrl contentOpfLocationUrl = buildContentOpfLocationUrl(bookId, version);
    final Request contentOpfLocationRequest = new Request.Builder().url(contentOpfLocationUrl).build();

    // Obtain content opf location outside interactor thread
    httpClient.newCall(contentOpfLocationRequest).enqueue(new okhttp3.Callback() {
      @Override public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
        final boolean successful = response.isSuccessful();
        if (successful) {
          final ResponseBody rawBody = response.body();
          final ContentOpfLocationEntity contentOpfLocation;
          try {
            contentOpfLocation = xmlSerializer.read(ContentOpfLocationEntity.class, rawBody.charStream(), false);
          } catch (Exception e) {
            // Invalid XML response, so we return back early with the same error that Retrofit would raise
            throw new IOException("Can't parse properly this XML", e);
          }

          // Let's try to go first to content_generated.opf
          final String rawContentOpfFullPath = contentOpfLocation.getRawContentOpfFullPath();
          final boolean isRawContentOpfFullPathEmpty = TextUtils.isEmpty(rawContentOpfFullPath);

          if (!isRawContentOpfFullPathEmpty) {
            final String rawContentOpfFullPathWithoutExtension = rawContentOpfFullPath.replace(".opf", "");
            final String contentOpfFullPath = rawContentOpfFullPathWithoutExtension + "_generated.opf";

            // Perform the network call
            final HttpUrl contentOpfUrl = buildContentOpfUrl(bookId, version, contentOpfFullPath);

            final Request contentOpfRequest = new Request.Builder().url(contentOpfUrl).build();

            // Let's try the first call (done in sync way instead of callback, as this request is already outside interactor thread)
            final okhttp3.Response contentOpfResponse = httpClient.newCall(contentOpfRequest).execute();

            final boolean contentOpfResponseSuccessful = contentOpfResponse.isSuccessful();
            final int contentOpfResponseCode = contentOpfResponse.code();

            // If response is OK, we return back the result converted
            if (contentOpfResponseSuccessful) {
              final Pair<BookMetadataEntity, InputStream> pair =
                  toBookMetadataAndInputStreamPair(contentOpfLocation, contentOpfResponse, bookId, version, true);
              notifySuccessCallback(pair, callback);
            } else if (contentOpfResponseCode == HttpStatus.NOT_FOUND) { // Retry same request without generated.opf

              // Let's build the same request but without _generated.opf
              final HttpUrl contentOpfUrl2 = buildContentOpfUrl(bookId, version, rawContentOpfFullPath);
              final Request contentOpfRequest2 = new Request.Builder().url(contentOpfUrl2).build();

              // Call to network
              final okhttp3.Response contentOpfResponse2 = httpClient.newCall(contentOpfRequest2).execute();

              final boolean contentOpfResponseSuccessful2 = contentOpfResponse2.isSuccessful();

              // If response is OK, we return back the result converted
              if (contentOpfResponseSuccessful2) {
                final Pair<BookMetadataEntity, InputStream> pair =
                    toBookMetadataAndInputStreamPair(contentOpfLocation, contentOpfResponse2, bookId, version, false);
                notifySuccessCallback(pair, callback);
              } else {
                notifyErrorCallback(callback, response);
              }
            } else {
              notifyErrorCallback(callback, response);
            }

          } else {
            notifyErrorCallback(callback, response);
          }

        } else {
          notifyErrorCallback(callback, response);
        }
      }

      @Override public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
        if (callback != null) {
          callback.onError(e);
        }
      }
    });
  }

  private HttpUrl buildContentOpfLocationUrl(final String bookId, final String version) {
    final String containerResource = "META-INF/container.xml";

    final HttpUrl fallbackUrl = HttpUrl.parse(resourceEndpointUrl.toString())
        .newBuilder()
        .addPathSegment("books")
        .addPathSegment(bookId)
        .addPathSegment(version)
        .addPathSegment("content")
        .addPathSegments(containerResource)
        .build();

    return buildResourceUrl(bookId, version, containerResource, false, fallbackUrl);
  }

  private HttpUrl buildContentOpfUrl(final String bookId, final String version, final String contentOpfFullPath) {
    final HttpUrl fallbackUrl = HttpUrl.parse(resourceEndpointUrl.toString())
        .newBuilder()
        .addPathSegment("books")
        .addPathSegment(bookId)
        .addPathSegment(version)
        .addPathSegment("content")
        .addEncodedPathSegments(contentOpfFullPath)
        .build();

    return buildResourceUrl(bookId, version, contentOpfFullPath, false, fallbackUrl);
  }

  private void notifySuccessCallback(Pair<BookMetadataEntity, InputStream> pair, Callback<Pair<BookMetadataEntity, InputStream>> callback) {
    if (callback != null) {
      callback.onSuccess(pair);
    }
  }

  @NonNull private Pair<BookMetadataEntity, InputStream> toBookMetadataAndInputStreamPair(final ContentOpfLocationEntity contentOpfLocation,
      final okhttp3.Response contentOpfResponse, final String bookId, final String version, final boolean isContentOpfGenerated) throws IOException {
    // Extract the original body
    final ResponseBody responseBody = contentOpfResponse.body();
    final BufferedSource responseSource = responseBody != null ? responseBody.source() : null;

    // Double check that everything is good to go
    if (responseSource == null) {
      throw new IOException("Can't parse properly the XML as the response source is null: " + contentOpfResponse.toString());
    }

    // Not highly efficient, but we can't clone properly the response buffer with okio (responseBody.source().buffer().clone() doesn't work as expected)
    final String rawBody = responseBody.string();
    final InputStream bodyIs = new ByteArrayInputStream(rawBody.getBytes(Charsets.UTF_8));

    // Convert the original response to ContentOpfEntity
    final ContentOpfEntity contentOpfEntity;
    try {
      contentOpfEntity = xmlSerializer.read(ContentOpfEntity.class, bodyIs, false);
    } catch (Exception e) {
      throw new IOException("Can't parse properly the XML", e);
    }

    // Reset InputStream to be consumed later
    bodyIs.reset();

    // Let's convert this response into a BookMetadataEntity
    final BookMetadataEntity bookMetadataEntity = toBookMetadataEntity(bookId, version, contentOpfLocation, contentOpfEntity, isContentOpfGenerated);

    // Let's wrap the pair with the values
    return Pair.with(bookMetadataEntity, bodyIs);
  }

  private BookMetadataEntity toBookMetadataEntity(final String bookId, final String version, final ContentOpfLocationEntity contentContainer,
      final ContentOpfEntity contentOpf, boolean isGeneratedOpf) {
    final String rawContentOpfName = contentContainer.getContentOpfName();
    final String contentOpfName = isGeneratedOpf ? rawContentOpfName.substring(0, rawContentOpfName.lastIndexOf(".")) + "_generated.opf" : rawContentOpfName;

    final BookMetadataEntity entity = new BookMetadataEntity();
    entity.setBookId(bookId);
    entity.setVersion(version);
    entity.setRelativeContentUrl(contentContainer.getContentOpfPath());
    entity.setContentOpfName(contentOpfName);
    entity.setTocResource(contentOpf.getTocEntry());
    entity.setResources(contentOpf.getManifestEntriesHref());
    entity.setImagesResources(contentOpf.getImagesResourcesEntries());

    return entity;
  }

  @Override public StreamingResourceEntity getBookResource(final String id, final BookMetadataEntity bookMetadata, final String resource) throws Exception {
    // Create resource url
    final String resourcePath = bookMetadata.getRelativeContentUrl() != null ? bookMetadata.getRelativeContentUrl() + resource : "" + resource;
    final String resourceUrl = String.format(GET_BOOK_RESOURCE_URL, id, resourcePath);

    final HttpUrl url = buildBookResourceUrl(id, bookMetadata.getVersion(), resourceUrl, resourcePath);
    final Request request = new Request.Builder().url(url).build();

    // Perform the network call
    final okhttp3.Response response = httpClient.newCall(request).execute();

    final boolean isSuccessful = response.isSuccessful();

    if (isSuccessful) {
      return StreamingResourceEntity.create(response.body().byteStream());
    } else {
      throw new IOException("Can't process this request: " + response.toString());
    }
  }

  private HttpUrl buildBookResourceUrl(final String id, final String version, final String resourceUrl, final String resourcePath) {
    final boolean isImageRequest = isImageRequest(resourceUrl);

    // Perform a fix for the urls related to images
    final String finalResourcePath;
    if (isImageRequest) {
      finalResourcePath = resourcePath.substring(0, resourcePath.lastIndexOf(".")) + ".jpg";
    } else {
      finalResourcePath = resourcePath;
    }

    final HttpUrl.Builder fallBackUrlBuilder = HttpUrl.parse(resourceEndpointUrl.toString())
        .newBuilder()
        .addPathSegment("books")
        .addPathSegment(id)
        .addPathSegment(version)
        .addPathSegment("content")
        .addEncodedPathSegments(finalResourcePath);

    if (isImageRequest) {
      fallBackUrlBuilder.addQueryParameter("size", "480x800");
    }

    return buildResourceUrl(id, version, finalResourcePath, isImageRequest, fallBackUrlBuilder.build());
  }

  @NonNull private HttpUrl buildResourceUrl(final String bookId, final String version, final String resourcePath, final boolean isImageResource,
      final HttpUrl fallbackUrl) {
    // Check if cache is empty
    ResourcesCredentialsEntity credentials = cache.getIfPresent(RESOURCES_CREDENTIALS_KEY);

    // TODO: 13/09/2017 Add logic for requesting images with different densities
    // If cache entry is not valid, request a new one and store it on cache
    if (!isValidResourcesCredentials(credentials)) {
      cache.invalidateAll();
      credentials = getResourcesCredentials();
      if (credentials != null) {
        cache.put(RESOURCES_CREDENTIALS_KEY, credentials);
      }
    }

    final boolean isCacheEmpty = cache.size() == 0;

    if (!isCacheEmpty) {
      return HttpUrl.parse(credentials.getHost())
          .newBuilder()
          .addPathSegment(credentials.getPrefix())
          .addPathSegment(bookId)
          .addPathSegment(version)
          .addPathSegments(isImageResource ? "content_generated/RESOLUTION_480x800" : "content")
          .addPathSegments(resourcePath)
          .encodedQuery(credentials.getQuery())
          .build();
    } else {
      return fallbackUrl;
    }
  }

  private boolean isValidResourcesCredentials(@Nullable final ResourcesCredentialsEntity credentials) {
    return !(credentials == null || new Date().getTime() > credentials.getDate().getTime());
  }

  private boolean isImageRequest(@Nonnull final String path) {
    return IMAGE_URL_PATTERN.matcher(path).matches();
  }

  private ResourcesCredentialsEntity getResourcesCredentials() {
    final HttpUrl url = HttpUrl.parse(resourceEndpointUrl.toString()).newBuilder().addPathSegments("v1/assets/get_resources_credentials").build();
    final Request request = new Request.Builder().url(url).build();
    try {
      final Response response = httpClient.newCall(request).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final ResponseBody body = response.body();
        return gson.fromJson(body.charStream(), ResourcesCredentialsEntity.class);
      } else {
        return null;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void notifyErrorCallback(com.worldreader.core.common.callback.Callback callback, okhttp3.Response response) {
    final Retrofit2Error error = Retrofit2Error.httpError(response);
    if (callback != null) {
      callback.onError(errorAdapter.of(error).getCause());
    }
  }

}
