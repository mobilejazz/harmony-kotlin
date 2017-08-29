package com.worldreader.core.domain.interactors.collection;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.Interactor;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.Collection;
import com.worldreader.core.domain.repository.CollectionRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class GetCollectionInteractorImp implements GetCollectionInteractor, Interactor {

  private static final String TAG = GetCollectionInteractorImp.class.getSimpleName();

  private final InteractorExecutor executor;
  private final ListeningExecutorService executorService;
  private final MainThread mainThread;
  private final CollectionRepository repository;

  private CompletionCallback<Collection> collectionCallback;
  private int collectionId = -1;

  private CompletionCallback<List<Collection>> collectionsCallback;
  private List<Integer> collectionsId;

  private List<Collection> responseCollections;

  @Inject public GetCollectionInteractorImp(InteractorExecutor executor,
      final ListeningExecutorService executorService, MainThread mainThread,
      CollectionRepository repository) {
    this.executor = executor;
    this.executorService = executorService;
    this.mainThread = mainThread;
    this.repository = repository;
  }

  @Override public void execute(int collectionId, CompletionCallback<Collection> callback) {
    this.collectionId = collectionId;
    this.collectionCallback = callback;
    this.executor.run(this);
  }

  @Override
  public void execute(List<Integer> collectionsId, CompletionCallback<List<Collection>> callback) {
    this.collectionsCallback = callback;
    this.collectionsId = collectionsId;
    this.executor.run(this);
  }

  @Override public ListenableFuture<Collection> execute(final int collectionId) {
    final SettableFuture<Collection> settableFuture = SettableFuture.create();

    executorService.execute(new Runnable() {
      @Override public void run() {
        //Execute a simple request to one collection
        fetchCollection(collectionId, new CompletionCallback<Collection>() {
          @Override public void onSuccess(final Collection result) {
            settableFuture.set(result);
          }

          @Override public void onError(final ErrorCore error) {
            settableFuture.setException(error.getCause());
          }
        });
      }
    });

    return settableFuture;

  }

  @Override public void run() {
    responseCollections = new ArrayList<>();
    if (collectionsId != null && collectionsId.size() > 0) {

      //Execute a pool of operations
      for (int index = 0; index < collectionsId.size(); index++) {
        final Integer id = collectionsId.get(index);
        fetchCollection(id, new CompletionCallback<Collection>() {
          @Override public void onSuccess(Collection result) {
            //Adding the responses in the array with all the responses
            responseCollections.add(result);

            //Check if the queue of the operations is finished
            if (collectionsId.size() == responseCollections.size()) {
              mainThread.post(new Runnable() {
                @Override public void run() {
                  if (collectionsCallback != null) {
                    collectionsCallback.onSuccess(responseCollections);
                  }
                }
              });
            }
          }

          @Override public void onError(final ErrorCore error) {
            mainThread.post(new Runnable() {
              @Override public void run() {
                collectionsCallback.onError(error);
              }
            });
          }
        });
      }
    } else {
      //Execute a simple request to one collection
      fetchCollection(collectionId, new CompletionCallback<Collection>() {
        @Override public void onSuccess(final Collection result) {
          mainThread.post(new Runnable() {
            @Override public void run() {
              if (collectionCallback != null) {
                collectionCallback.onSuccess(result);
              }
            }
          });
        }

        @Override public void onError(final ErrorCore error) {
          mainThread.post(new Runnable() {
            @Override public void run() {
              if (collectionCallback != null) {
                collectionCallback.onError(error);
              }
            }
          });
        }
      });
    }
  }

  private void fetchCollection(int collectionId, final CompletionCallback<Collection> callback) {
    repository.collection(collectionId, new CompletionCallback<Collection>() {
      @Override public void onSuccess(Collection collection) {
        if (callback != null) {
          callback.onSuccess(collection);
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        if (callback != null) {
          callback.onError(errorCore);
        }
      }
    });
  }
}
