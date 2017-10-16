package com.worldreader.core.domain.interactors.user;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.repository.spec.NetworkSpecification;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.*;

@PerActivity public class UpdateUserProfileProcessInteractor {

  private final ListeningExecutorService executor;
  private final GetUserInteractor getUserInteractor;
  private final SaveUserInteractor saveUserInteractor;
  private final UserRepository repository;

  @Inject public UpdateUserProfileProcessInteractor(final ListeningExecutorService executor,
      final GetUserInteractor getUserInteractor, final SaveUserInteractor saveUserInteractor,
      final UserRepository repository) {
    this.executor = executor;
    this.getUserInteractor = getUserInteractor;
    this.saveUserInteractor = saveUserInteractor;
    this.repository = repository;
  }

  public ListenableFuture<User2> execute(final User2 userToUpdate) {
    final SettableFuture<User2> future = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {

        final UserStorageSpecification spec =
            new UserStorageSpecification(UserStorageSpecification.UserTarget.LOGGED_IN);
        final ListenableFuture<User2> getUserFuture =
            getUserInteractor.execute(spec, MoreExecutors.directExecutor());

        final User2 user = getUserFuture.get();

        // Compare photo ids and update if necessary // repository threading is on the same thread so we don't have to worry to wait for it
        final String newPictureId = userToUpdate.getPicture();
        final String oldPictureId = user.getPicture();
        if (!TextUtils.isEmpty(newPictureId) && !newPictureId.equals(oldPictureId)) {
          repository.updateProfilePicture(newPictureId, new Callback<Void>() {
            @Override public void onSuccess(final Void aVoid) {
              // We don't care about this response
            }

            @Override public void onError(final Throwable e) {
              // We ignore this error
            }
          });
        }

        // Compare email and update if necessary // repository threading is on the same thread so we don't have to worry to wait for it
        final String newEmail = userToUpdate.getEmail();
        final String oldEmail = user.getEmail();
        if (TextUtils.isEmpty(oldEmail)) { // We only want to allow users without e-mail to add one.
          if (!TextUtils.isEmpty(newEmail) && !newEmail.equals(oldEmail)) {
            repository.updateEmail(newEmail, new Callback<Void>() {
              @Override public void onSuccess(final Void aVoid) {
                // We don't care about this response
              }

              @Override public void onError(final Throwable e) {
                future.setException(e);
              }
            });
          }
        }

        // Compare birthdate and update accordingly // repository threading is on the same thread so we don't have to worry to wait for it
        final Date newBirthDate = userToUpdate.getBirthDate();
        final Date oldBirthDate = user.getBirthDate();
        if (newBirthDate != null && (oldBirthDate == null
            || newBirthDate.getTime() != oldBirthDate.getTime())) {
          repository.updateBirthdate(newBirthDate, new Callback<Void>() {
            @Override public void onSuccess(final Void aVoid) {
              // We don't care about this response
            }

            @Override public void onError(final Throwable e) {
              // We ignore this error
            }
          });
        }

        // Update name
        final String newName = userToUpdate.getName();
        final String oldName = user.getName();
        if (!TextUtils.isEmpty(newName) && !newName.equals(oldName)) {
          repository.updateName(newName, new Callback<Void>() {
            @Override public void onSuccess(final Void aVoid) {
              // We don't care about this response
            }

            @Override public void onError(final Throwable e) {
              // We ignore this error
            }
          });
        }

        // Let's obtain the updated version of the user and save it to the database

        final NetworkSpecification networkSpec = new NetworkSpecification();
        final ListenableFuture<User2> getUserNetworkFuture =
            getUserInteractor.execute(networkSpec, MoreExecutors.directExecutor());

        final ListenableFuture<User2> saveUpdatedUserFuture =
            Futures.transformAsync(getUserNetworkFuture, new AsyncFunction<User2, User2>() {
              @Override public ListenableFuture<User2> apply(@Nullable final User2 input)
                  throws Exception {
                return saveUserInteractor.execute(input, SaveUserInteractor.Type.LOGGED_IN);
              }
            });

        Futures.addCallback(saveUpdatedUserFuture, new FutureCallback<User2>() {
          @Override public void onSuccess(@Nullable final User2 result) {
            future.set(result);
          }

          @Override public void onFailure(final Throwable t) {
            future.setException(t);
          }
        });
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        future.setException(t);
      }
    });

    return future;
  }

}
