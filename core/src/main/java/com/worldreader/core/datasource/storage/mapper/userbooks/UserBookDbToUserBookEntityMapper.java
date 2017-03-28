package com.worldreader.core.datasource.storage.mapper.userbooks;

import android.text.TextUtils;
import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.network.model.UserBookNetworkResponse;
import com.worldreader.core.datasource.storage.model.UserBookDb;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class UserBookDbToUserBookEntityMapper
    implements Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> {

  private final Gson gson;

  @Inject public UserBookDbToUserBookEntityMapper(Gson gson) {
    this.gson = gson;
  }

  @Override public Optional<UserBookEntity> transform(final Optional<UserBookDb> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookDb raw = optional.get();
      final UserBookEntity entity = new UserBookEntity.Builder().setId(raw.getId())
          .setUserId(raw.getUserId())
          .setBookId(raw.getBookId())
          .setInMyBooks(raw.isFavorite())
          .setBookmark(raw.getBookmark())
          .setFinished(raw.isFinished())
          .setSaveOfflineAt(toDate(raw.getSaveOfflineAt()))
          .setRating(raw.getRating())
          .setLiked(raw.isLiked())
          .setCollectionIds(UserBookNetworkResponse.mapCollectionIds(raw.getCollectionIds()))
          .setCreatedAt(toDate(raw.getCreatedAt()))
          .setUpdatedAt(toDate(raw.getUpdatedAt()))
          .setSynchronized(raw.getSyncronized())
          .build();
      return Optional.of(entity);
    }
  }

  private Date toDate(final String createdAt) {
    if (TextUtils.isEmpty(createdAt)) {
      return null;
    }

    final String date = "\"" + createdAt + "\""; // Restore previous removed comma

    return gson.fromJson(date, Date.class);
  }

}
