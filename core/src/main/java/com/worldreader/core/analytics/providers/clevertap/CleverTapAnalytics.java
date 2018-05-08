package com.worldreader.core.analytics.providers.clevertap;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.providers.clevertap.events.CleverTapAnalyticsEvent;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CleverTapAnalytics implements Analytics {

  private static final String TAG = CleverTapAnalytics.class.getSimpleName();

  private final CleverTapAPI clevertap;
  private final Logger logger;

  @Inject
  public CleverTapAnalytics(Context context, Logger logger) {
    try {
      this.clevertap = CleverTapAPI.getInstance(context);
    } catch (Exception e) {
      final RuntimeException exception = new RuntimeException(e);
      logger.e(TAG, "Error while initializing CleverTapAnalytics: " + Throwables.getStackTraceAsString(exception));
      throw exception;
    }
    this.logger = logger;
  }

  public static void registerEvents(Application app) {
    ActivityLifecycleCallback.register(app);
  }

  @Override public <T extends AnalyticsEvent> void sendEvent(@NonNull T event) {
    if (!(event instanceof CleverTapAnalyticsEvent)) {
      return;
    }

    final CleverTapAnalyticsEvent ev = (CleverTapAnalyticsEvent) event;

    clevertap.event.push(ev.id(), ev.actions());
  }

  @Override public void addGlobalProperties(Map<String, String> attributes) {
  }
}
