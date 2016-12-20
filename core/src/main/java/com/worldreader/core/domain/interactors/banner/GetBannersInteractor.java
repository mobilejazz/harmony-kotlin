package com.worldreader.core.domain.interactors.banner;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Banner;

import java.util.*;

public interface GetBannersInteractor {

  enum Type {
    MAIN,
    COLLECTION
  }

  void execute(Type type, int index, int limit, DomainCallback<List<Banner>, ErrorCore> callback);
}
