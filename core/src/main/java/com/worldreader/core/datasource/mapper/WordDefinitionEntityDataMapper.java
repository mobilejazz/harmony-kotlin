package com.worldreader.core.datasource.mapper;

import com.google.common.collect.Lists;
import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.WordDefinitionEntity;
import com.worldreader.core.domain.model.WordDefinition;

import javax.inject.Inject;
import java.util.*;

public class WordDefinitionEntityDataMapper
    implements Mapper<WordDefinition, WordDefinitionEntity> {

  @Inject public WordDefinitionEntityDataMapper() {
  }

  @Override public WordDefinition transform(WordDefinitionEntity data) {
    WordDefinition wordDefinition = new WordDefinition();

    wordDefinition.setEntry(data.getEntry());
    wordDefinition.setRequest(data.getRequest());
    wordDefinition.setResponse(data.getResponse());
    wordDefinition.setMeaning(transformType(data.getMeaning()));

    return wordDefinition;
  }

  @Override public List<WordDefinition> transform(List<WordDefinitionEntity> data) {
    List<WordDefinition> list = Lists.newArrayList();
    for (WordDefinitionEntity entity : data) {
      list.add(transform(entity));
    }
    return list;
  }

  @Override public WordDefinitionEntity transformInverse(WordDefinition data) {
    return null;
  }

  @Override public List<WordDefinitionEntity> transformInverse(List<WordDefinition> data) {
    return null;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////
  private Map<WordDefinition.WordType, String> transformType(
      Map<WordDefinitionEntity.WordType, String> meaning) {
    Map<WordDefinition.WordType, String> map = new HashMap<>();

    if (meaning == null) {
      return map;
    }

    for (Map.Entry<WordDefinitionEntity.WordType, String> entry : meaning.entrySet()) {
      map.put(toWordDefinitionWordType(entry.getKey()), entry.getValue());
    }

    return map;
  }

  private WordDefinition.WordType toWordDefinitionWordType(WordDefinitionEntity.WordType type) {
    switch (type) {
      case NOUN:
        return WordDefinition.WordType.NOUN;
      case VERB:
        return WordDefinition.WordType.VERB;
      case ADVERB:
        return WordDefinition.WordType.ADVERB;
      case ADJECTIVE:
        return WordDefinition.WordType.ADJECTIVE;
      default:
        return null;
    }
  }
}
