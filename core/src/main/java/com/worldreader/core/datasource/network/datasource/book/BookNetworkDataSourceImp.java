package com.worldreader.core.datasource.network.datasource.book;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.datasource.model.BookEntity;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.domain.model.BookSort;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;

public class BookNetworkDataSourceImp implements BookNetworkDataSource {

  public static final String TAG = BookNetworkDataSource.class.getSimpleName();

  public static final String ENDPOINT = "/books";

  private final CountryCodeProvider countryCodeProvider;
  private final ErrorAdapter<Throwable> errorAdapter;
  private final BookApiService bookApiService;
  private final Logger logger;

  @Inject
  public BookNetworkDataSourceImp(BookApiService bookApiService, CountryCodeProvider countryCodeProvider, final ErrorAdapter<Throwable> errorAdapter,
      Logger logger) {
    this.countryCodeProvider = countryCodeProvider;
    this.bookApiService = bookApiService;
    this.errorAdapter = errorAdapter;
    this.logger = logger;
  }

  @Override
  public void books(int index, int limit, List<BookSort> sorters, String list, List<Integer> categories, boolean countryOpen, String language,
      final CompletionCallback<List<BookEntity>> callback) {

    final List<String> sorterList = getSorterList(sorters);
    final String countryCode = getCountryCode();
    final String countryOpenCode = countryOpen ? countryCode : null;

    bookApiService.books(index, limit, sorterList, list, categories, countryCode, countryOpenCode, language)
        .enqueue(new retrofit2.Callback<List<BookEntity>>() {
          @Override public void onResponse(@NonNull final Call<List<BookEntity>> call, @NonNull final retrofit2.Response<List<BookEntity>> response) {
            final boolean successful = response.isSuccessful();
            if (successful) {
              if (callback != null) {
                final List<BookEntity> bookEntities = response.body();
                callback.onSuccess(bookEntities);
              }
            } else {
              if (callback != null) {
                final Retrofit2Error error = Retrofit2Error.httpError(response);
                callback.onError(errorAdapter.of(error));
              }
            }
          }

          @Override public void onFailure(@NonNull final Call<List<BookEntity>> call, @NonNull final Throwable t) {
            if (callback != null) {
              final ErrorCore errorCore = errorAdapter.of(t);
              callback.onError(errorCore);
            }
          }
        });
  }

  @Override public void search(int index, int limit, List<Integer> categories, String title, String author,
      final com.worldreader.core.common.callback.Callback<List<BookEntity>> callback) {
    final String countryCode = getCountryCode();

    bookApiService.search(index, limit, countryCode, title, author, categories).enqueue(new retrofit2.Callback<List<BookEntity>>() {
      @Override public void onResponse(@NonNull final Call<List<BookEntity>> call, @NonNull final retrofit2.Response<List<BookEntity>> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final List<BookEntity> bookEntities = response.body();
            callback.onSuccess(bookEntities);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error).getCause());
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<List<BookEntity>> call, @NonNull final Throwable t) {
        if (callback != null) {
          final ErrorCore errorCore = errorAdapter.of(t);
          callback.onError(errorCore.getCause());
        }
      }
    });
  }

  @Override public void bookDetail(String bookId, String version, final CompletionCallback<BookEntity> callback) {
    final String countryCode = getCountryCode();

    bookApiService.bookDetail(bookId, version, countryCode).enqueue(new retrofit2.Callback<BookEntity>() {
      @Override public void onResponse(@NonNull final Call<BookEntity> call, @NonNull final retrofit2.Response<BookEntity> response) {
        final boolean successful = response.isSuccessful();
        if (successful) {
          if (callback != null) {
            final BookEntity bookEntity = response.body();
            callback.onSuccess(bookEntity);
          }
        } else {
          if (callback != null) {
            final Retrofit2Error error = Retrofit2Error.httpError(response);
            callback.onError(errorAdapter.of(error));
          }
        }
      }

      @Override public void onFailure(@NonNull final Call<BookEntity> call, @NonNull final Throwable t) {
        if (callback != null) {
          final ErrorCore errorCore = errorAdapter.of(t);
          callback.onError(errorCore);
        }
      }
    });
  }

  private String getCountryCode() {
    return countryCodeProvider.getCountryCode();
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  @Nullable private List<String> getSorterList(final List<BookSort> sorters) {
    if (sorters != null && sorters.size() > 0) {
      final List<String> sorterPath = new ArrayList<>();
      for (BookSort sorter : sorters) {
        sorterPath.add(sorter.getUrlPath());
      }

      return sorterPath;
    }

    return null;
  }
}
