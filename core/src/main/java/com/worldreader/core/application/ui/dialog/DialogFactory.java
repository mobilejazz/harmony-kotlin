package com.worldreader.core.application.ui.dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.widget.DatePicker;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.worldreader.core.R;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DialogFactory {

  public static final int EMPTY = -1;

  private DialogFactory() {
    throw new AssertionError("No instances of this class are allowed!");
  }

  public enum Action {
    OK, CANCEL
  }

  public interface ActionCallback {

    void onResponse(MaterialDialog dialog, Action action);
  }

  public interface CallbackDateDialog {

    void onSuccess(Date selectedDate);
  }

  public static ProgressDialog createProgressDialog(Context context, Activity activity, String message) {
    ProgressDialog d = new ProgressDialog(context);
    d.setOwnerActivity(activity);
    d.setOnKeyListener(new DialogInterface.OnKeyListener() {
      @Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return true;
      }
    });
    d.setMessage(message);
    return d;
  }

  public static MaterialDialog createDialog(Context context, @AnyRes int titleResId, @AnyRes int positiveResId, @AnyRes int negativeResId,
      final ActionCallback callback) {

    MaterialDialog.Builder builder;
    if (negativeResId == EMPTY) {
      builder = new MaterialDialog.Builder(context).content(titleResId).positiveText(positiveResId).callback(new MaterialDialog.ButtonCallback() {
        @Override public void onPositive(MaterialDialog dialog) {
          if (callback != null) {
            callback.onResponse(dialog, Action.OK);
          }
        }

        @Override public void onNegative(MaterialDialog dialog) {
          if (callback != null) {
            callback.onResponse(dialog, Action.CANCEL);
          }
        }
      });
    } else {
      builder = new MaterialDialog.Builder(context).content(titleResId)
          .negativeText(negativeResId)
          .negativeColorRes(android.R.color.holo_red_light)
          .positiveText(positiveResId)
          .callback(new MaterialDialog.ButtonCallback() {
            @Override public void onPositive(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.OK);
              }
            }

            @Override public void onNegative(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.CANCEL);
              }
            }
          });
    }

    return builder.build();
  }

  public static MaterialDialog createDialog(Context context, @AnyRes int titleResId, @AnyRes int messageResId, @AnyRes int positiveResId,
      @AnyRes int negativeResId, final ActionCallback callback) {

    MaterialDialog.Builder builder;
    if (negativeResId == EMPTY) {
      builder = new MaterialDialog.Builder(context).title(titleResId)
          .content(messageResId)
          .positiveText(positiveResId)
          .callback(new MaterialDialog.ButtonCallback() {
            @Override public void onPositive(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.OK);
              }
            }

            @Override public void onNegative(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.CANCEL);
              }
            }
          });
    } else {
      builder = new MaterialDialog.Builder(context).title(titleResId)
          .content(messageResId)
          .negativeText(negativeResId)
          .negativeColorRes(android.R.color.holo_red_light)
          .positiveText(positiveResId)
          .callback(new MaterialDialog.ButtonCallback() {
            @Override public void onPositive(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.OK);
              }
            }

            @Override public void onNegative(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.CANCEL);
              }
            }
          });
    }

    return builder.build();
  }

  public static MaterialDialog createDialog(Context context, @AnyRes int titleResId, String message, @AnyRes int positiveResId,
      @AnyRes int negativeResId, final ActionCallback callback) {

    MaterialDialog.Builder builder;
    if (negativeResId == EMPTY) {
      builder = new MaterialDialog.Builder(context).title(titleResId)
          .content(message)
          .positiveText(positiveResId)
          .callback(new MaterialDialog.ButtonCallback() {
            @Override public void onPositive(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.OK);
              }
            }

            @Override public void onNegative(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.CANCEL);
              }
            }
          });
    } else {
      builder = new MaterialDialog.Builder(context).title(titleResId)
          .content(message)
          .negativeText(negativeResId)
          .negativeColorRes(android.R.color.holo_red_light)
          .positiveText(positiveResId)
          .callback(new MaterialDialog.ButtonCallback() {
            @Override public void onPositive(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.OK);
              }
            }

            @Override public void onNegative(MaterialDialog dialog) {
              if (callback != null) {
                callback.onResponse(dialog, Action.CANCEL);
              }
            }
          });
    }

    return builder.build();
  }

  public static void createDateDialog(Context context, Date dateToStart, long minDate, long maxDate, final CallbackDateDialog callback) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(dateToStart);

    // TODO: 09/10/15 Fix to apply the color theme to the date dialog
    final DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date dateSelected = calendar.getTime();

        if (callback != null) {
          callback.onSuccess(dateSelected);
        }
      }
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    if (minDate > EMPTY) {
      datePickerDialog.getDatePicker().setMinDate(minDate);
    }

    if (maxDate > EMPTY) {
      datePickerDialog.getDatePicker().setMaxDate(maxDate);
    }

    datePickerDialog.show();
  }

  public static MaterialDialog createSetYourGoalsDialog(Context context, final ActionCallback callback) {
    Resources res = context.getResources();
    return new MaterialDialog.Builder(context).title(res.getString(R.string.ls_reader_set_your_goals_title))
        .content(res.getString(R.string.ls_reader_set_your_goals_message))
        .positiveText(android.R.string.ok)
        .callback(new MaterialDialog.ButtonCallback() {
          @Override public void onPositive(MaterialDialog dialog) {
            if (callback != null) {
              callback.onResponse(dialog, Action.OK);
            }
          }
        })
        .build();
  }

  public static MaterialDialog createSetYourGoalsReminderDialog(Context context, final ActionCallback callback) {
    Resources res = context.getResources();
    return new MaterialDialog.Builder(context).title(res.getString(R.string.ls_reader_reminder_set_your_goals_title))
        .content(res.getString(R.string.ls_reader_reminder_set_your_goals_message))
        .positiveText(android.R.string.ok)
        .callback(new MaterialDialog.ButtonCallback() {
          @Override public void onAny(MaterialDialog dialog) {
            if (callback != null) {
              callback.onResponse(dialog, Action.OK);
            }
          }
        })
        .build();
  }

  public static MaterialDialog createDownloadBookProgressDialog(final Context context) {
    return new MaterialDialog.Builder(context).content(context.getString(R.string.ls_book_detail_download_book))
        .progress(false, 100, true)
        .cancelable(false)
        .build();
  }

  public static MaterialDialog createProgressDialog(final Context context, @StringRes final int title, @StringRes final int content) {
    final MaterialDialog.Builder builder = new MaterialDialog.Builder(context).content(content).progress(true, 0).cancelable(false);
    if (title != 0) {
      builder.title(title);
    }
    return builder.build();

  }

  public static MaterialDialog createLocalLibrarySentSuccessfullyDialog(final Context context, @StringRes int message,
      final ActionCallback callback) {
    return new MaterialDialog.Builder(context).content(message)
        .positiveText(R.string.ls_generic_accept)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            callback.onResponse(dialog, Action.OK);
          }
        })
        .positiveText(android.R.string.ok)
        .callback(new MaterialDialog.ButtonCallback() {
          @Override public void onPositive(MaterialDialog dialog) {
            if (callback != null) {
              callback.onResponse(dialog, Action.OK);
            }
          }
        })
        .build();
  }
}
