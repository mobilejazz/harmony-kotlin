package com.worldreader.core.datasource.mapper.user.userbook;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.domain.model.user.UserBook;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class UserBookEntityToUserBookMapper
    implements Mapper<Optional<UserBookEntity>, Optional<UserBook>> {

  @Inject public UserBookEntityToUserBookMapper() {
  }

  @Override public Optional<UserBook> transform(Optional<UserBookEntity> optionalEntity) {
    if (!optionalEntity.isPresent()) {
      return Optional.absent();
    } else {
      final UserBookEntity entity = optionalEntity.get();
      final UserBook userBook = new UserBook.Builder().setId(entity.getId())
          .setBookId(entity.getBookId())
          .setUserId(entity.getUserId())
          .setCreatedAt(entity.getCreatedAt())
          .setUpdatedAt(entity.getUpdatedAt())
          .setRating(entity.getRating())
          .setFavorite(entity.isFavorite())
          .setCollectionIds(entity.getCollectionIds())
          .setBookmark(entity.getBookmark())
          .setFinished(entity.isFinished())
          .setLiked(entity.isLiked())
          .setSaveOfflineAt(entity.getSaveOfflineAt())
          .setSynchronized(entity.isSynchronized())
          .build();
      return Optional.of(userBook);
    }
  }

}
