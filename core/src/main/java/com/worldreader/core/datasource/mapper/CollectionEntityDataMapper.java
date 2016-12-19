package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.CollectionEntity;
import com.worldreader.core.domain.model.Collection;

import javax.inject.Inject;
import java.util.*;

public class CollectionEntityDataMapper implements Mapper<Collection, CollectionEntity> {

  private BookEntityDataMapper bookMapper;

  @Inject public CollectionEntityDataMapper(BookEntityDataMapper bookMapper) {
    this.bookMapper = bookMapper;
  }

  @Override public Collection transform(CollectionEntity data) {
    if (data == null) {
      throw new IllegalArgumentException("CollectionEntity must be not null");
    }

    Collection collection = new Collection();
    collection.setId(data.getId());
    collection.setName(data.getName());
    collection.setStart(data.getStart());
    collection.setEnd(data.getEnd());
    collection.setBooks(bookMapper.transform(data.getBooks()));

    return collection;
  }

  @Override public List<Collection> transform(List<CollectionEntity> data) {
    if (data == null) {
      throw new IllegalArgumentException("CollectionEntities must be not null");
    }

    List<Collection> collections = new ArrayList<>();
    for (CollectionEntity collectionEntity : data) {
      collections.add(transform(collectionEntity));
    }

    return collections;
  }

  @Override public CollectionEntity transformInverse(Collection data) {
    throw new IllegalStateException("transformInverse(Collection data) is not supported");
  }

  @Override public List<CollectionEntity> transformInverse(List<Collection> data) {
    throw new IllegalStateException("transformInverse(List<Collection> data) is not supported");
  }
}
