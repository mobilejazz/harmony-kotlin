package com.worldreader.core.application.helper.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

public class Dimens {

  private Dimens() {
    throw new AssertionError("No instances of this class are allowed!");
  }

  public static int dpToPx(Context c, int dp) {
    DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
  }

  public static int obtainNavBarHeight(Context c) {
    int result = 0;
    boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean isGenymotion =
        Build.MANUFACTURER != null && Build.MANUFACTURER.toLowerCase().equals("genymotion");

    if (isGenymotion || !hasMenuKey && !hasBackKey) {
      //The device has a navigation bar
      Resources resources = c.getResources();
      int orientation = resources.getConfiguration().orientation;
      int resourceId;

      if (isTablet(c)) {
        resourceId = resources.getIdentifier(
            orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height"
                                                              : "navigation_bar_height_landscape", "dimen", "android");
      } else {
        resourceId = resources.getIdentifier(
            orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height"
                                                              : "navigation_bar_width", "dimen", "android");
      }

      if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId);
      }
    }

    return result;
  }

  private static boolean isTablet(Context c) {
    return (c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
        >= Configuration.SCREENLAYOUT_SIZE_LARGE;
  }
}
