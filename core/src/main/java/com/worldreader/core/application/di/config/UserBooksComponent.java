package com.worldreader.core.application.di.config;

import com.google.common.base.Optional;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.user.userbooks.UserBookEntity;
import com.worldreader.core.datasource.storage.model.UserBookDb;
import com.worldreader.core.domain.interactors.user.GetFinishedBooksCountInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.GetAllUserBookInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.GetUserLikedBooksCountInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.IsBookLikedInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.LikeBookInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.PutAllUserBooksInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.PutUserBookInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.UnlikeBookInteractor;
import com.worldreader.core.domain.repository.UserBooksRepository;

public interface UserBooksComponent {

  UserBooksRepository userBooksRepository();

  LikeBookInteractor likeBookInteractor();

  UnlikeBookInteractor unlikeBookInteractor();

  IsBookLikedInteractor isBookLikedInteractor();

  GetUserLikedBooksCountInteractor getUserLikedBooksCountInteractor();

  GetFinishedBooksCountInteractor getFinishedBooksCountInteractor();

  GetAllUserBookInteractor getAllUserBookInteractor();

  PutUserBookInteractor putUserBookInteractor();

  PutAllUserBooksInteractor putAllUserBooksInteractor();

  Mapper<Optional<UserBookDb>, Optional<UserBookEntity>> fromUserBookDbToUserBookEntity();

  Mapper<Optional<UserBookEntity>, Optional<UserBookDb>> fromUserBookEntityToUserBookDb();
}
