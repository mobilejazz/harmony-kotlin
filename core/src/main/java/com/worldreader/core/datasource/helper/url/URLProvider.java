package com.worldreader.core.datasource.helper.url;

import android.text.TextUtils;
import com.worldreader.core.domain.model.BookSort;

import java.util.*;

public class URLProvider {

  public static final String KEY_LIST_FEATURED = "featured";
  public static final String KEY_LATEST = "latest";
  private static final String KEY_COUNTRY = "country";
  private static final String KEY_INDEX = "index";
  private static final String KEY_LIMIT = "limit";
  private static final String KEY_LIST = "list";
  private static final String KEY_SORT = "sort";
  private static final String KEY_OPENS_COUNTRY = "opensCountry";
  private static final String KEY_CATEGORY = "category";
  private static final String KEY_TITLE = "title";
  private static final String KEY_AUTHOR = "author";
  private static final String KEY_PUBLISHER = "publisher";
  private static final String KEY_LANGUAGE = "languages";
  private static final String KEY_TAG = "tag";

  public static Builder withEndpoint(String endpoint) {
    return new Builder(endpoint);
  }

  private enum Symbol {
    QUESTION("?"),
    AND("&"),
    EQUAL("="),
    SLASH("/");

    private String symbol;

    Symbol(String symbol) {
      this.symbol = symbol;
    }

    public String getSymbol() {
      return symbol;
    }
  }

  public static class Builder {

    private boolean isAddedQuestion = false;

    private StringBuilder builder;

    private String endpoint;
    private int index = -1;
    private int limit = -1;
    private String country;
    private List<BookSort> sorters;
    private List<Integer> categories;
    private String openCountry;
    private String listValue;
    private int id = -1;
    private String stringId;
    private String countryCode3Iso;
    private String version;
    private String title;
    private String author;
    private String publisher;
    private String subpath;
    private List<String> languages;
    private String languageQuery;
    private List<String> ages;

    public Builder(String endpoint) {
      this.endpoint = endpoint;

      builder = new StringBuilder();
    }

    public Builder addIndex(int index) {
      this.index = index;
      return this;
    }

    public Builder addId(int id) {
      this.id = id;
      return this;
    }

    public Builder addId(String id) {
      this.stringId = id;
      return this;
    }

    public Builder addLimit(int limit) {
      this.limit = limit;
      return this;
    }

    public Builder addCountryCode(String country) {
      this.country = country;
      return this;
    }

    public Builder addSorters(List<BookSort> sorters) {
      this.sorters = sorters;
      return this;
    }

    public Builder addCategories(List<Integer> categories) {
      this.categories = categories;
      return this;
    }

    public Builder addOpenCountry(String country) {
      this.openCountry = country;
      return this;
    }

    public Builder addList(String value) {
      this.listValue = value;
      return this;
    }

    public Builder addCountryCodeIso3(String value) {
      this.countryCode3Iso = value;
      return this;
    }

    public Builder addVersion(String version) {
      this.version = version;
      return this;
    }

    public Builder addTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder addAuthor(String author) {
      this.author = author;
      return this;
    }

    public Builder addPublisher(String publisher) {
      this.publisher = publisher;
      return this;
    }

    public Builder addSubPath(String subpath) {
      this.subpath = subpath;
      return this;
    }

    public Builder addLanguage(String language) {
      this.languages = Arrays.asList(language);
      return this;
    }

    public Builder addLanguage(List<String> languages) {
      this.languages = languages;
      return this;
    }

    public Builder addLaguageQuery(String language) {
      this.languageQuery = language;
      return this;
    }

    public Builder addAges(List<String> ages) {
      this.ages = ages;
      return this;
    }

    public String build() {
      //The endpoint is the most important thing in the URL
      if (isValidEndpoint()) {
        builder.append(endpoint);

        if (id >= 0 || stringId != null) {
          builder.append(Symbol.SLASH.getSymbol());
          builder.append(id >= 0 ? id : stringId);
        }

        if (!TextUtils.isEmpty(countryCode3Iso)) {
          builder.append(Symbol.SLASH.getSymbol());
          builder.append(countryCode3Iso);
        }

        if (languages != null && !languages.isEmpty()) {
          for (String language : languages) {
            builder.append(Symbol.SLASH.getSymbol());
            builder.append(language);
          }
        }

        if (index >= 0) {
          addParameter(KEY_INDEX, index);
        }

        if (limit >= 0) {
          addParameter(KEY_LIMIT, limit);
        }

        if (!TextUtils.isEmpty(country)) {
          addParameter(KEY_COUNTRY, country);
        }

        if (!TextUtils.isEmpty(openCountry)) {
          addParameter(KEY_OPENS_COUNTRY, openCountry);
        }

        if (sorters != null && sorters.size() > 0) {
          for (BookSort sorter : sorters) {
            addParameter(KEY_SORT, sorter.getUrlPath());
          }
        }

        if (categories != null && categories.size() > 0) {
          for (Integer category : categories) {
            addParameter(KEY_CATEGORY, category);
          }
        }

        if (!TextUtils.isEmpty(listValue)) {
          addParameter(KEY_LIST, listValue);
        }

        if (!TextUtils.isEmpty(title)) {
          addParameter(KEY_TITLE, title);
        }

        if (!TextUtils.isEmpty(author)) {
          addParameter(KEY_AUTHOR, author);
        }

        if (!TextUtils.isEmpty(publisher)) {
          addParameter(KEY_PUBLISHER, publisher);
        }

        if (!TextUtils.isEmpty(languageQuery)) {
          addParameter(KEY_LANGUAGE, languageQuery);
        }

        if (ages != null && !ages.isEmpty()) {
          for (String age : ages) {
            builder.append(Symbol.SLASH.getSymbol());
            builder.append(age);
          }
        }

        if (version != null) {
          builder.append(Symbol.SLASH.getSymbol());
          builder.append(version);
        }

        if (subpath != null) {
          builder.append(Symbol.SLASH.getSymbol());
          builder.append(subpath);
        }
      }

      return builder.toString();
    }

    private boolean isValidEndpoint() {
      return !TextUtils.isEmpty(endpoint);
    }

    private void addParameter(String key, String value) {
      if (!isAddedQuestion) {
        addSymbol(Symbol.QUESTION);
        isAddedQuestion = true;
      } else {
        addSymbol(Symbol.AND);
      }

      addValue(key);
      addSymbol(Symbol.EQUAL);
      addValue(value);
    }

    private void addParameter(String key, int value) {
      addParameter(key, String.valueOf(value));
    }

    private void addSymbol(Symbol symbol) {
      builder.append(symbol.getSymbol());
    }

    private void addValue(int value) {
      builder.append(value);
    }

    private void addValue(String value) {
      builder.append(value);
    }
  }
}
