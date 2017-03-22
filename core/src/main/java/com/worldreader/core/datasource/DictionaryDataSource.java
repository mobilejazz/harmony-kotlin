package com.worldreader.core.datasource;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.mapper.WordDefinitionEntityDataMapper;
import com.worldreader.core.datasource.model.WordDefinitionEntity;
import com.worldreader.core.datasource.network.datasource.dictionary.DictionaryNetworkDataSource2;
import com.worldreader.core.domain.model.WordDefinition;
import com.worldreader.core.domain.repository.DictionaryRepository;

import javax.inject.Inject;

public class DictionaryDataSource implements DictionaryRepository {

  private final DictionaryNetworkDataSource2 dictionaryNetworkDataSource;
  private final WordDefinitionEntityDataMapper wordDefinitionEntityDataMapper;

  @Inject public DictionaryDataSource(DictionaryNetworkDataSource2 dictionaryNetworkDataSource,
      WordDefinitionEntityDataMapper wordDefinitionEntityDataMapper) {
    this.dictionaryNetworkDataSource = dictionaryNetworkDataSource;
    this.wordDefinitionEntityDataMapper = wordDefinitionEntityDataMapper;
  }

  @Override public void searchWordDefinition(String word, final CompletionCallback<WordDefinition> callback) {
    dictionaryNetworkDataSource.searchWordDefinition(word, new Callback<WordDefinitionEntity>() {
      @Override public void onSuccess(final WordDefinitionEntity wordDefinitionEntity) {
            if (callback != null) {
              callback.onSuccess(wordDefinitionEntityDataMapper.transform(wordDefinitionEntity));
            }
      }

      @Override public void onError(final Throwable e) {
            if (callback != null) {
              callback.onError(ErrorCore.of(e));
            }
      }
    });
  }
}
