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
import com.worldreader.core.domain.model.BookImageQuality;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.core.domain.thread.MainThread;
import com.worldreader.core.error.book.FailedDownloadBookException;

import javax.inject.Inject;
import java.net.UnknownHostException;
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

  @Inject
  public DownloadBookInteractorImpl(Logger logger, InteractorExecutor executor, MainThread mainThread, GetBookMetadataInteractor getBookMetadataInteractor,
      StreamingBookRepository streamingBookRepository) {
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

  @Override public ListenableFuture<Void> execute(final String bookId, final String version,
      final BookImageQuality bookImageQuality) {
    return this.execute(bookId, version, bookImageQuality, executor.getExecutor());
  }

  @Override public ListenableFuture<Void> execute(final String bookId, final String version, final BookImageQuality bookImageQuality, Executor executor) {
    this.bookId = bookId;
    this.version = version;
    this.forceBookMetadataRefresh = false;

    final SettableFuture<Void> settableFuture = SettableFuture.create();
    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final BookMetadata bookMetadata = getBookMetadataInteractor.execute(bookId, version, false, MoreExecutors.directExecutor()).get();
        downloadBookResources(bookMetadata, false, bookImageQuality);
        settableFuture.set(null);
      }

      @Override protected void onExceptionThrown(Throwable t) {
        settableFuture.setException(t);
      }
    });
    return settableFuture;
  }

  //@Override public void execute(String bookId, DomainCallback<Integer, ErrorCore<?>> callback) {
  //  this.bookId = bookId;
  //  this.version = "latest";
  //  this.forceBookMetadataRefresh = false;
  //  this.callback = callback;
  //  this.executor.run(this);
  //}

  @Override public void execute(String bookId, String version, boolean forceBookMetadataRefresh, DomainBackgroundCallback<Void, ErrorCore<?>>
      backgroundCallback) {
    this.bookId = bookId;
    this.version = version;
    this.forceBookMetadataRefresh = forceBookMetadataRefresh;
    this.backgroundCallback = backgroundCallback;
    this.executor.run(this);
  }

  @Override public void run() {
    getBookMetadataInteractor.execute(bookId, version, forceBookMetadataRefresh, new DomainBackgroundCallback<BookMetadata, ErrorCore<?>>() {
      @Override public void onSuccess(BookMetadata bookMetadata) {
        try {
          boolean shouldNotify = backgroundCallback == null;
          downloadBookResources(bookMetadata, shouldNotify, BookImageQuality.NETWORK_QUALITY_DEPENDANT);
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

  private void downloadBookResources(BookMetadata bookMetadata, boolean shouldNotifyResponses, BookImageQuality bookImageQuality) throws Throwable {
    if (bookMetadata != null && bookMetadata.resources != null) {
      final List<String> resources = bookMetadata.resources;

      int numberOfResources = resources.size() + 2/*content opf && toc*/;
      int count = 0;

      streamingBookRepository.getBookResource(bookId, bookMetadata, bookMetadata.contentOpfName);
      shouldNotifySuccessfulResponse(shouldNotifyResponses, count++, numberOfResources);
      streamingBookRepository.getBookResource(bookId, bookMetadata, bookMetadata.tocResourceName);
      shouldNotifySuccessfulResponse(shouldNotifyResponses, count++, numberOfResources);

      for (int position = 0; position < resources.size(); position++) {
        String resource = resources.get(position);
        logger.d(TAG, "Downloading current resource: " + resource);
        try {
          streamingBookRepository.getBookResource(bookId, bookMetadata, bookMetadata.contentOpfPath + resource, bookImageQuality);
        } catch (Exception e) {
          if (e instanceof UnknownHostException) {
            // If we have problems with the network we rethrow the exception (causing the download to stop)
            throw e;
          } else {
            // We want to continue the download even if some resources are not present on the server
            continue;
          }
        }

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
