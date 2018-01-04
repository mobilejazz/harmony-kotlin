package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.BookMetadataEntity;
import com.worldreader.core.domain.model.BookMetadata;

import java.util.*;

public class BookMetadataEntityDataMapper implements Mapper<BookMetadata, BookMetadataEntity> {

  @Override public BookMetadata transform(BookMetadataEntity data) {
    BookMetadata bookMetadata = new BookMetadata();
    bookMetadata.bookId = data.getBookId();
    bookMetadata.version = data.getVersion();
    bookMetadata.contentOpfName = data.getContentOpfName();
    bookMetadata.tocResourceName = data.getTocResource();
    bookMetadata.resources = data.getResources();
    bookMetadata.imagesResources = data.getImagesResources();
    bookMetadata.contentOpfPath = data.getContentOpfPath();
    return bookMetadata;
  }

  @Override public List<BookMetadata> transform(List<BookMetadataEntity> data) {
    throw new IllegalArgumentException("List are not supported!");
  }

  @Override public BookMetadataEntity transformInverse(BookMetadata data) {
    BookMetadataEntity bookMetadataEntity = new BookMetadataEntity();
    bookMetadataEntity.setBookId(data.bookId);
    bookMetadataEntity.setVersion(data.version);
    bookMetadataEntity.setTocResource(data.tocResourceName);
    bookMetadataEntity.setContentOpfName(data.contentOpfName);
    bookMetadataEntity.setResources(data.resources);
    bookMetadataEntity.setImagesResources(data.imagesResources);
    bookMetadataEntity.setContentOpfPath(data.contentOpfPath);
    return bookMetadataEntity;
  }

  @Override public List<BookMetadataEntity> transformInverse(List<BookMetadata> data) {
    throw new IllegalArgumentException("Lists are not supported!");
  }
}
