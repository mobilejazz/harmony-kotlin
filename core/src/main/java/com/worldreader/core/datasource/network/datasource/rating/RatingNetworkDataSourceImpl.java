package com.worldreader.core.datasource.network.datasource.rating;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.ScoreEntity;
import okhttp3.Response;
import retrofit.Callback;
import retrofit.RetrofitError;

import javax.inject.Inject;

public class RatingNetworkDataSourceImpl implements RatingNetworkDataSource {

  private final RatingApiService ratingApiService;
  private Logger logger;
  public static final String TAG = RatingNetworkDataSource.class.getSimpleName();

  @Inject public RatingNetworkDataSourceImpl(RatingApiService ratingApiService, Logger logger) {
    this.ratingApiService = ratingApiService;
    this.logger = logger;
  }

  @Override
  public void rate(String id, ScoreEntity scoreEntity, final CompletionCallback<Boolean> callback) {
    ratingApiService.rate(id, scoreEntity, new Callback<Response>() {
      @Override public void success(Response response, retrofit.client.Response response2) {
        if (callback != null) {
          callback.onSuccess(true);
        }
      }

      @Override public void failure(RetrofitError error) {
        if (callback != null) {
          logger.e(TAG, error.toString());
          callback.onSuccess(false);
        }
      }
    });
  }
}
