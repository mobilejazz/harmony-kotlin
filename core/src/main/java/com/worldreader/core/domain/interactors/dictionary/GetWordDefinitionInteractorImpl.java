package com.worldreader.core.domain.interactors.dictionary;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.model.WordDefinition;
import com.worldreader.core.domain.repository.DictionaryRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;

public class GetWordDefinitionInteractorImpl extends AbstractInteractor<WordDefinition, ErrorCore>
    implements GetWordDefinitionInteractor {

  private DictionaryRepository dictionaryRepository;

  private String word;
  private DomainCallback<WordDefinition, ErrorCore> callback;

  @Inject public GetWordDefinitionInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      DictionaryRepository dictionaryRepository) {
    super(executor, mainThread);
    this.dictionaryRepository = dictionaryRepository;
  }

  @Override public void execute(String word, DomainCallback<WordDefinition, ErrorCore> callback) {
    this.word = word;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    dictionaryRepository.searchWordDefinition(word, new CompletionCallback<WordDefinition>() {
      @Override public void onSuccess(WordDefinition wordDefinition) {
        performSuccessCallback(callback, wordDefinition);
      }

      @Override public void onError(ErrorCore errorCore) {
        performErrorCallback(callback, errorCore);
      }
    });
  }
}
