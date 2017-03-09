package com.worldreader.core.domain.interactors.user.userbooks;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.domain.model.user.UserBook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.*;

@Singleton public class GetCollectionIdMapOfUserBooksInteractor {

  public static final String NONE_COLLECTION_ID = String.valueOf(0);

  private final ListeningExecutorService executorService;

  @Inject
  public GetCollectionIdMapOfUserBooksInteractor(final ListeningExecutorService executorService) {
    this.executorService = executorService;
  }

  public ListenableFuture<Map<String, Set<String>>> execute(final List<UserBook> userBooks,
      final Executor executor) {
    final SettableFuture<Map<String, Set<String>>> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final Map<String, Set<String>> map = new HashMap<>();

        for (final UserBook userBook : userBooks) {
          final List<String> collectionIds = userBook.getCollectionIds();
          if (collectionIds != null && collectionIds.size() > 0) {
            for (final String collectionId : collectionIds) {
              Set<String> bookIds = map.get(collectionId);

              if (bookIds == null) {
                bookIds = new HashSet<>();
              }

              bookIds.add(userBook.getBookId());
              map.put(collectionId, bookIds);
            }

          } else {
            Set<String> noneCollection = map.get(NONE_COLLECTION_ID);
            if (noneCollection == null) {
              noneCollection = new HashSet<>();
            }

            noneCollection.add(userBook.getBookId());
            map.put(NONE_COLLECTION_ID, noneCollection);
          }

          settableFuture.set(map);
        }

      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  public ListenableFuture<Map<String, Set<String>>> execute(final List<UserBook> userBooks) {
    return execute(userBooks, executorService);
  }

}
