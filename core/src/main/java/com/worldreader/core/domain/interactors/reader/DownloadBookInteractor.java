package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;

public interface DownloadBookInteractor {

  void execute(String bookId, DomainCallback<Integer, ErrorCore<?>> callback);

  void execute(String bookId, DomainBackgroundCallback<Void, ErrorCore<?>> callback);

  void execute(String bookId, boolean forceBookMetadataRefresh,
      DomainBackgroundCallback<Void, ErrorCore<?>> callback);
}
