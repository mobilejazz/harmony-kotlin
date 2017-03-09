package com.worldreader.core.domain.interactors.user;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;

public interface IsBookLikedByUserInteractor {

  void execute(String bookId, DomainCallback<Boolean, ErrorCore<?>> callback);
}
