package com.worldreader.core.domain.repository;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.Repository;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.domain.model.LeaderboardStat;
import com.worldreader.core.domain.model.user.RegisterProvider;
import com.worldreader.core.domain.model.user.RegisterProviderData;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserReadingStats;

import java.util.*;

public interface UserRepository extends Repository<User2, RepositorySpecification> {

  enum LeaderboardPeriod {
    GLOBAL,
    MONTHLY,
    WEEKLY
  }

  void register(RegisterProvider provider, RegisterProviderData<?> registerProviderData, Callback<Optional<User2>> callback);

  void resetPassword(String email, Callback<Optional<Boolean>> callback);

  void updateGoals(int pagesPerDay, int minChildAge, int maxChildAge, Callback<Optional<User2>> callback);

  void leaderboardStats(LeaderboardPeriod period, Callback<Optional<LeaderboardStat>> callback);

  void readingStats(Date from, Date to, Callback<Optional<UserReadingStats>> callback);

  void updateReadingStats(String bookId, int readPages, Date when, Callback<Optional<Boolean>> callback);

  void updateProfilePicture(final String profilePictureId, final Callback<Void> callback);

  void updateBirthdate(final Date birthDate, final Callback<Void> callback);

  void updateEmail(final String email, final Callback<Void> callback);

  void updateName(final String name, final Callback<Void> callback);

  void sendLocalLibrary(String localLibrary, final Callback<Boolean> callback);
}
