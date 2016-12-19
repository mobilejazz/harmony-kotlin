package com.worldreader.core.datasource;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.helper.url.URLProvider;
import com.worldreader.core.datasource.mapper.CollectionEntityDataMapper;
import com.worldreader.core.datasource.model.CollectionEntity;
import com.worldreader.core.datasource.network.datasource.collection.CollectionNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.collection.CollectionNetworkDataSourceImp;
import com.worldreader.core.datasource.storage.datasource.collection.CollectionBdDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.model.Collection;
import com.worldreader.core.domain.repository.CollectionRepository;

import javax.inject.Inject;
import java.util.*;

public class CollectionDataSource implements CollectionRepository {

  private final CollectionNetworkDataSource networkDataSource;
  private final CollectionBdDataSource bdDataSource;
  private final CollectionEntityDataMapper entityDataMapper;
  private final CountryCodeProvider countryCodeProvider;
  private final Provider<List<BookDownloaded>> bookDownloadedProvider;

  @Inject public CollectionDataSource(CollectionNetworkDataSource networkDataSource,
      CollectionBdDataSource bdDataSource, CollectionEntityDataMapper entityDataMapper,
      CountryCodeProvider countryCodeProvider,
      Provider<List<BookDownloaded>> bookDownloadedProvider) {
    this.networkDataSource = networkDataSource;
    this.bdDataSource = bdDataSource;
    this.entityDataMapper = entityDataMapper;
    this.countryCodeProvider = countryCodeProvider;
    this.bookDownloadedProvider = bookDownloadedProvider;
  }

  @Override public void collections(final CompletionCallback<List<Collection>> callback) {
    final String key = URLProvider.withEndpoint(CollectionNetworkDataSourceImp.ENDPOINT)
        .addCountryCode(countryCodeProvider.getCountryCode())
        .build();

    try {
      List<CollectionEntity> collectionEntities = bdDataSource.obtains(key);
      List<Collection> collections = entityDataMapper.transform(collectionEntities);

      // Set the book downloaded attribute
      performSetBookDownloadedPromise(collections);

      responseCollectionsLoaded(callback, collections);
    } catch (InvalidCacheException invalid) {
      networkDataSource.fetchCollections(new CompletionCallback<List<CollectionEntity>>() {
        @Override public void onSuccess(List<CollectionEntity> collectionEntities) {
          bdDataSource.persist(key, collectionEntities);

          List<Collection> collections = entityDataMapper.transform(collectionEntities);

          // Set the book downloaded attribute
          performSetBookDownloadedPromise(collections);

          responseCollectionsLoaded(callback, collections);
        }

        @Override public void onError(ErrorCore errorCore) {
          if (callback != null) {
            callback.onError(errorCore);
          }
        }
      });
    }
  }

  private void performSetBookDownloadedPromise(List<Collection> collections) {
    for (Collection collection : collections) {
      promiseSetBooksDownloaded(collection.getBooks());
    }
  }

  @Override
  public void collection(int collectionId, final CompletionCallback<Collection> callback) {
    final String key = URLProvider.withEndpoint(CollectionNetworkDataSourceImp.ENDPOINT)
        .addId(collectionId)
        .addCountryCode(countryCodeProvider.getCountryCode())
        .build();

    try {
      CollectionEntity collectionEntity = bdDataSource.obtain(key);
      Collection collection = entityDataMapper.transform(collectionEntity);

      // Set the book downloaded attribute
      performSetBookDownloadedPromise(collection);

      responseCollectionLoaded(callback, collection);
    } catch (InvalidCacheException invalid) {
      networkDataSource.fetchCollection(collectionId, new CompletionCallback<CollectionEntity>() {
        @Override public void onSuccess(CollectionEntity collectionEntity) {
          bdDataSource.persist(key, collectionEntity);
          Collection collection = entityDataMapper.transform(collectionEntity);

          // Set the book downloaded attribute
          performSetBookDownloadedPromise(collection);

          responseCollectionLoaded(callback, collection);
        }

        @Override public void onError(ErrorCore errorCore) {
          if (callback != null) {
            callback.onError(errorCore);
          }
        }
      });
    }
  }

  private void performSetBookDownloadedPromise(Collection collection) {
    performSetBookDownloadedPromise(Collections.singletonList(collection));
  }

  //region Private methods
  private void responseCollectionLoaded(CompletionCallback<Collection> callback,
      Collection collection) {
    if (callback != null) {
      callback.onSuccess(collection);
    }
  }

  private void responseCollectionsLoaded(CompletionCallback<List<Collection>> callback,
      List<Collection> collections) {
    if (callback != null) {
      // Convert to Set to remove duplicates
      Set<Collection> collectionSet = new HashSet<>(collections);
      final ArrayList<Collection> clearCollections = new ArrayList<>(collectionSet);
      callback.onSuccess(clearCollections);
    }
  }

  private void promiseSetBooksDownloaded(List<Book> books) {
    for (Book book : books) {
      promiseSetBookDownloaded(book);
    }
  }

  private void promiseSetBookDownloaded(Book book) {
    for (BookDownloaded bookDownloaded : bookDownloadedProvider.get()) {
      if (book.getId().equals(bookDownloaded.getBookId())) {
        if (book.getId().equals(bookDownloaded.getBookId())) {
          book.setBookDownloaded(true);
          break;
        }
      }
    }
  }
  //endregion
}
