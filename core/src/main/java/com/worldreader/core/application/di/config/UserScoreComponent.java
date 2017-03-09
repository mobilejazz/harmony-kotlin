package com.worldreader.core.application.di.config;

import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.interactors.user.score.UserScoreSynchronizationProcessInteractor;
import com.worldreader.core.domain.model.user.UserScore;
import com.worldreader.core.domain.repository.UserScoreRepository;

public interface UserScoreComponent {

  Repository<UserScore, RepositorySpecification> provideUserScoreRepository();

  UserScoreRepository userScoreRepository();

  UserScoreSynchronizationProcessInteractor userScoreSynchronizationProcessInteractor();

}
