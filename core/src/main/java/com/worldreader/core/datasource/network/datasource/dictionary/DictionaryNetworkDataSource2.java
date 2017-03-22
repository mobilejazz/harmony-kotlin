package com.worldreader.core.datasource.network.datasource.dictionary;

import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.WordDefinitionEntity;

public interface DictionaryNetworkDataSource2 {

  void searchWordDefinition(String word, Callback<WordDefinitionEntity> callback);

}
