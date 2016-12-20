package com.worldreader.core.datasource;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.mapper.RatingEntityDataMapper;
import com.worldreader.core.datasource.network.datasource.rating.RatingNetworkDataSource;
import com.worldreader.core.domain.model.Score;
import com.worldreader.core.domain.repository.RatingRepository;

import javax.inject.Inject;

public class RatingDataSource implements RatingRepository {

  private RatingNetworkDataSource ratingNetworkDataSource;
  private RatingEntityDataMapper ratingEntityDataMapper;

  @Inject public RatingDataSource(RatingNetworkDataSource ratingNetworkDataSource,
      RatingEntityDataMapper ratingEntityDataMapper) {
    this.ratingNetworkDataSource = ratingNetworkDataSource;
    this.ratingEntityDataMapper = ratingEntityDataMapper;
  }

  @Override public void rate(String id, Score score, final CompletionCallback<Boolean> callback) {
    ratingNetworkDataSource.rate(id, ratingEntityDataMapper.transformInverse(score),
        new CompletionCallback<Boolean>() {
          @Override public void onSuccess(Boolean result) {
            if (callback != null) {
              callback.onSuccess(result);
            }
          }

          @Override public void onError(ErrorCore error) {
            if (callback != null) {
              callback.onError(error);
            }
          }
        });
  }
}
