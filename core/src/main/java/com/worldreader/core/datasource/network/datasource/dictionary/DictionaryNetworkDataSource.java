package com.worldreader.core.datasource.network.datasource.dictionary;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.datasource.model.WordDefinitionEntity;

@Deprecated
public interface DictionaryNetworkDataSource {

  void searchWordDefinition(String word, CompletionCallback<WordDefinitionEntity> callback);

}
