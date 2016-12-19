package com.worldreader.core.domain.model;

import java.io.*;
import java.util.*;

public class Collection implements Serializable {

  private int id;
  private String name;
  private Date start;
  private Date end;
  private List<Book> books;

  public Collection() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public List<Book> getBooks() {
    return books;
  }

  public void setBooks(List<Book> books) {
    this.books = books;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Collection that = (Collection) o;

    return id == that.id;
  }

  @Override public int hashCode() {
    return id;
  }

  @Override public String toString() {
    return "Collection{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", start=" + start +
        ", end=" + end +
        ", books=" + books +
        '}';
  }
}
