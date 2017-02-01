package com.worldreader.core.datasource;

import android.text.TextUtils;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.helper.Provider;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.helper.url.URLProvider;
import com.worldreader.core.datasource.mapper.BookEntityDataMapper;
import com.worldreader.core.datasource.model.BookEntity;
import com.worldreader.core.datasource.network.datasource.book.BookNetworkDataSource;
import com.worldreader.core.datasource.network.datasource.book.BookNetworkDataSourceImp;
import com.worldreader.core.datasource.storage.datasource.book.BookBdDataSource;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookDownloaded;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.repository.BookRepository;

import javax.inject.Inject;
import java.util.*;

public class BookDataSource implements BookRepository {

  private final BookNetworkDataSource networkDataSource;
  private final BookBdDataSource bddDataSource;
  private final BookEntityDataMapper entityDataMapper;
  private final CountryCodeProvider countryCodeProvider;
  private final Provider<List<BookDownloaded>> booksDownloadedProvider;

  @Inject
  public BookDataSource(BookNetworkDataSource networkDataSource, BookBdDataSource bddDataSource,
      BookEntityDataMapper entityDataMapper, CountryCodeProvider countryCodeProvider,
      Provider<List<BookDownloaded>> booksDownloadedProvider) {
    this.networkDataSource = networkDataSource;
    this.bddDataSource = bddDataSource;
    this.entityDataMapper = entityDataMapper;
    this.countryCodeProvider = countryCodeProvider;
    this.booksDownloadedProvider = booksDownloadedProvider;
  }

  @Override public void books(List<Integer> categoriesId, String list, List<BookSort> sorters,
      boolean openCountry, String language, int index, int limit,
      CompletionCallback<List<Book>> callback) {
    String countryIsoCode = countryCodeProvider.getCountryCode();

    String key = URLProvider.withEndpoint(BookNetworkDataSourceImp.ENDPOINT)
        .addIndex(index)
        .addLimit(limit)
        .addCategories(categoriesId)
        .addList(list)
        .addSorters(sorters)
        //.addOpenCountry(openCountry ? countryIsoCode : null)
        .addCountryCode(countryIsoCode)
        .addLaguageQuery(language)
        .build();

    try {
      List<BookEntity> obtains = bddDataSource.obtains(key);
      List<Book> response = transform(obtains);

      //Set if the books are downloaded attribute
      promiseSetBooksDownloaded(response);

      notifyResponse(response, callback);
    } catch (InvalidCacheException e) {
      fetchBooks(key, index, limit, sorters, list, categoriesId, /*open country*/ false, language,
          callback);
    }
  }

  @Override public void search(final int index, final int limit, final List<Integer> categories,
      final String title, final String author, final String publisher,
      final Callback<List<Book>> callback) {
    String countryCode = countryCodeProvider.getCountryCode();

    final String key = URLProvider.withEndpoint(BookNetworkDataSourceImp.ENDPOINT)
        .addIndex(index)
        .addLimit(limit)
        .addCountryCode(countryCode)
        .addAuthor(author)
        .addTitle(title)
        .addCategories(categories)
        .build();

    try {
      List<BookEntity> bookEntitiesFromCache = bddDataSource.obtains(key);
      List<Book> booksFromCache = transform(bookEntitiesFromCache);

      //Set if the books are downloaded attribute
      promiseSetBooksDownloaded(booksFromCache);

      if (callback != null) {
        callback.onSuccess(booksFromCache);
      }
    } catch (InvalidCacheException e) {
      if (TextUtils.isEmpty(title) && TextUtils.isEmpty(author)) {
        throw new IllegalArgumentException("Title and Author must be not null");
      }

      networkDataSource.search(index, limit, categories, title, author,
          new Callback<List<BookEntity>>() {
            @Override public void onSuccess(List<BookEntity> bookEntitiesFromNetwork) {
              bddDataSource.persist(key, bookEntitiesFromNetwork);
              List<Book> booksFromNetwork = transform(bookEntitiesFromNetwork);

              //Set if the books are downloaded attribute
              promiseSetBooksDownloaded(booksFromNetwork);

              if (callback != null) {
                callback.onSuccess(booksFromNetwork);
              }
            }

            @Override public void onError(Throwable e) {
              if (callback != null) {
                callback.onError(e);
              }
            }
          });
    }
  }

