package com.worldreader.core.datasource.network.datasource.book;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.deprecated.error.adapter.ErrorRetrofitAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.BookEntity;
import com.worldreader.core.domain.helper.DefaultValues;
import com.worldreader.core.domain.model.BookSort;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.*;

public class BookNetworkDataSourceImp implements BookNetworkDataSource {

  public static final String ENDPOINT = "/books";

  private CountryCodeProvider countryCodeProvider;

  private ErrorAdapter<RetrofitError> errorAdapter = new ErrorRetrofitAdapter();

  private BookApiService bookApiService;
  private Logger logger;
  public static final String TAG = BookNetworkDataSource.class.getSimpleName();

  @Inject public BookNetworkDataSourceImp(BookApiService bookApiService,
      CountryCodeProvider countryCodeProvider, Logger logger) {
    this.countryCodeProvider = countryCodeProvider;
    this.bookApiService = bookApiService;
    this.logger = logger;
  }

  @Override public void fetchBooks(int index, int limit, List<BookSort> sorters, String list,
      List<Integer> categories, boolean countryOpen, String language,
      final CompletionCallback<List<BookEntity>> callback) {

    List<String> sorterList = getSorterList(sorters);
    String countryCode = getCountryCode();
    String countryOpenCode = countryOpen ? countryCode : null;

    bookApiService.books(index, limit, sorterList, list, categories, countryCode, countryOpenCode,
        language, new Callback<List<BookEntity>>() {
          @Override public void success(List<BookEntity> bookEntities, Response response) {
            if (callback != null) {
              callback.onSuccess(bookEntities);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error));
            }
          }
        });
  }

  @Override public void fetchSearchBooksByTitle(int index, int limit, String title,
      final CompletionCallback<List<BookEntity>> callback) {
    String countryCode = getCountryCode();

    bookApiService.searchBooksByTitle(index, limit, countryCode, title,
        new Callback<List<BookEntity>>() {
          @Override public void success(List<BookEntity> bookEntities, Response response) {
            if (callback != null) {
              callback.onSuccess(bookEntities);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error));
            }
          }
        });
  }

  @Override public void fetchSearchBooksByAuthor(int index, int limit, String author,
      final CompletionCallback<List<BookEntity>> callback) {
    String countryCode = getCountryCode();

    bookApiService.searchBooksByAuthor(index, limit, countryCode, author,
        new Callback<List<BookEntity>>() {
          @Override public void success(List<BookEntity> bookEntities, Response response) {
            if (callback != null) {
              callback.onSuccess(bookEntities);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error));
            }
          }
        });
  }

  @Override
  public void search(int index, int limit, List<Integer> categories, String title, String author,
      final com.worldreader.core.common.callback.Callback<List<BookEntity>> callback) {
    String countryCode = getCountryCode();

    bookApiService.search(index, limit, countryCode, title, author, categories,
        new Callback<List<BookEntity>>() {
          @Override public void success(List<BookEntity> bookEntities, Response response) {
            if (callback != null) {
              callback.onSuccess(bookEntities);
            }
          }

          @Override public void failure(RetrofitError error) {
            if (callback != null) {
              logger.e(TAG, error.toString());
              callback.onError(errorAdapter.of(error).getCause());
            }
          }
        });
  }

  private String getCountryCode() {
    return countryCodeProvider.getCountryCode();
  }

  @Override public void fetchBookDetail(String bookId, String version,
      final CompletionCallback<BookEntity> callback) {

    String countryCode = getCountryCode();

    bookApiService.bookDetail(bookId, version, countryCode, new Callback<BookEntity>() {
      @Override public void success(BookEntity bookEntity, Response response) {
        if (callback != null) {
          callback.onSuccess(bookEntity);
        }
      }

      @Override public void failure(RetrofitError error) {
        if (callback != null) {
          logger.e(TAG, error.toString());
          callback.onError(errorAdapter.of(error));
        }
      }
    });
  }

  @Override public void fetchRecommendedBooks(CompletionCallback<List<BookEntity>> callback) {
    this.fetchBooks(0, 3,
        Collections.singletonList(BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC)),
        "es", null, false, DefaultValues.DEFAULT_LANGUAGE, callback);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private List<String> getSorterList(List<BookSort> sorters) {
    if (sorters != null || sorters.size() > 0) {
      List<String> sorterPath = new ArrayList<>();
      for (BookSort sorter : sorters) {
        sorterPath.add(sorter.getUrlPath());
      }

      return sorterPath;
    }

    return null;
  }
}
