package com.worldreader.core.application.ui.presenter;

import android.support.annotation.NonNull;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.application.helper.InteractorHandler;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.interactors.reader.GetBookMetadataInteractor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.Collection;

import javax.inject.Inject;

public class LoadingBookPresenterImpl implements LoadingBookPresenter {

  private final GetBookMetadataInteractor getBookMetadataInteractor;
  private final InteractorHandler interactorHandler;

  private View view;

  @Inject public LoadingBookPresenterImpl(GetBookMetadataInteractor getBookMetadataInteractor, InteractorHandler interactorHandler) {
    this.getBookMetadataInteractor = getBookMetadataInteractor;
    this.interactorHandler = interactorHandler;
  }

  @Override public void initialize() {
    // Nothing to do
  }

  @Override public void onResume() {
    // Nothing to do
  }

  @Override public void onPause() {
    // Nothing to do
  }

  @Override public void onDestroy() {
    // Nothing to do
  }

  @Override public void attachView(View view) {
    this.view = view;
  }

  @Override public void initialize(final Book book, final Collection collection) {
    final ListenableFuture<BookMetadata> getBookMetadataFuture = getBookMetadataInteractor.execute(book.getId(), book.getVersion());
    interactorHandler.addCallbackMainThread(getBookMetadataFuture, new FutureCallback<BookMetadata>() {
      @Override public void onSuccess(@NonNull BookMetadata bookMetadata) {
        bookMetadata.title = book.getTitle();
        bookMetadata.author = book.getAuthor();
        bookMetadata.collectionId = collection == null ? 0 : collection.getId();
        bookMetadata.mode = BookMetadata.STREAMING_MODE;

        view.onNotifyDisplayReader(bookMetadata);
      }

      @Override public void onFailure(@NonNull Throwable t) {
        view.showError(ErrorCore.of(t));
      }
    });
  }
}
