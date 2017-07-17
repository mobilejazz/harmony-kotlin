package com.worldreader.core.datasource.storage.mapper.score;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.score.UserScoreEntity;
import com.worldreader.core.datasource.storage.model.UserScoreDb;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ListUserScoreDbToListUserScoreEntityMapper implements Mapper<Optional<List<UserScoreDb>>, Optional<List<UserScoreEntity>>> {

  private final UserScoreDbToUserScoreEntityMapper mapper;

  @Inject public ListUserScoreDbToListUserScoreEntityMapper(UserScoreDbToUserScoreEntityMapper mapper) {
    this.mapper = mapper;
  }

  @Override public Optional<List<UserScoreEntity>> transform(final Optional<List<UserScoreDb>> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final List<UserScoreDb> raw = optional.get();
      final List<UserScoreEntity> toReturn = new ArrayList<>(raw.size());

      for (final UserScoreDb userScoreDb : raw) {
        final UserScoreEntity userScoreEntity = mapper.transform(Optional.fromNullable(userScoreDb)).get();
        toReturn.add(userScoreEntity);
      }

      return Optional.fromNullable(toReturn);
    }
  }

}
