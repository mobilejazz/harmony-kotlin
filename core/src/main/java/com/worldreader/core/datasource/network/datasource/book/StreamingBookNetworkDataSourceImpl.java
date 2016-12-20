package com.worldreader.core.datasource.network.datasource.book;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.datasource.model.ContentOpfLocationEntity;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;

public class StreamingBookNetworkDataSourceImpl implements StreamingBookNetworkDataSource {

  public static final String TAG = StreamingBookNetworkDataSource.class.getSimpleName();

  private final StreamingBookApiService2 streamingBookApiService;
  private final Logger logger;
  private final ErrorAdapter<Throwable> errorAdapter = new Retrofit2ErrorAdapter();

  @Inject
  public StreamingBookNetworkDataSourceImpl(StreamingBookApiService2 streamingBookApiService,
      Logger logger) {
    this.streamingBookApiService = streamingBookApiService;
    this.logger = logger;
  }

  @Override public void retrieveBookMetadata(final String bookId,
      final CompletionCallback<BookMetadataEntity> callback) {
    streamingBookApiService.getContentOpfLocationEntity(bookId,
        StreamingBookApiService2.VERSION_LATEST).enqueue(new Callback<ContentOpfLocationEntity>() {
      @Override public void onResponse(Call<ContentOpfLocationEntity> call,
          Response<ContentOpfLocationEntity> response) {
        if (response.isSuccessful()) {

          final ContentOpfLocationEntity contentContainer = response.body();

          final Call<ContentOpfEntity> contentOpfEntityCall =
              streamingBookApiService.getContentOpfEntity(bookId,
                  StreamingBookApiService2.VERSION_LATEST,
                  contentContainer.getRawContentOpfFullPath());

          contentOpfEntityCall.enqueue(new Callback<ContentOpfEntity>() {
            @Override public void onResponse(Call<ContentOpfEntity> call,
                Response<ContentOpfEntity> response) {
              if (response.isSuccessful()) {
                final ContentOpfEntity contentOpf = response.body();

                // Once we currently have the contentContainer and the contentOpf we can generate the BookMetadata
                final BookMetadataEntity bookMetadataEntity = new BookMetadataEntity();
                bookMetadataEntity.setBookId(bookId);
                bookMetadataEntity.setRelativeContentUrl(contentContainer.getContentOpfPath());
                bookMetadataEntity.setContentOpfName(contentContainer.getContentOpfName());
                bookMetadataEntity.setTocResource(contentOpf.getTocEntry());
                bookMetadataEntity.setResources(contentOpf.getManifestEntries());

                callback.onSuccess(bookMetadataEntity);
              } else {
                if (callback != null) {
                  logger.e(TAG, "Exception finding content opf resource!");
                  final Retrofit2Error error = Retrofit2Error.httpError(response);
                  callback.onError(errorAdapter.of(error));
                }
              }
            }

            @Override public void onFailure(Call<ContentOpfEntity> call, Throwable t) {
              if (callback != null) {
                logger.e(TAG, t.toString());
                callback.onError(errorAdapter.of(t));
              }
            }
          });

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

  @Override public StreamingResourceEntity getBookResource(final String id,
      final BookMetadataEntity bookMetadata, final String resource) throws Throwable {
    final String resourcePath =
        bookMetadata.getRelativeContentUrl() != null ? bookMetadata.getRelativeContentUrl()
            + resource : "" + resource;
    final String resourceUrl =
        String.format(StreamingBookApiService2.GET_BOOK_RESOURCE_URL, id, resourcePath);

    final Call<ResponseBody> bookResourceCall =
        streamingBookApiService.getBookResource(resourceUrl);
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
