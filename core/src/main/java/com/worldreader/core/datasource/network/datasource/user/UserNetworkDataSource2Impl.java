package com.worldreader.core.datasource.network.datasource.user;

import android.content.Context;
import com.google.common.base.Optional;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.common.helper.HttpStatus;
import com.worldreader.core.datasource.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardPeriodEntity;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.model.user.UserReadingStatsEntity;
import com.worldreader.core.datasource.model.user.user.UserEntity2;
import com.worldreader.core.datasource.network.general.retrofit.adapter.Retrofit2ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.error.WorldreaderErrorAdapter2;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.datasource.network.general.retrofit.services.AuthApiService2;
import com.worldreader.core.datasource.network.general.retrofit.services.UserApiService2;
import com.worldreader.core.datasource.network.model.GoogleProviderDataNetwork;
import com.worldreader.core.datasource.network.model.LeaderboardStatNetwork;
import com.worldreader.core.datasource.network.model.RegisterProviderDataNetwork;
import com.worldreader.core.datasource.network.model.RegisterProviderNetwork;
import com.worldreader.core.datasource.network.model.ResetPasswordNetworkBody;
import com.worldreader.core.datasource.network.model.ResetPasswordResponse;
import com.worldreader.core.datasource.network.model.UpdateReadingStatsNetworkBody;
import com.worldreader.core.datasource.network.model.UpdateUserFavoriteCategoriesNetworkBody;
import com.worldreader.core.datasource.network.model.UserBirthdDateNetworkBody;
import com.worldreader.core.datasource.network.model.UserEmailNetworkBody;
import com.worldreader.core.datasource.network.model.UserFacebookRegisterBody;
import com.worldreader.core.datasource.network.model.UserGoalsBody;
import com.worldreader.core.datasource.network.model.UserGoogleRegisterBody;
import com.worldreader.core.datasource.network.model.UserNameNetworkBody;
import com.worldreader.core.datasource.network.model.UserNetworkResponse;
import com.worldreader.core.datasource.network.model.UserPictureNetworkBody;
import com.worldreader.core.datasource.network.model.UserReadingStatNetworkBody;
import com.worldreader.core.datasource.network.model.UserReadingStatsNetworkResponse;
import com.worldreader.core.datasource.network.model.UserRegisterBody;
import com.worldreader.core.datasource.network.model.WorldreaderProviderDataNetwork;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;
import com.worldreader.core.datasource.spec.user.UpdateUserCategoriesSpecification;
import com.worldreader.core.error.user.RegisterException;
import retrofit2.Response;

import java.io.*;
import java.util.*;

public class UserNetworkDataSource2Impl implements UserNetworkDataSource2 {

  private final UserApiService2 apiService;
  private final AuthApiService2 authApiService;

  private final Mapper<Optional<UserNetworkResponse>, Optional<UserEntity2>> toUserEntityMapper;
  private final Mapper<Optional<LeaderboardPeriodEntity>, Optional<String>>
      toLeaderBoardStringMapper;
  private final Mapper<Optional<LeaderboardStatNetwork>, Optional<LeaderboardStatEntity>>
      toLeaderboardStatEntityMapper;
  private final Mapper<Optional<UserReadingStatsNetworkResponse>, Optional<UserReadingStatsEntity>>
      toUserReadingStatsEntityMapper;

  private final ErrorAdapter<Throwable> errorAdapter;

  private final Logger logger;

  public UserNetworkDataSource2Impl(Context context, UserApiService2 apiService,
      AuthApiService2 authApiService,
      Mapper<Optional<UserNetworkResponse>, Optional<UserEntity2>> toUserEntityMapper,
      Mapper<Optional<LeaderboardPeriodEntity>, Optional<String>> toLeaderBoardStringMapper,
      Mapper<Optional<LeaderboardStatNetwork>, Optional<LeaderboardStatEntity>> toLeaderboardStatEntityMapper,
      final Mapper<Optional<UserReadingStatsNetworkResponse>, Optional<UserReadingStatsEntity>> toUserReadingStatsEntityMapper,
      Logger logger) {
    this.apiService = apiService;
    this.authApiService = authApiService;
    this.toUserEntityMapper = toUserEntityMapper;
    this.toLeaderBoardStringMapper = toLeaderBoardStringMapper;
    this.toLeaderboardStatEntityMapper = toLeaderboardStatEntityMapper;
    this.toUserReadingStatsEntityMapper = toUserReadingStatsEntityMapper;
    this.errorAdapter = new WorldreaderErrorAdapter2(context, new Retrofit2ErrorAdapter(), logger);
    this.logger = logger;
  }

