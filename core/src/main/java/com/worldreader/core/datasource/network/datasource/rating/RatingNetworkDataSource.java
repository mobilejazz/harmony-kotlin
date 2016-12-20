package com.worldreader.core.datasource.network.datasource.rating;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.ScoreEntity;

public interface RatingNetworkDataSource {

  void rate(String id, ScoreEntity scoreEntity, CompletionCallback<Boolean> callback);

}
