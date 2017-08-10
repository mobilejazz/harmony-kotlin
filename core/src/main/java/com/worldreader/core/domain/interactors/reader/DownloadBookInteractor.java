package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;

public interface DownloadBookInteractor {

  void execute(final String bookId, final String version, DomainCallback<Integer, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, DomainBackgroundCallback<Void, ErrorCore<?>> callback);

  void execute(final String bookId, final String version, boolean forceBookMetadataRefresh,
      DomainBackgroundCallback<Void, ErrorCore<?>> callback);
}
