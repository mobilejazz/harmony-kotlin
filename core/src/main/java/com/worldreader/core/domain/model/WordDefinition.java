package com.worldreader.core.domain.model;

import java.util.*;

public class WordDefinition {

  public enum WordType {
    NOUN,
    VERB,
    ADVERB,
    ADJECTIVE,
    UNKNOWN
  }

  private final String word;
  private final Map<WordType, String> meanings;

  public WordDefinition() {
    this.word = "";
    this.meanings = new HashMap<>();
  }

  public WordDefinition(String entry, Map<WordType, String> meaning) {
    this.word = entry;
    this.meanings = meaning;
  }

  public String getWord() {
    return word;
  }

  public Map<WordType, String> getMeanings() {
    return meanings;
  }

}
