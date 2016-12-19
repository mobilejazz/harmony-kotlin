package com.worldreader.core.domain.model;

public class BookSort {

  public enum Type {
    DATE("date"),
    OPENS("opens"),
    SCORE("score");

    private String type;

    Type(String type) {
      this.type = type;
    }

    public String getType() {
      return type;
    }
  }

  public enum Value {
    ASC("asc"),
    DESC("desc");

    private String value;

    Value(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  private Type sortType;
  private Value sortValue;

  private BookSort(Type sortType, Value sortValue) {
    this.sortType = sortType;
    this.sortValue = sortValue;
  }

  public static BookSort createBookSort(Type type, Value value) {
    return new BookSort(type, value);
  }

  public String getUrlPath() {
    StringBuilder builder = new StringBuilder();
    builder.append(sortType.getType());
    builder.append(":");
    builder.append(sortValue.getValue());

    return builder.toString();
  }
}