  @Override public void get(RepositorySpecification specification,
      final Callback<Optional<UserEntity2>> callback) {
    try {
      final Response<UserNetworkResponse> response = apiService.user().execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserNetworkResponse userNetwork = response.body();
        final Optional<UserEntity2> toReturn =
            toUserEntityMapper.transform(Optional.fromNullable(userNetwork));
        notifySuccessResponse(callback, toReturn);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void getAll(final RepositorySpecification specification,
      final Callback<Optional<List<UserEntity2>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void put(UserEntity2 model, RepositorySpecification specification,
      final Callback<Optional<UserEntity2>> callback) {
    if (specification instanceof UpdateUserCategoriesSpecification) {
      updateUserCategories(((UpdateUserCategoriesSpecification) specification), callback);
    } else {
      throw new IllegalArgumentException("specification not registered!");
    }
  }

  private void updateUserCategories(UpdateUserCategoriesSpecification specification,
      final Callback<Optional<UserEntity2>> callback) {
    final UpdateUserFavoriteCategoriesNetworkBody body =
        new UpdateUserFavoriteCategoriesNetworkBody(specification.getCategories());
    try {
      final Response<UserNetworkResponse> response =
          apiService.updateFavoriteCategories(body).execute();
      if (response.isSuccessful()) {
        final UserNetworkResponse userNetwork = response.body();
        final Optional<UserEntity2> toReturn =
            toUserEntityMapper.transform(Optional.fromNullable(userNetwork));
        notifySuccessResponse(callback, toReturn);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override
  public void putAll(List<UserEntity2> userEntity2s, RepositorySpecification specification,
      Callback<Optional<List<UserEntity2>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void remove(UserEntity2 model, RepositorySpecification specification,
      Callback<Optional<UserEntity2>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override
  public void removeAll(List<UserEntity2> userEntity2s, RepositorySpecification specification,
      Callback<Optional<List<UserEntity2>>> callback) {
    throw new IllegalStateException("Not implemented!");
  }

  @Override public void register(RegisterProviderNetwork provider,
      RegisterProviderDataNetwork<?> registerProviderData,
      Callback<Optional<UserEntity2>> callback) {
    final Object rawData = registerProviderData.get();
    switch (provider) {
      case FACEBOOK:
        registerUserWithFacebook(rawData, callback);
        break;
      case GOOGLE:
        registerUserWithGoogle(rawData, callback);
        break;
      case WORLDREADER:
        registerUserWithWorldreader(rawData, callback);
        break;
    }
  }

  private void registerUserWithFacebook(Object data,
      final Callback<Optional<UserEntity2>> callback) {
    final String facebookToken = (String) data;
    final UserFacebookRegisterBody body = UserFacebookRegisterBody.create(facebookToken);
    try {
      final Response<Void> response = authApiService.registerWithFacebook(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, Optional.<UserEntity2>absent());
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  private void registerUserWithGoogle(Object data, final Callback<Optional<UserEntity2>> callback) {
    final GoogleProviderDataNetwork.NetworkGoogleRegisterData registerData =
        (GoogleProviderDataNetwork.NetworkGoogleRegisterData) data;
    final UserGoogleRegisterBody body =
        UserGoogleRegisterBody.create(registerData.getGoogleId(), registerData.getName(),
            registerData.getEmail());
    try {
      final Response<Void> response = authApiService.registerWithGoogle(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, Optional.<UserEntity2>absent());
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  private void registerUserWithWorldreader(Object data,
      final Callback<Optional<UserEntity2>> callback) {
    final WorldreaderProviderDataNetwork.NetworkWorldreaderRegisterData registerData =
        (WorldreaderProviderDataNetwork.NetworkWorldreaderRegisterData) data;
    final UserRegisterBody body =
        UserRegisterBody.create(registerData.getUsername(), registerData.getPassword(),
            registerData.getEmail());
    try {
      final Response<UserNetworkResponse> response = apiService.register(body).execute();
      final boolean successful = response.isSuccessful();
      final int code = response.code();
      if (successful) {
        final UserNetworkResponse userNetwork = response.body();
        final Optional<UserEntity2> toReturn =
            toUserEntityMapper.transform(Optional.fromNullable(userNetwork));
        notifySuccessResponse(callback, toReturn);
      } else if (code == HttpStatus.BAD_REQUEST) { // User already registered
        notifyErrorResponse(callback, new RegisterException());
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void resetPassword(String email, final Callback<Optional<Boolean>> callback) {
    final ResetPasswordNetworkBody body = new ResetPasswordNetworkBody(email);
    try {
      final Response<ResetPasswordResponse> response = apiService.resetPassword(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final ResetPasswordResponse resetPasswordResponse = response.body();
        final Optional<Boolean> toReturn = Optional.of(resetPasswordResponse.isStatus());
        notifySuccessResponse(callback, toReturn);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void updateGoals(int pagesPerDay, int minChildAge, int maxChildAge,
      final Callback<Optional<UserEntity2>> callback) {
    final UserGoalsBody body = new UserGoalsBody(pagesPerDay, minChildAge, maxChildAge);
    try {
      final Response<UserNetworkResponse> response = apiService.updateGoals(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserNetworkResponse userNetwork = response.body();
        final Optional<UserEntity2> toReturn =
            toUserEntityMapper.transform(Optional.fromNullable(userNetwork));
        notifySuccessResponse(callback, toReturn);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void leaderboardStats(LeaderboardPeriodEntity leaderboardPeriodEntity,
      final Callback<Optional<LeaderboardStatEntity>> callback) {
    final String period =
        toLeaderBoardStringMapper.transform(Optional.fromNullable(leaderboardPeriodEntity)).get();
    try {
      final Response<LeaderboardStatNetwork> response = apiService.leaderboards(period).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final LeaderboardStatNetwork leaderboardStatNetwork = response.body();
        final Optional<LeaderboardStatEntity> toReturn =
            toLeaderboardStatEntityMapper.transform(Optional.fromNullable(leaderboardStatNetwork));
        notifySuccessResponse(callback, toReturn);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void readingStats(final Date from, final Date to,
      final Callback<Optional<UserReadingStatsEntity>> callback) {
    final UserReadingStatNetworkBody body = new UserReadingStatNetworkBody(from, to);
    try {
      final Response<UserReadingStatsNetworkResponse> response =
          apiService.readingStats(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        final UserReadingStatsNetworkResponse userReadingStatsNetworkResponse = response.body();
        final Optional<UserReadingStatsEntity> toReturn = toUserReadingStatsEntityMapper.transform(
            Optional.fromNullable(userReadingStatsNetworkResponse));
        notifySuccessResponse(callback, toReturn);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void updateReadingStats(String bookId, int readPages, Date when,
      Callback<Optional<Boolean>> callback) {
    final UpdateReadingStatsNetworkBody body =
        UpdateReadingStatsNetworkBody.create(bookId, readPages, when);
    try {
      final Response<Void> response = apiService.updateReadingStats(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, Optional.of(true));
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void updatePoints(final int points, final Callback<Optional<Boolean>> callback) {
    //final UserPointsNetworkBody body = new UserPointsNetworkBody(points);
    //try {
    //  final Response<Void> response = apiService.updatePoints(body).execute();
    //  final boolean successful = response.isSuccessful();
    //  if (successful) {
    //    notifySuccessResponse(callback, Optional.of(true));
    //  } else {
    //    final Retrofit2Error httpError = Retrofit2Error.httpError(response);
    //    final ErrorCore<?> errorCore = mapToErrorCore(httpError);
    //    notifyErrorResponse(callback, errorCore.getCause());
    //  }
    //} catch (IOException e) {
    //  final ErrorCore<?> errorCore = mapToErrorCore(e);
    //  notifyErrorResponse(callback, errorCore.getCause());
    //}
  }

  @Override
  public void updateProfilePicture(final String profilePictureId, final Callback<Void> callback) {
    final UserPictureNetworkBody body = new UserPictureNetworkBody(profilePictureId);
    try {
      final Response<Void> response = apiService.updateUserPicture(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, null);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void updateBirthdate(final Date birthDate, final Callback<Void> callback) {
    final UserBirthdDateNetworkBody body = new UserBirthdDateNetworkBody(birthDate);
    try {
      final Response<Void> response = apiService.updateBirthdate(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, null);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void updateEmail(final String email, final Callback<Void> callback) {
    final UserEmailNetworkBody body = new UserEmailNetworkBody(email);
    try {
      final Response<Void> response = apiService.updateEmail(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, null);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  @Override public void updateName(final String name, final Callback<Void> callback) {
    final UserNameNetworkBody body = new UserNameNetworkBody(name);
    try {
      final Response<Void> response = apiService.updateName(body).execute();
      final boolean successful = response.isSuccessful();
      if (successful) {
        notifySuccessResponse(callback, null);
      } else {
        final Retrofit2Error httpError = Retrofit2Error.httpError(response);
        final ErrorCore<?> errorCore = mapToErrorCore(httpError);
        notifyErrorResponse(callback, errorCore.getCause());
      }
    } catch (IOException e) {
      final ErrorCore<?> errorCore = mapToErrorCore(e);
      notifyErrorResponse(callback, errorCore.getCause());
    }
  }

  private <T> void notifySuccessResponse(Callback<T> callback, T response) {
    if (callback != null) {
      callback.onSuccess(response);
    }
  }

  private void notifyErrorResponse(Callback<?> callback, Throwable error) {
    if (callback != null) {
      callback.onError(error);
    }
  }

  private ErrorCore<?> mapToErrorCore(Throwable throwable) {
    return errorAdapter.of(throwable);
  }

}