  @Override public void bookDetail(String bookId, String version, boolean forceUpdate,
      CompletionCallback<Book> callback) {
    String countryIsoCode = countryCodeProvider.getCountryCode();

    String key = URLProvider.withEndpoint(BookNetworkDataSourceImp.ENDPOINT)
        .addId(bookId)
        .addVersion(version)
        .build();

    if (forceUpdate) {
      bddDataSource.delete(key);
      fetchBookDetail(key, bookId, version, callback);
      return;
    }

    try {
      BookEntity cached = bddDataSource.obtain(key);
      Book response = transform(cached);

      //Set if the books are downloaded attribute
      promiseSetBookDownloaded(response);

      notifyResponse(response, callback);
    } catch (InvalidCacheException e) {
      fetchBookDetail(key, bookId, version, callback);
    }
  }

  @Override public void bookDetailLatest(String bookId, boolean forceUpdate,
      CompletionCallback<Book> callback) {
    bookDetail(bookId, BookRepository.KEY_LATEST, forceUpdate, callback);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void fetchBooks(final String key, int index, int limit, List<BookSort> sorters,
      String list, List<Integer> categories, boolean openCountryCode, String language,
      final CompletionCallback callback) {
    networkDataSource.books(index, limit, sorters, list, categories, openCountryCode, language,
        new CompletionCallback<List<BookEntity>>() {
          @Override public void onSuccess(List<BookEntity> result) {
            bddDataSource.persist(key, result);
            List<Book> books = transform(result);

            // Set if the books are downloaded
            promiseSetBooksDownloaded(books);

            performResponse(books, callback);
          }

          @Override public void onError(ErrorCore error) {
            if (callback != null) {
              callback.onError(error);
            }
          }
        });
  }

  private void promiseSetBookDownloaded(Book book) {
    promiseSetBooksDownloaded(Collections.singletonList(book));
  }

  private void promiseSetBooksDownloaded(List<Book> books) {
    final List<BookDownloaded> collectionBookDownloaded = booksDownloadedProvider.get();

    for (Book book : books) {
      promiseSetBookDownloaded(book, collectionBookDownloaded);
    }
  }

  private void promiseSetBookDownloaded(final Book book,
      final List<BookDownloaded> collectionBookDownloaded) {
    for (BookDownloaded bookDownloaded : collectionBookDownloaded) {
      if (book != null) {
        if (book.getId().equals(bookDownloaded.getBookId())) {
          book.setBookDownloaded(true);
          break;
        }
      }
    }
  }

  private void performResponse(List<Book> books, CompletionCallback callback) {
    notifyResponse(books, callback);
  }

  private void fetchBookDetail(final String key, final String bookId, final String version,
      final CompletionCallback callback) {
    networkDataSource.bookDetail(bookId, version, new CompletionCallback<BookEntity>() {
      @Override public void onSuccess(BookEntity result) {
        bddDataSource.persist(key, result);
        Book transformed = transform(result);

        //Set if the book is downloaded attribute
        promiseSetBookDownloaded(transformed);

        notifyResponse(transformed, callback);
      }

      @Override public void onError(ErrorCore error) {
        if (callback != null) {
          callback.onError(error);
        }
      }
    });
  }

  private <T> void notifyResponse(T response, CompletionCallback callback) {
    if (callback != null) {
      callback.onSuccess(response);
    }
  }

  private List<Book> transform(List<BookEntity> result) {
    return entityDataMapper.transform(result);
  }

  private Book transform(BookEntity result) {
    return entityDataMapper.transform(result);
  }
}
