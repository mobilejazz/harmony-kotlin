package com.worldreader.core.domain.interactors.dictionary;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.model.WordDefinition;
import com.worldreader.core.domain.repository.DictionaryRepository;

import javax.inject.Inject;
import java.util.concurrent.*;

public class GetWordDefinitionWordnikInteractor implements GetWordDefinitionInteractor {

  private final ListeningExecutorService executor;
  private final DictionaryRepository repository;

  @Inject public GetWordDefinitionWordnikInteractor(ListeningExecutorService executor, DictionaryRepository dictionaryRepository) {
    this.executor = executor;
    this.repository = dictionaryRepository;
  }

  public ListenableFuture<WordDefinition> execute(final String word) {
    return executor.submit(new Callable<WordDefinition>() {

      WordDefinition result;

      @Override public WordDefinition call() throws Exception {
        repository.searchWordDefinition(word, new CompletionCallback<WordDefinition>() {
          @Override public void onSuccess(WordDefinition wordDefinition) {
            result = wordDefinition;
          }

          @Override public void onError(ErrorCore errorCore) {
            throw new RuntimeException(errorCore.getCause());
          }
        });

        return result;
      }
    });
  }

}
