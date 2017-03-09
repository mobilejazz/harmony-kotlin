package com.worldreader.core.datasource.network.datasource.user;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.model.LeaderboardPeriodEntity;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.model.user.UserReadingStatsEntity;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.network.model.RegisterProviderDataNetwork;
import com.worldreader.core.datasource.network.model.RegisterProviderNetwork;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.*;

public interface UserNetworkDataSource2
    extends Repository.Network<UserEntity2, RepositorySpecification> {

  void register(RegisterProviderNetwork provider,
      RegisterProviderDataNetwork<?> registerProviderData,
      Callback<Optional<UserEntity2>> callback);

  void resetPassword(String email, Callback<Optional<Boolean>> callback);

  void updateGoals(int pagesPerDay, int minChildAge, int maxChildAge,
      Callback<Optional<UserEntity2>> callback);

  void leaderboardStats(LeaderboardPeriodEntity leaderboardPeriodEntity,
      Callback<Optional<LeaderboardStatEntity>> callback);

  void readingStats(Date from, Date to, Callback<Optional<UserReadingStatsEntity>> callback);

  void updateReadingStats(String bookId, int readPages, Date when,
      Callback<Optional<Boolean>> callback);

  void updatePoints(int points, Callback<Optional<Boolean>> callback);

  void updateProfilePicture(final String profilePictureId, final Callback<Void> callback);

  void updateBirthdate(final Date birthDate, final Callback<Void> callback);

  void updateEmail(final String email, final Callback<Void> callback);

  void updateName(final String name, final Callback<Void> callback);
}
