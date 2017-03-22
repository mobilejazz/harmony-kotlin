package com.worldreader.core.datasource.network.datasource.dictionary;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.deprecated.error.adapter.ErrorRetrofitAdapter;
import com.worldreader.core.datasource.model.WordDefinitionEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

@Deprecated
public class DictionaryNetworkDataSourceImpl implements DictionaryNetworkDataSource {

  private DictionaryApiService dictionaryApiService;
  private Logger logger;
  private ErrorAdapter<RetrofitError> errorAdapter = new ErrorRetrofitAdapter();

  private static final String TAG = DictionaryNetworkDataSource.class.getSimpleName();

  @Inject
  public DictionaryNetworkDataSourceImpl(DictionaryApiService dictionaryApiService, Logger logger) {
    this.dictionaryApiService = dictionaryApiService;
    this.logger = logger;
  }

  @Override public void searchWordDefinition(String word,
      final CompletionCallback<WordDefinitionEntity> callback) {
    dictionaryApiService.definition(word, new Callback<WordDefinitionEntity>() {
      @Override public void success(WordDefinitionEntity wordDefinitionEntity, Response response) {
        if (callback != null) {
          callback.onSuccess(wordDefinitionEntity);
        }
      }

      @Override public void failure(RetrofitError error) {
        if (callback != null) {
          logger.d(TAG, error.toString());
          callback.onError(errorAdapter.of(error));
        }
      }
    });
  }
}
