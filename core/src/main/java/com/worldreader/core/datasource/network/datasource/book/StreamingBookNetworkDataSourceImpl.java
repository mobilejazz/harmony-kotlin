package com.worldreader.core.datasource.network.datasource.book;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.di.qualifiers.WorldReaderServer;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class StreamingBookNetworkDataSourceImpl implements StreamingBookNetworkDataSource {

  public static final String TAG = StreamingBookNetworkDataSource.class.getSimpleName();

  private static final String RESOURCES_CREDENTIALS_KEY = "resources.credentials.key";

  private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^[^?]*\\.(?:gif|png|jpe|jpeg|jpg|jps|bmp|webp).*", Pattern.CASE_INSENSITIVE);

  private static final String BOOK_CONTENT_IMAGE_MULTIMEDIA_PATH = "content_generated/RESOLUTION_480x800";

  private final StreamingBookApiService2 streamingBookApiService;
  private final Logger logger;
  private final OkHttpClient okHttpClient;
  private final ErrorAdapter<Throwable> errorAdapter = new Retrofit2ErrorAdapter();

  private final Cache<String, ResourcesCredentialsEntity> cache;

  @Inject public StreamingBookNetworkDataSourceImpl(StreamingBookApiService2 streamingBookApiService, Logger logger,
      @WorldReaderServer final OkHttpClient okHttpClient) {
    this.streamingBookApiService = streamingBookApiService;
    this.logger = logger;
    this.okHttpClient = okHttpClient;
    this.cache = CacheBuilder.newBuilder().maximumSize(1).expireAfterAccess(5, TimeUnit.MINUTES).build();
  }

  @Override public void retrieveBookMetadata(final String bookId, final String version,
      final com.worldreader.core.common.callback.Callback<BookMetadataEntity> callback) {
    streamingBookApiService.getContentOpfLocationEntity(bookId, StreamingBookApiService2.VERSION_LATEST)
        .enqueue(new Callback<ContentOpfLocationEntity>() {
          @Override public void onResponse(Call<ContentOpfLocationEntity> call, Response<ContentOpfLocationEntity> response) {
            if (response.isSuccessful()) {

              final ContentOpfLocationEntity contentContainer = response.body();
              final String rawContentOpfFullPath = contentContainer.getRawContentOpfFullPath();

              if (!TextUtils.isEmpty(rawContentOpfFullPath)) {

                // Try first to go to content_generated.opf

                final String rawContentOpfFullPathWithoutExtension = rawContentOpfFullPath.replace(".opf", "");
                final String contentOpfFullPath = rawContentOpfFullPathWithoutExtension + "_generated.opf";
                final Call<ContentOpfEntity> contentOpfEntityCall =
                    streamingBookApiService.getContentOpfEntity(bookId, StreamingBookApiService2.VERSION_LATEST, contentOpfFullPath);

                final Callback<ContentOpfEntity> contentOpfEntityCallback = new Callback<ContentOpfEntity>() {
                  @Override public void onResponse(Call<ContentOpfEntity> call, Response<ContentOpfEntity> response) {
                    if (response.isSuccessful()) {
                      final ContentOpfEntity contentOpf = response.body();

                      // As we successfully gathered the _generated.opf version, we are going to add that extension
                      final String rawContentOpfName = contentContainer.getContentOpfName();
                      final String contentOpfName = rawContentOpfName.substring(0, rawContentOpfName.lastIndexOf("."));

                      // Once we currently have the contentContainer and the contentOpf we can generate the BookMetadata
                      final BookMetadataEntity bookMetadataEntity = new BookMetadataEntity();
                      bookMetadataEntity.setBookId(bookId);
                      bookMetadataEntity.setVersion(version);
                      bookMetadataEntity.setRelativeContentUrl(contentContainer.getContentOpfPath());
                      bookMetadataEntity.setContentOpfName(contentOpfName + "_generated.opf");
                      bookMetadataEntity.setTocResource(contentOpf.getTocEntry());
                      bookMetadataEntity.setResources(contentOpf.getManifestEntries());
                      bookMetadataEntity.setImagesResources(contentOpf.getImagesResourcesEntries());

                      callback.onSuccess(bookMetadataEntity);
                    } else {
                      final int code = response.code();
                      if (code == HttpStatus.NOT_FOUND) {

                        // Try second the real call to content.opf
                        streamingBookApiService.getContentOpfEntity(bookId, StreamingBookApiService2.VERSION_LATEST, rawContentOpfFullPath)
                            .enqueue(new Callback<ContentOpfEntity>() {
                              @Override public void onResponse(final Call<ContentOpfEntity> call, final Response<ContentOpfEntity> response) {
                                if (response.isSuccessful()) {
                                  final ContentOpfEntity contentOpf = response.body();

                                  // Once we currently have the contentContainer and the contentOpf we can generate the BookMetadata
                                  final BookMetadataEntity bookMetadataEntity = new BookMetadataEntity();
                                  bookMetadataEntity.setBookId(bookId);
                                  bookMetadataEntity.setVersion(version);
                                  bookMetadataEntity.setRelativeContentUrl(contentContainer.getContentOpfPath());
                                  bookMetadataEntity.setContentOpfName(contentContainer.getContentOpfName());
                                  bookMetadataEntity.setTocResource(contentOpf.getTocEntry());
                                  bookMetadataEntity.setResources(contentOpf.getManifestEntries());
                                  bookMetadataEntity.setImagesResources(contentOpf.getImagesResourcesEntries());

                                  callback.onSuccess(bookMetadataEntity);
                                } else {
                                  if (callback != null) {
                                    logger.e(TAG, "Exception finding content opf resource!");
                                    final Retrofit2Error error = Retrofit2Error.httpError(response);
                                    callback.onError(errorAdapter.of(error).getCause());
                                  }
                                }
                              }

                              @Override public void onFailure(final Call<ContentOpfEntity> call, final Throwable t) {
                                if (callback != null) {
                                  logger.e(TAG, t.toString());
                                  callback.onError(errorAdapter.of(t).getCause());
                                }
                              }
                            });

                      } else {
                        if (callback != null) {
                          logger.e(TAG, "Exception finding content opf resource!");
                          final Retrofit2Error error = Retrofit2Error.httpError(response);
                          callback.onError(errorAdapter.of(error).getCause());
                        }
                      }
                    }
                  }

                  @Override public void onFailure(Call<ContentOpfEntity> call, Throwable t) {
                    if (callback != null) {
                      logger.e(TAG, t.toString());
                      callback.onError(errorAdapter.of(t).getCause());
                    }
                  }
                };

                contentOpfEntityCall.enqueue(contentOpfEntityCallback);
              } else {
                if (callback != null) {
                  logger.e(TAG, "Exception with the content opf resource for book id: !" + bookId);
                  final Retrofit2Error error = Retrofit2Error.httpError(response);
                  callback.onError(errorAdapter.of(error).getCause());
                }
              }
            } else {
              if (callback != null) {
                logger.e(TAG, "Exception with the content opf resource for book id: !" + bookId);
                final Retrofit2Error error = Retrofit2Error.httpError(response);
                callback.onError(errorAdapter.of(error).getCause());
              }
            }
          }

          @Override public void onFailure(Call<ContentOpfLocationEntity> call, Throwable t) {
            if (callback != null) {
              logger.e(TAG, "Exception while finding the content opf resource for book id: !" + bookId);
              callback.onError(errorAdapter.of(t).getCause());
            }
          }
        });
  }

  @Override public StreamingResourceEntity getBookResource(final String id, final BookMetadataEntity bookMetadata, final String resource)
      throws Exception {
    final String resourcePath = bookMetadata.getRelativeContentUrl() != null ? bookMetadata.getRelativeContentUrl() + resource : "" + resource;
    final String resourceUrl = String.format(StreamingBookApiService2.GET_BOOK_RESOURCE_URL, id, resourcePath);

    // Check if cache is empty
    ResourcesCredentialsEntity credentials = cache.getIfPresent(RESOURCES_CREDENTIALS_KEY);

    // If cache entry is not valid, request a new one and store it on cache
    if (!isValidResourcesCredentials(credentials)) {
      cache.invalidateAll();
      credentials = getResourcesCredentials();
      if (credentials != null) {
        cache.put(RESOURCES_CREDENTIALS_KEY, credentials);
      }
    }

    // Check if the request belongs to an image and if we have a valid resource credentials
    if (isImageRequest(resourceUrl) && cache.size() > 0) {

      // Replace original image resource for fixed JPG and remove /content/ from url
      final String fixedPath = resourcePath.substring(0, resourcePath.lastIndexOf(".")) + ".jpg";

      // Compose new url
      final HttpUrl.Builder bookResourceHttpUrlBuilder = HttpUrl.parse(credentials.getHost())
          .newBuilder()
          .addPathSegment(credentials.getPrefix())
          .addPathSegment(bookMetadata.getBookId())
          .addPathSegment(bookMetadata.getVersion())
          .addPathSegments(BOOK_CONTENT_IMAGE_MULTIMEDIA_PATH)
          .addPathSegments(fixedPath)
          .encodedQuery(credentials.getQuery());

      // TODO: 04/09/2017 Prepare code for using low, medium and high quality images depending on bandwidth

      // Build request
      final HttpUrl bookResourceHttpUrl = bookResourceHttpUrlBuilder.build();
      final Request bookResourceRequest = new Request.Builder().url(bookResourceHttpUrl).build();

      // Go to network
      final okhttp3.Response response = okHttpClient.newCall(bookResourceRequest).execute();

      if (!response.isSuccessful()) {
        throw new Exception("Can't process this request! Request code: " + response.code() + " Current URL: " + resourceUrl);
      }

      return StreamingResourceEntity.create(response.body().byteStream());
    }
    //} else if (cache.size() > 0) {
    //  // If we have a valid resource credential, try to fetch the resource directly
    //
    //  final HttpUrl url = HttpUrl.parse(credentials.getHost())
    //      .newBuilder()
    //      .addPathSegment(credentials.getPrefix())
    //      .addPathSegment(bookMetadata.getBookId())
    //      .addPathSegment(bookMetadata.getVersion())
    //      .addPathSegment("content")
    //      .addPathSegments(resource)
    //      .encodedQuery(credentials.getQuery())
    //      .build();
    //
    //  final Call<ResponseBody> bookResourceCall = streamingBookApiService.getBookResource(url.toString());
    //  final Response<ResponseBody> bodyResponse = bookResourceCall.execute();
    //
    //  if (!bodyResponse.isSuccessful()) {
    //    throw new Exception("Can't process this request! Request code: " + bodyResponse.code() + " Current URL: " + resourceUrl);
    //  }
    //
    //  return StreamingResourceEntity.create(bodyResponse.body().byteStream());
    //
    //} else {
    else {
      // As we don't have a valid resource credential, we rely on using the old good known endpoint
      final Call<ResponseBody> bookResourceCall = streamingBookApiService.getBookResource(resourceUrl);
      final Response<ResponseBody> bodyResponse = bookResourceCall.execute();

      if (!bodyResponse.isSuccessful()) {
        throw new Exception("Can't process this request! Request code: " + bodyResponse.code() + " Current URL: " + resourceUrl);
      }

      return StreamingResourceEntity.create(bodyResponse.body().byteStream());
    }
  }

  private boolean isValidResourcesCredentials(@Nullable final ResourcesCredentialsEntity credentials) {
    return !(credentials == null || new Date().getTime() > credentials.getDate().getTime());
  }

  private boolean isImageRequest(@Nonnull final String path) {
    return IMAGE_URL_PATTERN.matcher(path).matches();
  }

  @Override public ResourcesCredentialsEntity getResourcesCredentials() {
    try {
      final Response<ResourcesCredentialsEntity> response = streamingBookApiService.getResourcesCredentials().execute();

      if (response.isSuccessful()) {
        return response.body();
      } else {
        logger.e(TAG, "Couldn't fetch the Resources Credentials");
        return null;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

}
