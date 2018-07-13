package com.worldreader.core.analytics.event.search;

import com.worldreader.core.analytics.event.AnalyticsEvent;

import java.util.*;

public class SearchAnalyticsEvent implements AnalyticsEvent {

  private final String query;
  private final List<Integer> categories;
  private final List<String> languages;
  private final List<String> ages;
  private final String country;


  public static SearchAnalyticsEvent of(String query, List categories, List languages, List ages, String country) {
    return new SearchAnalyticsEvent(query, categories, languages, ages, country);
  }

  public SearchAnalyticsEvent(String query, List<Integer> categories, List<String> languages, List<String> ages, String country) {
    this.query = query;
    this.categories = categories;
    this.languages = languages;
    this.ages = ages;
    this.country = country;
  }

  public String getQuery() {
    return query;
  }

  public List<Integer> getCategories() {
    return categories;
  }

  public List<String> getLanguages() {
    return languages;
  }

  public List<String> getAges() {
    return ages;
  }

  public String getCountry() {
    return country;
  }
}
