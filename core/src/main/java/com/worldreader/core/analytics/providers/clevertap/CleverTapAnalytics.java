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
import com.worldreader.core.analytics.providers.clevertap.helper.CleverTapEventConstants;
import com.worldreader.core.analytics.providers.clevertap.mappers.CleverTapAnalyticsEventMappers;
import com.worldreader.core.analytics.providers.clevertap.mappers.CleverTapAnalyticsMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class CleverTapAnalytics implements Analytics {

  private static final String TAG = CleverTapAnalytics.class.getSimpleName();

  private final CleverTapAPI clevertap;
  private final CleverTapAnalyticsEventMappers mappers;
  private final Logger logger;

  @Inject public CleverTapAnalytics(Context context, Logger logger) {
    try {
      this.clevertap = CleverTapAPI.getInstance(context);
      this.logger = logger;
    } catch (Exception e) {
      final RuntimeException exception = new RuntimeException(e);
      logger.e(TAG, "Error while initializing CleverTapAnalytics: " + Throwables.getStackTraceAsString(exception));
      throw exception;
    }
    this.mappers = new CleverTapAnalyticsEventMappers(context);
  }

  public static void registerEvents(Application app) {
    ActivityLifecycleCallback.register(app);
  }

  @Override public <T extends AnalyticsEvent> void sendEvent(@NonNull T event) {
    final CleverTapAnalyticsMapper mapper = mappers.obtain(event.getClass());
    if (mapper != null) {
      @SuppressWarnings("unchecked") final Map<String, Object> eventActions = mapper.transform(event);
      final String eventName = ((String) eventActions.get(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME));
      eventActions.remove(CleverTapEventConstants.CLEVERTAP_KEY_EVENT_NAME);
      String printable = new String();
      for (Map.Entry<String, Object> entry : eventActions.entrySet()) {
        if(!entry.getKey().isEmpty() && entry.getValue()!=null){
          printable += (entry.getKey() + ": " + entry.getValue().toString())+"\n";
        }
      }
      logger.d(TAG, "CleverTap eventName: " + eventName + ", attributes: \n" + printable);
      clevertap.event.push(eventName, eventActions);

    }
  }

  public <T extends AnalyticsEvent> void sendProfile(@NonNull T event) {
    final CleverTapAnalyticsMapper mapper = mappers.obtain(event.getClass());
    if (mapper != null) {
      @SuppressWarnings("unchecked") final Map<String, Object> profile = mapper.transform(event);
      String printable = new String();
      for (Map.Entry<String, Object> entry : profile.entrySet()) {
        if(!entry.getKey().isEmpty() && entry.getValue()!=null){
          printable += (entry.getKey() + ": " + entry.getValue().toString())+"\n";
        }
      }
      logger.d(TAG, "CleverTap PROFILE, attributes: \n" + printable);
      clevertap.profile.push(profile);
    }
  }

  @Override public void addGlobalProperties(Map<String, String> attributes) {
    // For now CleverTap doesn't need this properties
  }

  @Override public void onStart(){

  }
}
