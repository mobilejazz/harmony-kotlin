package com.worldreader.core.datasource.storage.mapper.userbooks;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.model.UserBookNetworkBody;
import com.worldreader.core.datasource.storage.model.UserBookDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserBookEntityToUserBookDbMapper
    implements Mapper<Optional<UserBookEntity>, Optional<UserBookDb>> {

  private final Gson gson;

  @Inject public UserBookEntityToUserBookDbMapper(Gson gson) {
    this.gson = gson;
  }

  @Override public Optional<UserBookDb> transform(final Optional<UserBookEntity> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookEntity raw = optional.get();
      final UserBookDb entity = new UserBookDb.Builder().withId(raw.getId())
          .withUserId(raw.getUserId())
          .withBookId(raw.getBookId())
          .withFavorite(raw.isInMyBooks())
          .withBookmark(raw.getBookmark())
          .withFinished(raw.isFinished())
          .withSaveOfflineAt(
              raw.getSaveOfflineAt() == null ? "" : getIsoDate(raw.getSaveOfflineAt()))
          .withRating(raw.getRating())
          .withCollectionIds(UserBookNetworkBody.mapCollectionIds(raw.getCollectionIds()))
          .withLiked(raw.isLiked())
          .withSynchronized(raw.isSynchronized())
          .withCreatedAt(getIsoDate(raw.getCreatedAt()))
          .withUpdatedAt(getIsoDate(raw.getUpdatedAt()))
          .withOpenedAt(getIsoDate(raw.getOpenedAt()))
          .build();
      return Optional.of(entity);
    }
  }

  private String getIsoDate(final Date createdAt) {
    Date date;
    if (createdAt == null) {
      date = new Date();
    } else {
      date = createdAt;
    }
    return gson.toJson(date).replaceAll("\"", "");
  }
}
