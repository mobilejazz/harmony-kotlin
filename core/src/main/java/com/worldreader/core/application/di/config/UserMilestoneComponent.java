package com.worldreader.core.application.di.config;

import com.worldreader.core.datasource.network.datasource.milestones.UserMilestonesNetworkDataSource;
import com.worldreader.core.domain.interactors.user.milestones.GetAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.GetUnsyncUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesNetworkInteractor;
import com.worldreader.core.domain.repository.UserMilestonesRepository;

public interface UserMilestoneComponent {

  UserMilestonesNetworkDataSource userMilestoneNetworkDataSource();

  UserMilestonesRepository userMilestoneRepositoryCasted();

  GetAllUserMilestonesInteractor getAllUSerMilestonesInteractor();

  PutAllUserMilestonesNetworkInteractor putAllUserMilestonesNetworkInteractor();

  PutAllUserMilestonesInteractor putAllUserMilestonesInteractor();

  GetUnsyncUserMilestonesInteractor getUnsyncUserMilestonesInteractor();
}
