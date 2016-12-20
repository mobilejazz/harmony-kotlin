package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.ScoreEntity;
import com.worldreader.core.domain.model.Score;

import java.util.*;

public class RatingEntityDataMapper implements Mapper<Score, ScoreEntity> {

  @Override public Score transform(ScoreEntity data) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override public List<Score> transform(List<ScoreEntity> data) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  public ScoreEntity transformInverse(Score score) {
    return new ScoreEntity(score.getScore());
  }

  @Override public List<ScoreEntity> transformInverse(List<Score> data) {
    throw new IllegalStateException("transformInverse(List<Score> data) is not supported");
  }
}
