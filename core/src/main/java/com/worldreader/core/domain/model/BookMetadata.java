package com.worldreader.core.domain.model;

import com.worldreader.core.datasource.model.ContentOpfEntity;

import java.io.*;
import java.util.*;

public class BookMetadata implements Serializable {

  public static final BookMetadata EMPTY = new BookMetadata();

  private String bookId;
  private String version;
  private int collectionId;
  private String relativeContentUrl;
  private String contentOpfName;
  private String tocResourceName;
  private List<String> resources;
  private Map<String, ContentOpfEntity.Item> imagesResources;
  private boolean streaming;

  // Extra fields for BookFinished
  private String title;
  private String author;

  public String getBookId() {
    return bookId;
  }

  public void setBookId(String bookId) {
    this.bookId = bookId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(final String version) {
    this.version = version;
  }

  public boolean isStreaming() {
    return streaming;
  }

  public void setStreaming(boolean streaming) {
    this.streaming = streaming;
  }

  public String getContentOpfName() {
    return contentOpfName;
  }

  public void setContentOpfName(String contentOpfName) {
    this.contentOpfName = contentOpfName;
  }

  public void setRelativeContentUrl(String relativeUrl) {
    this.relativeContentUrl = relativeUrl;
  }

  public String getRelativeContentUrl() {
    return this.relativeContentUrl;
  }

  public String getTocResource() {
    return tocResourceName;
  }

  public void setTocResource(String tocResource) {
    this.tocResourceName = tocResource;
  }

  public int getCollectionId() {
    return collectionId;
  }

  public void setCollectionId(int collectionId) {
    this.collectionId = collectionId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public List<String> getResources() {
    return resources;
  }

  public void setResources(List<String> resources) {
    this.resources = resources;
  }

  public Map<String, ContentOpfEntity.Item> getImagesResources() {
    return imagesResources;
  }

  public void setImagesResources(Map<String, ContentOpfEntity.Item> resources) {
    this.imagesResources = resources;
  }

}
