package com.worldreader.core.domain.interactors.reader;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.di.qualifiers.RemoveBookDownloaded;
import com.worldreader.core.application.helper.image.ImageDownloader;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainBackgroundCallback;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class DeleteBookDownloadedInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>> implements DeleteBookDownloadedInteractor {

  private static final String TAG = DeleteBookDownloadedInteractor.class.getSimpleName();

  private final Action<BookDownloaded, Boolean> deleteBookDownloadedAction;
  private final GetBookMetadataInteractor getBookMetadataInteractor;
  private final StreamingBookRepository streamingBookRepository;
  private final ImageDownloader imageDownloader;
  private final Logger logger;

  private String bookId;
  private String version;
  private DomainCallback<Boolean, ErrorCore<?>> callback;
  private DomainBackgroundCallback<Boolean, ErrorCore<?>> backgroundCallback;

  @Inject public DeleteBookDownloadedInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      @RemoveBookDownloaded final Action<BookDownloaded, Boolean> deleteBookDownloadedAction, GetBookMetadataInteractor getBookMetadataInteractor,
      StreamingBookRepository streamingBookRepository, ImageDownloader imageDownloader, Logger logger) {
    super(executor, mainThread);
    this.deleteBookDownloadedAction = deleteBookDownloadedAction;
    this.getBookMetadataInteractor = getBookMetadataInteractor;
    this.streamingBookRepository = streamingBookRepository;
    this.imageDownloader = imageDownloader;
    this.logger = logger;
  }

  @Override public void execute(final String bookId, final String version, final DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.version = version;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void execute(final String bookId, final String version, final DomainBackgroundCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.version = version;
    this.backgroundCallback = callback;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Void> execute(final String bookId, final String version) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    getExecutor().execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        execute(bookId, version, new Callback<Boolean>() {
          @Override public void onSuccess(final Boolean aBoolean) {
            settableFuture.set(null);
          }

          @Override public void onError(final Throwable e) {
            settableFuture.setException(e);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  @Override public void run() {
    execute(this.bookId, this.version, new Callback<Boolean>() {
      @Override public void onSuccess(final Boolean result) {
        if (backgroundCallback == null) {
          performSuccessCallback(callback, result);
        } else {
          backgroundCallback.onSuccess(result);
        }
      }

      @Override public void onError(final Throwable e) {
        if (backgroundCallback == null) {
          performErrorCallback(callback, ErrorCore.of(e));
        } else {
          backgroundCallback.onError(ErrorCore.of(e));
        }
      }
    });
  }

  //region Private methods

  private void execute(final String bookId, final String version, final Callback<Boolean> callback) {
    getBookMetadataInteractor.execute(bookId, version, new DomainBackgroundCallback<BookMetadata, ErrorCore<?>>() {
      @Override public void onSuccess(BookMetadata bookMetadata) {
        logger.d(TAG, "Deleting downloaded book with id: " + bookId);

        if (bookMetadata != null && bookMetadata.resources != null) {
          performDeleteAllResources(bookMetadata);
          performDeleteBookIdFromBookDownloadedList(bookId, callback);
          imageDownloader.delete(bookId);
        } else {
          if (callback != null) {
            callback.onError(ErrorCore.EMPTY.getCause());
          }
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        if (callback != null) {
          callback.onError(errorCore.getCause());
        }
      }
    });

  }

  private void performDeleteBookIdFromBookDownloadedList(final String bookId, Callback<Boolean> callback) {
    BookDownloaded bookDownloaded = BookDownloaded.create(bookId, new Date());

    boolean isDeleted = deleteBookDownloadedAction.perform(bookDownloaded);

    if (callback != null) {
      callback.onSuccess(isDeleted);
    }
  }

  private void performDeleteAllResources(BookMetadata bookMetadata) {
    final List<String> resources = bookMetadata.resources;

    streamingBookRepository.deleteBookResource(bookMetadata.bookId, bookMetadata.contentOpfName);
    streamingBookRepository.deleteBookResource(bookMetadata.bookId, bookMetadata.tocResourceName);

    for (String resource : resources) {
      logger.d(TAG, "Deleting current resource: " + resource);

      String resourceToDownload;
      if (resource.contains("png") || resource.contains("jpg") || resource.contains("jpeg")) {
        resourceToDownload = resource + "?size=480x800";
      } else {
        resourceToDownload = resource;
      }

      streamingBookRepository.deleteBookResource(bookMetadata.bookId, resourceToDownload);
    }
  }
  //endregion
}
