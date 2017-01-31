package com.worldreader.core.domain.interactors.reader;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.di.qualifiers.RemoveBookDownloaded;
import com.worldreader.core.application.helper.image.ImageDownloader;
import com.worldreader.core.common.deprecated.error.ErrorCore;
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

public class DeleteBookDownloadedInteractorImpl extends AbstractInteractor<Boolean, ErrorCore<?>>
    implements DeleteBookDownloadedInteractor {

  private static final String TAG = DeleteBookDownloadedInteractor.class.getSimpleName();

  private final Action<BookDownloaded> deleteBookDownloadedAction;
  private final GetBookMetadataInteractor getBookMetadataInteractor;
  private final StreamingBookRepository streamingBookRepository;
  private final ImageDownloader imageDownloader;
  private final Logger logger;

  private String bookId;
  private DomainCallback<Boolean, ErrorCore<?>> callback;
  private DomainBackgroundCallback<Boolean, ErrorCore<?>> backgroundCallback;

  @Inject
  public DeleteBookDownloadedInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      @RemoveBookDownloaded final Action<BookDownloaded> deleteBookDownloadedAction,
      GetBookMetadataInteractor getBookMetadataInteractor,
      StreamingBookRepository streamingBookRepository, ImageDownloader imageDownloader,
      Logger logger) {
    super(executor, mainThread);
    this.deleteBookDownloadedAction = deleteBookDownloadedAction;
    this.getBookMetadataInteractor = getBookMetadataInteractor;
    this.streamingBookRepository = streamingBookRepository;
    this.imageDownloader = imageDownloader;
    this.logger = logger;
  }

  @Override public void execute(String bookId, DomainCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override
  public void execute(String bookId, DomainBackgroundCallback<Boolean, ErrorCore<?>> callback) {
    this.bookId = bookId;
    this.backgroundCallback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    getBookMetadataInteractor.execute(bookId,
        new DomainBackgroundCallback<BookMetadata, ErrorCore<?>>() {
          @Override public void onSuccess(BookMetadata bookMetadata) {
            logger.d(TAG, "Deleting downloaded book with id: " + bookId);

            if (bookMetadata != null && bookMetadata.getResources() != null) {
              performDeleteAllResources(bookMetadata);
              performDeleteBookIdFromBookDownloadedList(bookId);
              imageDownloader.delete(bookId);
            } else {
              performErrorCallback(callback, ErrorCore.EMPTY);
            }
          }

          @Override public void onError(ErrorCore errorCore) {
            if (backgroundCallback == null) {
              performErrorCallback(callback, errorCore);
            } else {
              backgroundCallback.onError(errorCore);
            }
          }
        });
  }

  private void performDeleteBookIdFromBookDownloadedList(String bookId) {
    BookDownloaded bookDownloaded = BookDownloaded.create(bookId, new Date());

    boolean isDeleted = deleteBookDownloadedAction.perform(bookDownloaded);

    if (backgroundCallback == null) {
      performSuccessCallback(callback, isDeleted);
    } else {
      backgroundCallback.onSuccess(isDeleted);
    }
  }

  private void performDeleteAllResources(BookMetadata bookMetadata) {
    List<String> resources = bookMetadata.getResources();

    streamingBookRepository.deleteBookResource(bookId, bookMetadata.getContentOpfName());
    streamingBookRepository.deleteBookResource(bookId, bookMetadata.getTocResource());

    for (String resource : resources) {
      logger.d(TAG, "Deleting current resource: " + resource);

      String resourceToDownload;
      if (resource.contains("png") || resource.contains("jpg") || resource.contains("jpeg")) {
        resourceToDownload = resource + "?size=480x800";
      } else {
        resourceToDownload = resource;
      }

      streamingBookRepository.deleteBookResource(bookId, resourceToDownload);
    }
  }
}
