package com.worldreader.core.domain.interactors.reader;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.core.domain.thread.MainThread;
import javax.inject.Inject;

public class GetBookMetadataInteractorImpl extends AbstractInteractor<BookMetadata, ErrorCore<?>> implements GetBookMetadataInteractor {

  private StreamingBookRepository streamingBookRepository;

  private String bookId;
  private String version;
  private boolean forceBookMetadataRefresh;

  private DomainCallback<BookMetadata, ErrorCore<?>> callback;
  private DomainBackgroundCallback<BookMetadata, ErrorCore<?>> backgroundCallback;

  @Inject public GetBookMetadataInteractorImpl(InteractorExecutor executor, MainThread mainThread, StreamingBookRepository streamingBookRepository) {
    super(executor, mainThread);
    this.streamingBookRepository = streamingBookRepository;
  }

  @Override public void execute(final String bookId, final String version, final DomainCallback<BookMetadata, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.version = version;
    this.forceBookMetadataRefresh = false;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void execute(final String bookId, final String version, final DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.version = version;
    this.forceBookMetadataRefresh = false;
    this.backgroundCallback = callback;
    this.executor.run(this);
  }

  @Override public void execute(final String bookId, final String version, final boolean forceRefreshBookMetadata, final DomainBackgroundCallback<BookMetadata, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.version = version;
    this.backgroundCallback = callback;
    this.forceBookMetadataRefresh = forceRefreshBookMetadata;
    this.executor.run(this);
  }

  @Override public void run() {
    streamingBookRepository.retrieveBookMetadata(this.bookId, this.version, this.forceBookMetadataRefresh, new CompletionCallback<BookMetadata>() {
      @Override public void onSuccess(final BookMetadata result) {
        if (backgroundCallback != null) {
          backgroundCallback.onSuccess(result);
        } else {
          performSuccessCallback(callback, result);
        }
      }

      @Override public void onError(final ErrorCore error) {
        if (backgroundCallback != null) {
          backgroundCallback.onError(error);
        } else {
          performErrorCallback(callback, error);
        }
      }
    });
  }

}
