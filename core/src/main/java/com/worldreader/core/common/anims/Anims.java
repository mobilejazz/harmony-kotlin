package com.worldreader.core.common.anims;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.worldreader.core.common.intents.Intents;

public class Anims {

  public static final String CX = "anim.cx";
  public static final String CY = "anim.cy";
  public static final String RADIUS = "anim.radius";

  public interface OnRevealAnimationListener {

    void onRevealHide();

    void onRevealShow();
  }

  private Anims() {
    throw new AssertionError("No instances!");
  }

  public static Intent createViewCoordinatesIntent(@NonNull View view) {

    int[] viewLocation = new int[2];
    view.getLocationOnScreen(viewLocation);

    final int cx = viewLocation[0] + view.getWidth() / 2;
    final int cy = viewLocation[1] + view.getHeight() / 2;
    final int radius = view.getWidth() / 2;

    return Intents.create().putExtra(CX, cx).putExtra(CY, cy).putExtra(RADIUS, radius).build();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public static void circularRevealShow(final Context ctx, final View view,
      @ColorRes final int initialColorRes, @ColorRes final int finalColorRes, int x, int y,
      final int startRadius, final OnRevealAnimationListener listener) {

    final float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
    final int colorInitial = ContextCompat.getColor(ctx, initialColorRes);
    final int colorFinal = ContextCompat.getColor(ctx, finalColorRes);

    Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, finalRadius);
    anim.setDuration(300);
    anim.setInterpolator(new AccelerateDecelerateInterpolator());
    anim.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        view.setBackgroundColor(colorInitial);
      }

      @Override public void onAnimationEnd(Animator animation) {
        view.setVisibility(View.VISIBLE);
        listener.onRevealShow();
      }
    });

    ValueAnimator colorAnim = ObjectAnimator.ofFloat(0f, 1f);
    colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float mul = (Float) animation.getAnimatedValue();
        int alphaColor = adjustAlpha(colorFinal, mul);
        view.getBackground().setColorFilter(alphaColor, PorterDuff.Mode.SRC_ATOP);
      }
    });
    colorAnim.setInterpolator(new AccelerateDecelerateInterpolator());
    colorAnim.setStartDelay(50);
    colorAnim.setDuration(650);

    AnimatorSet animationSet = new AnimatorSet();
    animationSet.playTogether(anim, colorAnim);
    animationSet.start();
  }

  //region Private methods

  private static int adjustAlpha(int color, float factor) {
    int alpha = Math.round(Color.alpha(color) * factor);
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    return Color.argb(alpha, red, green, blue);
  }

  //endregion
}
