package com.worldreader.reader.wr.helper;

import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;

import java.util.*;

public class LayoutDirectionHelper {

  private LayoutDirectionHelper() {
  }

  public static boolean isAppLayoutRTL() {
    return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
  }

}
