package com.worldreader.core.datasource;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.mapper.WordDefinitionEntityDataMapper;
import com.worldreader.core.datasource.model.WordDefinitionEntity;
import com.worldreader.core.datasource.network.datasource.dictionary.DictionaryNetworkDataSource;
import com.worldreader.core.domain.model.WordDefinition;
import com.worldreader.core.domain.repository.DictionaryRepository;

import javax.inject.Inject;

public class DictionaryDataSource implements DictionaryRepository {

  private final DictionaryNetworkDataSource dictionaryNetworkDataSource;
  private final WordDefinitionEntityDataMapper wordDefinitionEntityDataMapper;

  @Inject public DictionaryDataSource(DictionaryNetworkDataSource dictionaryNetworkDataSource,
      WordDefinitionEntityDataMapper wordDefinitionEntityDataMapper) {
    this.dictionaryNetworkDataSource = dictionaryNetworkDataSource;
    this.wordDefinitionEntityDataMapper = wordDefinitionEntityDataMapper;
  }

  @Override
  public void searchWordDefinition(String word, final CompletionCallback<WordDefinition> callback) {
    dictionaryNetworkDataSource.searchWordDefinition(word,
        new CompletionCallback<WordDefinitionEntity>() {
          @Override public void onSuccess(WordDefinitionEntity wordDefinitionEntity) {
            if (callback != null) {
              callback.onSuccess(wordDefinitionEntityDataMapper.transform(wordDefinitionEntity));
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
