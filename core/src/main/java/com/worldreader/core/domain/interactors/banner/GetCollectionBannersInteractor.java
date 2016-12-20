package com.worldreader.core.domain.interactors.banner;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Banner;

import java.util.*;

public interface GetCollectionBannersInteractor {

  void execute(int index, int limit, DomainCallback<List<Banner>, ErrorCore> callback);
}
