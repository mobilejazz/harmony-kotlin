package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.domain.model.Banner;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.repository.BannerRepository;

import javax.inject.Inject;
import java.util.*;

@PerActivity public class GetBooksRelatedWithBannerInteractor {

  private final ListeningExecutorService executorService;
  private final BannerRepository bannerRepository;

  @Inject public GetBooksRelatedWithBannerInteractor(ListeningExecutorService executorService,
      BannerRepository bannerRepository) {
    this.executorService = executorService;
    this.bannerRepository = bannerRepository;
  }

  public ListenableFuture<Optional<List<Book>>> execute(final Banner banner) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    executorService.execute(new Runnable() {
      @Override public void run() {
        bannerRepository.get(banner.getId(), banner.getType(), new Callback<Banner>() {
          @Override public void onSuccess(Banner banner) {
            List<Book> books = banner.getBooks();

            Optional<List<Book>> optional =
                books != null ? Optional.of(books) : Optional.<List<Book>>absent();

            settableFuture.set(optional);
          }

          @Override public void onError(Throwable e) {
            settableFuture.setException(e);
          }
        });
      }
    });

    return settableFuture;
  }
}
