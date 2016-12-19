package com.worldreader.core.datasource.model;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class WordDefinitionEntity {

  public enum WordType {
    @SerializedName("noun")
    NOUN,

    @SerializedName("verb")
    VERB,

    @SerializedName("adverb")
    ADVERB,

    @SerializedName("adjective")
    ADJECTIVE
  }

  @SerializedName("entry") String entry;
  @SerializedName("request") String request;
  @SerializedName("response") String response;
  @SerializedName("meaning") Map<WordType, String> meaning;

  public WordDefinitionEntity() {
  }

  public WordDefinitionEntity(String entry, String request, String response,
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
