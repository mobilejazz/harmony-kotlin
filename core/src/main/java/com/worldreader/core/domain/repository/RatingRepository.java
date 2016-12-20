package com.worldreader.core.domain.repository;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.Score;

public interface RatingRepository {

  void rate(String id, Score score, CompletionCallback<Boolean> callback);

}
