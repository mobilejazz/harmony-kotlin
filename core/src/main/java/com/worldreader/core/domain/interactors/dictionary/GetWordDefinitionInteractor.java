package com.worldreader.core.domain.interactors.dictionary;

import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.domain.model.WordDefinition;

public interface GetWordDefinitionInteractor {

  ListenableFuture<WordDefinition> execute(final String word);

}
