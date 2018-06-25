package com.worldreader.core.datasource.network.datasource.dictionary;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.model.WordDefinitionEntity;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.*;

public class DictionaryNetworkDataSource2Impl implements DictionaryNetworkDataSource2 {

  private final DictionaryApiService2 dictionaryApiService;
  private final ErrorAdapter<Throwable> errorAdapter;

  @Inject public DictionaryNetworkDataSource2Impl(final ErrorAdapter<Throwable> errorAdapter, final DictionaryApiService2 dictionaryApiService,
      final Logger logger) {
    this.dictionaryApiService = dictionaryApiService;
    this.errorAdapter = errorAdapter;
  }

  @Override public void searchWordDefinition(final String word, final Callback<WordDefinitionEntity> callback) {
    try {
      final Response<WordDefinitionEntity> response = dictionaryApiService.definition(word).execute();
      if (response.isSuccessful()) {
        final WordDefinitionEntity body = response.body();
        callback.onSuccess(body);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        callback.onError(errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      callback.onError(errorCore.getCause());
    }
  }

  private ErrorCore<?> mapToErrorCore(Throwable throwable) {
    return errorAdapter.of(throwable);
  }
}