package com.worldreader.core.datasource.mapper.user.userbook;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.domain.model.user.UserBook;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserBookToUserBookEntityMapper
    implements Mapper<Optional<UserBook>, Optional<UserBookEntity>> {

  @Inject public UserBookToUserBookEntityMapper() {
  }

  @Override public Optional<UserBookEntity> transform(final Optional<UserBook> optional) {
    if (!optional.isPresent()) {
      return Optional.absent();
    } else {
      final UserBook raw = optional.get();
      final UserBookEntity entity = new UserBookEntity.Builder().setId(raw.getId())
          .setUserId(raw.getUserId())
          .setBookId(raw.getBookId())
          .setInMyBooks(raw.isInMyBooks())
          .setBookmark(raw.getBookmark())
          .setFinished(raw.isFinished())
          .setSaveOfflineAt(raw.getSaveOfflineAt())
          .setRating(raw.getRating())
          .setLiked(raw.isLiked())
          .setCreatedAt(raw.getCreatedAt())
          .setUpdatedAt(raw.getUpdatedAt())
          .setCollectionIds(raw.getCollectionIds())
          .setSynchronized(raw.isSynchronized())
          .build();
      return Optional.of(entity);
    }
  }

}
