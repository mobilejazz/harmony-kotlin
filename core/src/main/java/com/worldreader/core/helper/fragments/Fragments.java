package com.worldreader.core.helper.fragments;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Util class for performing common operations related to fragments.
 */
@SuppressWarnings("unused") public class Fragments {

  private Fragments() {
    throw new AssertionError("No instances of this class are allowed!");
  }

  public static void replaceNowAllowingStateLoss(final FragmentActivity activity, final Fragment f, @IdRes final int container) {
    try {
      replaceNow(activity, f, container);
    } catch (IllegalStateException e) {
      activity.getSupportFragmentManager().beginTransaction().replace(container, f).commitNowAllowingStateLoss();
    }
  }

  public static void replaceNow(final FragmentActivity activity, final Fragment f, @IdRes final int container) {
    replaceNow(activity.getSupportFragmentManager(), f, container);
  }

  public static void replaceNowAllowingStateLoss(final FragmentActivity activity, final Fragment f, @IdRes final int container, final String tag) {
    try {
      replaceNow(activity, f, container, tag);
    } catch (IllegalStateException e) {
      activity.getSupportFragmentManager().beginTransaction().replace(container, f, tag).commitNowAllowingStateLoss();
    }
  }

  public static void replaceNow(final FragmentActivity activity, final Fragment f, @IdRes final int container, final String tag) {
    replaceNow(activity.getSupportFragmentManager(), f, container, tag);
  }

  public static void replaceNow(final FragmentManager manager, final Fragment f, @IdRes final int container) {
    manager.beginTransaction().replace(container, f).commitNow();
  }

  public static void replaceNow(final FragmentManager manager, final Fragment f, @IdRes final int container, final String tag) {
    manager.beginTransaction().replace(container, f, tag).commitNow();
  }

  public static void replace(@NonNull final FragmentActivity activity, @NonNull final Fragment f, @IdRes final int container) {
    final FragmentManager fm = activity.getSupportFragmentManager();
    fm.beginTransaction().replace(container, f).commit();
  }

  @Nullable public static Fragment findByTag(final FragmentActivity activity, final String tag) {
    return activity.getSupportFragmentManager().findFragmentByTag(tag);
  }

  @Nullable public static Fragment findyById(final FragmentActivity activity, @IdRes final int id) {
    return activity.getSupportFragmentManager().findFragmentById(id);
  }

  public static void hideNow(final FragmentActivity activity, final Fragment f) {
    hideNow(activity.getSupportFragmentManager(), f);
  }

  public static void hideNow(final FragmentManager manager, final Fragment f) {
    manager.beginTransaction().hide(f).commitNow();
  }

  public static void showNow(final FragmentActivity activity, final Fragment f) {
    showNow(activity.getSupportFragmentManager(), f);
  }

  public static void showNow(final FragmentManager manager, final Fragment f) {
    manager.beginTransaction().show(f).commitNow();
  }

}