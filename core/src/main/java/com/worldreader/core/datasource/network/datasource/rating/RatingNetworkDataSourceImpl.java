package com.worldreader.core.datasource.network.datasource.rating;

import android.support.annotation.NonNull;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.ScoreEntity;
import retrofit2.Call;

import javax.inject.Inject;

public class RatingNetworkDataSourceImpl implements RatingNetworkDataSource {

  public static final String TAG = RatingNetworkDataSource.class.getSimpleName();

  private final RatingApiService ratingApiService;
  private final Logger logger;

  @Inject public RatingNetworkDataSourceImpl(RatingApiService ratingApiService, Logger logger) {
    this.ratingApiService = ratingApiService;
    this.logger = logger;
  }

  @Override public void rate(String id, ScoreEntity scoreEntity, final CompletionCallback<Boolean> callback) {
    ratingApiService.rate(id, scoreEntity).enqueue(new retrofit2.Callback<Void>() {
      @Override public void onResponse(@NonNull final Call<Void> call, @NonNull final retrofit2.Response<Void> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            callback.onSuccess(true);
          }
        } else {
          if (callback != null) {
            callback.onSuccess(false);
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<Void> call, @NonNull final Throwable t) {
        if (callback != null) {
          callback.onSuccess(false);
        }
      }
    });
  }
}
