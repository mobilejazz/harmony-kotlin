package com.worldreader.core.datasource.network.datasource.book;

import android.text.TextUtils;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.di.qualifiers.WorldReaderServer;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.datasource.model.ContentOpfLocationEntity;
import com.worldreader.core.datasource.model.ResourcesCredentialsEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.*;

public class StreamingBookNetworkDataSourceImpl implements StreamingBookNetworkDataSource {

  public static final String TAG = StreamingBookNetworkDataSource.class.getSimpleName();

  private final StreamingBookApiService2 streamingBookApiService;
  private final Logger logger;
  private final OkHttpClient okHttpClient;
  private final ErrorAdapter<Throwable> errorAdapter = new Retrofit2ErrorAdapter();

  @Inject
  public StreamingBookNetworkDataSourceImpl(StreamingBookApiService2 streamingBookApiService,
      Logger logger, @WorldReaderServer final OkHttpClient okHttpClient) {
    this.streamingBookApiService = streamingBookApiService;
    this.logger = logger;
    this.okHttpClient = okHttpClient;
  }

  @Override public void retrieveBookMetadata(final String bookId, final String version,
      final CompletionCallback<BookMetadataEntity> callback) {
    streamingBookApiService.getContentOpfLocationEntity(bookId,
        StreamingBookApiService2.VERSION_LATEST).enqueue(new Callback<ContentOpfLocationEntity>() {
      @Override public void onResponse(Call<ContentOpfLocationEntity> call,
          Response<ContentOpfLocationEntity> response) {
        if (response.isSuccessful()) {

          final ContentOpfLocationEntity contentContainer = response.body();
          final String rawContentOpfFullPath = contentContainer.getRawContentOpfFullPath();

          if (!TextUtils.isEmpty(rawContentOpfFullPath)) {

            // Try first to go to content_generated.opf

            final String rawContentOpfFullPathWithoutExtension =
                rawContentOpfFullPath.replace(".opf", "");
            final String contentOpfFullPath =
                rawContentOpfFullPathWithoutExtension + "_generated.opf";
            final Call<ContentOpfEntity> contentOpfEntityCall =
                streamingBookApiService.getContentOpfEntity(bookId,
                    StreamingBookApiService2.VERSION_LATEST, contentOpfFullPath);

            final Callback<ContentOpfEntity> contentOpfEntityCallback =
                new Callback<ContentOpfEntity>() {
                  @Override public void onResponse(Call<ContentOpfEntity> call,
                      Response<ContentOpfEntity> response) {
                    if (response.isSuccessful()) {
                      final ContentOpfEntity contentOpf = response.body();

                      // Once we currently have the contentContainer and the contentOpf we can generate the BookMetadata
                      final BookMetadataEntity bookMetadataEntity = new BookMetadataEntity();
                      bookMetadataEntity.setBookId(bookId);
                      bookMetadataEntity.setVersion(version);
                      bookMetadataEntity.setRelativeContentUrl(
                          contentContainer.getContentOpfPath());
                      bookMetadataEntity.setContentOpfName(contentContainer.getContentOpfName());
                      bookMetadataEntity.setTocResource(contentOpf.getTocEntry());
                      bookMetadataEntity.setResources(contentOpf.getManifestEntries());
                      bookMetadataEntity.setImagesResources(contentOpf.getImagesResourcesEntries());

                      callback.onSuccess(bookMetadataEntity);
                    } else {
                      final int code = response.code();
                      if (code == HttpStatus.NOT_FOUND) {

                        // Try second the real call to content.opf
                        streamingBookApiService.getContentOpfEntity(bookId,
                            StreamingBookApiService2.VERSION_LATEST, rawContentOpfFullPath)
                            .enqueue(new Callback<ContentOpfEntity>() {
                              @Override public void onResponse(final Call<ContentOpfEntity> call,
                                  final Response<ContentOpfEntity> response) {
                                if (response.isSuccessful()) {
                                  final ContentOpfEntity contentOpf = response.body();

                                  // Once we currently have the contentContainer and the contentOpf we can generate the BookMetadata
                                  final BookMetadataEntity bookMetadataEntity =
                                      new BookMetadataEntity();
                                  bookMetadataEntity.setBookId(bookId);
                                  bookMetadataEntity.setVersion(version);
                                  bookMetadataEntity.setRelativeContentUrl(
                                      contentContainer.getContentOpfPath());
                                  bookMetadataEntity.setContentOpfName(
                                      contentContainer.getContentOpfName());
                                  bookMetadataEntity.setTocResource(contentOpf.getTocEntry());
                                  bookMetadataEntity.setResources(contentOpf.getManifestEntries());
                                  bookMetadataEntity.setImagesResources(
                                      contentOpf.getImagesResourcesEntries());

                                  callback.onSuccess(bookMetadataEntity);
                                } else {
                                  if (callback != null) {
                                    logger.e(TAG, "Exception finding content opf resource!");
                                    final Retrofit2Error error = Retrofit2Error.httpError(response);
                                    callback.onError(errorAdapter.of(error));
                                  }
                                }
                              }

                              @Override public void onFailure(final Call<ContentOpfEntity> call,
                                  final Throwable t) {
                                if (callback != null) {
                                  logger.e(TAG, t.toString());
                                  callback.onError(errorAdapter.of(t));
                                }
                              }
                            });

                      } else {
                        if (callback != null) {
                          logger.e(TAG, "Exception finding content opf resource!");
                          final Retrofit2Error error = Retrofit2Error.httpError(response);
                          callback.onError(errorAdapter.of(error));
                        }
                      }
                    }
                  }

                  @Override public void onFailure(Call<ContentOpfEntity> call, Throwable t) {
                    if (callback != null) {
                      logger.e(TAG, t.toString());
                      callback.onError(errorAdapter.of(t));
                    }
                  }
                };

            contentOpfEntityCall.enqueue(contentOpfEntityCallback);
          } else {
            if (callback != null) {
              logger.e(TAG, "Exception with the content opf resource for book id: !" + bookId);
              final Retrofit2Error error = Retrofit2Error.httpError(response);
              callback.onError(errorAdapter.of(error));
            }
          }
        } else {
          if (callback != null) {
            logger.e(TAG, "Exception with the content opf resource for book id: !" + bookId);
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(Call<ContentOpfLocationEntity> call, Throwable t) {
        if (callback != null) {
          logger.e(TAG, "Exception while finding the content opf resource for book id: !" + bookId);
          callback.onError(errorAdapter.of(t));
        }
      }
    });
  }

  private ResourcesCredentialsEntity credentialsEntity;

  @Override public StreamingResourceEntity getBookResource(final String id,
      final BookMetadataEntity bookMetadata, final String resource) throws Exception {
    final String resourcePath =
        bookMetadata.getRelativeContentUrl() != null ? bookMetadata.getRelativeContentUrl()
            + resource : "" + resource;
    final String resourceUrl =
        String.format(StreamingBookApiService2.GET_BOOK_RESOURCE_URL, id, resourcePath);

    if (credentialsEntity == null) {
      this.credentialsEntity = getResourcesCredentials();
    }

    if (credentialsEntity != null) {
      // TODO: 08/08/2017 Improve check if it's a image or a data
      final String queryDelimiter = !(resourcePath.contains(".jpg") || resourcePath.contains(".png") || resourcePath.contains(".gif"))? "?" : "&";

      // TODO: Improve concat with a String.format()...
      final String httpUrl = credentialsEntity.getHost()
          + credentialsEntity.getPrefix()
          + "/"
          + bookMetadata.getBookId()
          + "/"
          + bookMetadata.getVersion()
          + "/content/"
          + resourcePath
          + queryDelimiter
          + credentialsEntity.getQuery();

      final Request request = new Request.Builder().url(httpUrl).get().build();
      final okhttp3.Response response = okHttpClient.newCall(request).execute();

      if (!response.isSuccessful()) {
        throw new Exception("Can't process this request! Request code: "
            + response.code()
            + " Current URL: "
            + resourceUrl);
      }

      return StreamingResourceEntity.create(response.body().byteStream());
    } else {
      final Call<ResponseBody> bookResourceCall =
          streamingBookApiService.getBookResource(resourceUrl);
      logger.d("HTTP", bookResourceCall.request().url().toString());
      final Response<ResponseBody> bodyResponse = bookResourceCall.execute();

      if (!bodyResponse.isSuccessful()) {
        throw new Exception("Can't process this request! Request code: "
            + bodyResponse.code()
            + " Current URL: "
            + resourceUrl);
      }

      return StreamingResourceEntity.create(bodyResponse.body().byteStream());
    }
  }

  @Override public ResourcesCredentialsEntity getResourcesCredentials() {
    try {
      final Response<ResourcesCredentialsEntity> response =
          streamingBookApiService.getResourcesCredentials().execute();

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
