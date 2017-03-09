package com.worldreader.core.domain.interactors.user;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Score;

public interface UserLikeBookInteractor {

  void execute(String id, Score score, DomainCallback<Double, ErrorCore> callback);

}
