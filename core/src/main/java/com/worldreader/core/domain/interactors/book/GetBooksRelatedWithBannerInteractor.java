package com.worldreader.core.domain.interactors.book;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.Banner;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.repository.BannerRepository;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

@PerActivity public class GetBooksRelatedWithBannerInteractor {

  private final ListeningExecutorService executorService;
  private final BannerRepository bannerRepository;
  private final MergeBooksDownloadInformationInteractor mergeBooksDownloadInformationInteractor;

  @Inject public GetBooksRelatedWithBannerInteractor(ListeningExecutorService executorService,
      BannerRepository bannerRepository,
      final MergeBooksDownloadInformationInteractor mergeBooksDownloadInformationInteractor) {
    this.executorService = executorService;
    this.bannerRepository = bannerRepository;
    this.mergeBooksDownloadInformationInteractor = mergeBooksDownloadInformationInteractor;
  }

  public ListenableFuture<Optional<List<Book>>> execute(final Banner banner) {
    final SettableFuture<Optional<List<Book>>> settableFuture = SettableFuture.create();

    executorService.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        bannerRepository.get(banner.getId(), banner.getType(), new Callback<Banner>() {
          @Override public void onSuccess(Banner banner) {
            List<Book> books = banner.getBooks();

            final ListenableFuture<Optional<List<Book>>> mergedBooksDownloadedLf =
                mergeBooksDownloadInformationInteractor.execute(Optional.fromNullable(books),
                    MoreExecutors.directExecutor());
            try {
              final Optional<List<Book>> bookDownloadedMergeOp = mergedBooksDownloadedLf.get();

              if (bookDownloadedMergeOp.isPresent()) {
                settableFuture.set(bookDownloadedMergeOp);
              } else {
                settableFuture.set(Optional.fromNullable(books));
              }

            } catch (InterruptedException | ExecutionException e) {
              settableFuture.setException(e);
            }
          }

          @Override public void onError(Throwable e) {
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
}
