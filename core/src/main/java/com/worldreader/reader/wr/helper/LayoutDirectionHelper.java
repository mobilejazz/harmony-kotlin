package com.worldreader.reader.wr.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import com.worldreader.core.R;

import java.util.*;

public class LayoutDirectionHelper {

  private LayoutDirectionHelper() {
  }

  private static boolean isSystemLayoutRTL(Context context) {
    return context.getResources().getBoolean(R.bool.is_layout_direction_rtl);
  }

  public static boolean isAppLayoutRTL() {
    return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
  }

  public static Drawable mirrorDrawableIfNeeded(Context context, Drawable drawable) {
    Drawable mirroredDrawable = drawable;

    if (isAppLayoutRTL() && !isSystemLayoutRTL(context)) {
      mirroredDrawable = getRotatedDrawable(drawable, 180);
    }

    return mirroredDrawable;
  }



  private static Drawable getRotatedDrawable(final Drawable d, final float angle) {
    final Drawable[] arD = { d };
    return new LayerDrawable(arD) {
      @Override
      public void draw(final Canvas canvas) {
        canvas.save();
        canvas.rotate(angle, d.getBounds().width() / 2, d.getBounds().height() / 2);
        super.draw(canvas);
        canvas.restore();
      }
    };
  }

}
