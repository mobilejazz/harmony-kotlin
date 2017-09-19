package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.core.domain.thread.MainThread;
import com.worldreader.core.error.book.FailedDownloadBookException;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class DownloadBookInteractorImpl extends AbstractInteractor<Integer, ErrorCore<?>> implements DownloadBookInteractor {

  public static final String TAG = DownloadBookInteractor.class.getSimpleName();

  private final Logger logger;
  private final GetBookMetadataInteractor getBookMetadataInteractor;
  private final StreamingBookRepository streamingBookRepository;

  private String bookId;
  private String version;
  private boolean forceBookMetadataRefresh;
  private DomainCallback<Integer, ErrorCore<?>> callback;
  private DomainBackgroundCallback<Void, ErrorCore<?>> backgroundCallback;

  @Inject public DownloadBookInteractorImpl(Logger logger, InteractorExecutor executor, MainThread mainThread,
      GetBookMetadataInteractor getBookMetadataInteractor, StreamingBookRepository streamingBookRepository) {
    super(executor, mainThread);
    this.logger = logger;
    this.getBookMetadataInteractor = getBookMetadataInteractor;
    this.streamingBookRepository = streamingBookRepository;
  }

  @Override public void execute(final String bookId, final String version, final DomainCallback<Integer, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.version = version;
    this.forceBookMetadataRefresh = false;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void execute(String bookId, DomainCallback<Integer, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.version = "latest";
    this.forceBookMetadataRefresh = false;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void execute(String bookId, boolean forceBookMetadataRefresh, DomainBackgroundCallback<Void, ErrorCore<?>> backgroundCallback) {
    this.bookId = bookId;
    this.forceBookMetadataRefresh = forceBookMetadataRefresh;
    this.backgroundCallback = backgroundCallback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Void> execute(final String bookId, final String version, Executor executor) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();
    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final BookMetadata bookMetadata = getBookMetadataInteractor.execute(bookId, version, false, MoreExecutors.directExecutor()).get();
        downloadBookResources(bookMetadata, false);
        settableFuture.set(null);
      }

      @Override protected void onExceptionThrown(Throwable t) {
        settableFuture.setException(t);
      }
    });
    return settableFuture;
  }

  @Override public void run() {
    getBookMetadataInteractor.execute(bookId, version, forceBookMetadataRefresh, new DomainBackgroundCallback<BookMetadata, ErrorCore<?>>() {
      @Override public void onSuccess(BookMetadata bookMetadata) {
        try {
          boolean shouldNotify = backgroundCallback == null;
          downloadBookResources(bookMetadata, shouldNotify);
        } catch (Throwable throwable) {
          ErrorCore errorCore = ErrorCore.of(new FailedDownloadBookException());
          notifyErrorResponse(errorCore);
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        notifyErrorResponse(errorCore);
      }
    });
  }

  private void downloadBookResources(BookMetadata bookMetadata, boolean shouldNotifyResponses) throws Throwable {
    if (bookMetadata != null && bookMetadata.getResources() != null) {
      List<String> resources = bookMetadata.getResources();

      int numberOfResources = resources.size() + 2/*content opf && toc*/;
      int count = 0;

      streamingBookRepository.getBookResource(bookId, bookMetadata, bookMetadata.getContentOpfName());

      shouldNotifySuccessfulResponse(shouldNotifyResponses, count++, numberOfResources);

      streamingBookRepository.getBookResource(bookId, bookMetadata, bookMetadata.getTocResource());

      shouldNotifySuccessfulResponse(shouldNotifyResponses, count++, numberOfResources);

      for (int position = 0; position < resources.size(); position++) {
        String resource = resources.get(position);

        logger.d(TAG, "Downloading current resource: " + resource);

        streamingBookRepository.getBookResource(bookId, bookMetadata, resource);

        shouldNotifySuccessfulResponse(shouldNotifyResponses, position + count, numberOfResources);
      }

      shouldNotifySuccessfulResponse(shouldNotifyResponses, numberOfResources, numberOfResources);
    } else {
      notifyErrorResponse(ErrorCore.EMPTY);
    }
  }

  private int calculatePercentage(int count, int total) {
    return (int) ((float) count / total * 100.0);
  }

  private void shouldNotifySuccessfulResponse(boolean shouldNotifyResponses, int count, int numberOfResources) {
    final int percentage = calculatePercentage(count, numberOfResources);
    if (shouldNotifyResponses && callback != null) {
      performSuccessCallback(callback, percentage);
    } else {
      if (percentage == 100 && backgroundCallback != null) {
        backgroundCallback.onSuccess(null);
      } else if (percentage == 100 && callback != null) {
        performSuccessCallback(callback, 100);
      }
    }
  }

  private void notifyErrorResponse(ErrorCore errorCore) {
    if (backgroundCallback == null) {
      performErrorCallback(callback, errorCore);
    } else {
      backgroundCallback.onError(errorCore);
    }
  }
}
