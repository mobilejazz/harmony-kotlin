package com.worldreader.core.domain.repository;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.WordDefinition;

public interface DictionaryRepository {

  void searchWordDefinition(String word, CompletionCallback<WordDefinition> callback);
}
