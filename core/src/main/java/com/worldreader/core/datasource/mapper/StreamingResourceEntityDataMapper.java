package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.StreamingResourceEntity;
import com.worldreader.core.domain.model.StreamingResource;

import java.util.*;

public class StreamingResourceEntityDataMapper
    implements Mapper<StreamingResource, StreamingResourceEntity> {

  @Override public StreamingResource transform(StreamingResourceEntity data) {
    if (data == null) {
      throw new IllegalArgumentException("StreamingResourceEntity == null");
    }

    return StreamingResource.create(data.getInputStream());
  }

  @Override public List<StreamingResource> transform(List<StreamingResourceEntity> data) {
    throw new UnsupportedOperationException(
        "transform(List<StreamingResourceEntity> data) not supported");
  }

  @Override public StreamingResourceEntity transformInverse(StreamingResource data) {
    if (data == null) {
      throw new IllegalArgumentException("StreamingResource == null");
    }

    return StreamingResourceEntity.create(data.getInputStream());
  }

  @Override public List<StreamingResourceEntity> transformInverse(List<StreamingResource> data) {
    throw new UnsupportedOperationException("transformInverse(List<StreamingResource> data)");
  }
}
