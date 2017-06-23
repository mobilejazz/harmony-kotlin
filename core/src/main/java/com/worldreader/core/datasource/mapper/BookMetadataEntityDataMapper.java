package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.domain.model.BookMetadata;

import java.util.*;

public class BookMetadataEntityDataMapper implements Mapper<BookMetadata, BookMetadataEntity> {

  @Override public BookMetadata transform(BookMetadataEntity data) {
    BookMetadata bookMetadata = new BookMetadata();
    bookMetadata.setBookId(data.getBookId());
    bookMetadata.setContentOpfName(data.getContentOpfName());
    bookMetadata.setRelativeContentUrl(data.getRelativeContentUrl());
    bookMetadata.setTocResource(data.getTocResource());
    bookMetadata.setResources(data.getResources());
    bookMetadata.setImagesResources(data.getImagesResources());
    return bookMetadata;
  }

  @Override public List<BookMetadata> transform(List<BookMetadataEntity> data) {
    throw new IllegalArgumentException("List are not supported!");
  }

  @Override public BookMetadataEntity transformInverse(BookMetadata data) {
    BookMetadataEntity bookMetadataEntity = new BookMetadataEntity();
    bookMetadataEntity.setBookId(data.getBookId());
    bookMetadataEntity.setTocResource(data.getTocResource());
    bookMetadataEntity.setContentOpfName(data.getContentOpfName());
    bookMetadataEntity.setRelativeContentUrl(data.getRelativeContentUrl());
    bookMetadataEntity.setResources(data.getResources());
    bookMetadataEntity.setImagesResources(data.getImagesResources());
    return bookMetadataEntity;
  }

  @Override public List<BookMetadataEntity> transformInverse(List<BookMetadata> data) {
    throw new IllegalArgumentException("Lists are not supported!");
  }
}
