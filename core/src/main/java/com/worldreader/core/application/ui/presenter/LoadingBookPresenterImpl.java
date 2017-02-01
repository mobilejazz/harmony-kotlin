package com.worldreader.core.application.ui.presenter;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.interactors.reader.GetBookMetadataInteractor;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.Collection;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class LoadingBookPresenterImpl implements LoadingBookPresenter {

  private final GetBookMetadataInteractor getBookMetadataInteractor;
  private final MainThread mainThread;

  private View view;

  @Inject public LoadingBookPresenterImpl(GetBookMetadataInteractor getBookMetadataInteractor,
      MainThread mainThread) {
    this.getBookMetadataInteractor = getBookMetadataInteractor;
    this.mainThread = mainThread;
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
    getBookMetadataInteractor.execute(book.getId(),
        new DomainCallback<BookMetadata, ErrorCore<?>>(mainThread) {
          @Override public void onSuccessResult(BookMetadata bookMetadata) {
            int collectionId = collection == null ? 0 : collection.getId();
            bookMetadata.setCollectionId(collectionId);
            bookMetadata.setTitle(book.getTitle());

            view.onNotifyDisplayReader(bookMetadata);
          }

          @Override public void onErrorResult(ErrorCore errorCore) {
            view.showError(errorCore);
          }
        });
  }
}
