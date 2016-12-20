package com.worldreader.core.domain.interactors.dictionary;

import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.model.WordDefinition;

public interface GetWordDefinitionInteractor {

  void execute(String word, DomainCallback<WordDefinition, ErrorCore> callback);
}
