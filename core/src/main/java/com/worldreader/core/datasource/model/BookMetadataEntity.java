package com.worldreader.core.datasource.model;

import java.util.*;

/** Fake class as the response of this is not in JSON format but in XML */
public class BookMetadataEntity {

  public static final BookMetadataEntity EMPTY = new BookMetadataEntity();

  private String bookId;
  private String relativeContentUrl;
  private String contentOpfName;
  private String tocResourceName;
  private List<String> resources;

  public void setBookId(String bookId) {
    this.bookId = bookId;
  }

  public String getBookId() {
    return bookId;
  }

  public void setRelativeContentUrl(String relativeUrl) {
    this.relativeContentUrl = relativeUrl;
  }

  public String getRelativeContentUrl() {
    return this.relativeContentUrl;
  }

  public String getContentOpfName() {
    return contentOpfName;
  }

  public void setContentOpfName(String contentOpfName) {
    this.contentOpfName = contentOpfName;
  }

  public String getTocResource() {
    return tocResourceName;
  }

  public void setTocResource(String tocResource) {
    this.tocResourceName = tocResource;
  }

  public List<String> getResources() {
    return resources;
  }

  public void setResources(List<String> resources) {
    this.resources = resources;
  }

}
