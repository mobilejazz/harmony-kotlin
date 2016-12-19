package com.worldreader.core.domain.model;

import java.util.*;

public class WordDefinition {

  public enum WordType {
    NOUN,
    VERB,
    ADVERB,
    ADJECTIVE
  }

  private String entry;
  private String request;
  private String response;
  private Map<WordType, String> meaning;

  public WordDefinition() {
  }

  public WordDefinition(String entry, String request, String response,
      Map<WordType, String> meaning) {
    this.entry = entry;
    this.request = request;
    this.response = response;
    this.meaning = meaning;
  }

  public String getEntry() {
    return entry;
  }

  public void setEntry(String entry) {
    this.entry = entry;
  }

  public String getRequest() {
    return request;
  }

  public void setRequest(String request) {
    this.request = request;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public Map<WordType, String> getMeaning() {
    return meaning;
  }

  public void setMeaning(Map<WordType, String> meaning) {
    this.meaning = meaning;
  }
}
