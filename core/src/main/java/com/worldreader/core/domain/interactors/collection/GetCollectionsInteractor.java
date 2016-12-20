package com.worldreader.core.domain.interactors.collection;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.Collection;

import java.util.*;

public interface GetCollectionsInteractor {

  void execute(DomainCallback<List<Collection>, ErrorCore> callback);
}
