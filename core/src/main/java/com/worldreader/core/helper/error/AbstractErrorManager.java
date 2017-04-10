package com.worldreader.core.helper.error;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.worldreader.core.R;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.deprecated.error.exception.NetworkErrorException;
import com.worldreader.core.error.book.FailedDownloadBookException;
import com.worldreader.core.error.user.LoginException;
import com.worldreader.core.error.user.RegisterException;
import com.worldreader.core.error.user.RegisterWithFacebookException;
import com.worldreader.core.error.user.RegisterWithGoogleException;
import com.worldreader.core.error.user.UnAuthorizedUserException;

public abstract class AbstractErrorManager implements ErrorManager {

  protected final Context context;

  public AbstractErrorManager(final Context context) {
    this.context = context.getApplicationContext();
  }

  @Override public void perform(final Activity activity, final ErrorCore error, final int screen) {
    String message = "";
    final Throwable errorCause = error.getCause();
    if (errorCause instanceof NetworkErrorException) {
      if (!activity.isFinishing()) {
        onWarnUserExplicitly(activity, screen);
      }
      return;
    } else if (errorCause instanceof LoginException) {
      message = context.getString(R.string.ls_error_invalid_username_password_combination);
    } else if (errorCause instanceof RegisterException) {
      message = context.getString(R.string.ls_error_username_taken_message);
    } else if (errorCause instanceof RegisterWithGoogleException) {
      message = context.getString(R.string.ls_error_register_google_message);
    } else if (errorCause instanceof RegisterWithFacebookException) {
      message = context.getString(R.string.ls_error_register_facebook_message);
    } else if (errorCause instanceof UnAuthorizedUserException) {
      // Ignore it as the IntentFilter will take care of logging out the user
    } else if (errorCause instanceof FailedDownloadBookException) {
      message = context.getString(R.string.ls_error_common_network_message);
    } else {
      message = context.getString(R.string.ls_error_fatal_error_message);
    }

    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
  }

  protected abstract void onWarnUserExplicitly(final Activity activity, final int screen);

}
