package com.worldreader.core.application.ui.presenter;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.Collection;

public interface LoadingBookPresenter {

  void initialize(Book book, Collection collection);

  void initialize();

  void onResume();

  void onPause();

  void onDestroy();

  void attachView(LoadingBookPresenter.View view);

  interface View {

    void showError(ErrorCore errorCore);

    void showProgressView();

    void hideProgressView();

    void onNotifyDisplayReader(Book book, BookMetadata bookMetadata);
  }
}
