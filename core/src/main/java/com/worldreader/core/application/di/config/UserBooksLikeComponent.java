package com.worldreader.core.application.di.config;

import com.worldreader.core.domain.interactors.user.userbookslike.GetAllUserBookLikesInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.PutAllUserBooksLikesInteractor;
import com.worldreader.core.domain.repository.UserBooksLikeRepository;

public interface UserBooksLikeComponent {

  UserBooksLikeRepository userBooksLikeRepository();

  GetAllUserBookLikesInteractor getAllUserBooksLikesInteractor();

  PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor();
}
