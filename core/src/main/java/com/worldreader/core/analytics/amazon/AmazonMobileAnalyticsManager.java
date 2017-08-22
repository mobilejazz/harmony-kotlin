package com.worldreader.core.analytics.amazon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.EventClient;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.internal.core.util.Preconditions;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.BasicAnalyticsEvent;
import org.json.JSONObject;

import java.util.*;

public class AmazonMobileAnalyticsManager implements AmazonMobileAnalytics {

  private MobileAnalyticsManager mobileAnalyticsManager;

  private final Context context;
  private final String cognitoId;
  private final String applicationId;
  private final Logger logger;
  private final static String TAG = "AmazonMobileAnalitycs";

  public AmazonMobileAnalyticsManager(final Context context, final String cognitoId, final String applicationId, Logger logger) {
    this.context = context;
    this.cognitoId = cognitoId;
    this.applicationId = applicationId;
    this.logger = logger;
    logger.d(TAG, "context: "+ this.context+ ", cognitoId: " + this.cognitoId+ ", applicationId: "+this.applicationId);
  }

  @Override public <T extends AnalyticsEvent> void sendEvent(@NonNull final T event) {
    Preconditions.checkNotNull(event, "event != null");
    Preconditions.checkArgument(event instanceof BasicAnalyticsEvent, "event != BasicAnalyticsEvent");

    final BasicAnalyticsEvent basicAnalyticsEvent = (BasicAnalyticsEvent) event;


    final EventClient eventClient = getMobileAnalyticsManager().getEventClient();
    final com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent analyticsEvent =
        eventClient.createEvent(basicAnalyticsEvent.getEventName());

    final Map<String, String> attributes = basicAnalyticsEvent.getMap();
    for (final String key : attributes.keySet()) {
      analyticsEvent.addAttribute(key, attributes.get(key));
    }

    JSONObject jLogger = new JSONObject(analyticsEvent.getAllAttributes());
    logger.d(TAG, "eventName: "+ basicAnalyticsEvent.getEventName()+ ", attributes: " + jLogger.toString());

    getMobileAnalyticsManager().getEventClient().recordEvent(analyticsEvent);
  }

  @Override public void onResume() {
    getMobileAnalyticsManager().getSessionClient().resumeSession();
    getMobileAnalyticsManager().getEventClient().submitEvents();
    //logger.d(TAG, "onResume() - sending event");
  }

  @Override public void onPause() {
    getMobileAnalyticsManager().getSessionClient().pauseSession();
  }

  @Override public void addGlobalProperties(final Map<String, String> attributes) {
    addGlobalProperties(null, attributes);
  }

  @Override
  public void addGlobalProperties(final String eventType, final Map<String, String> attributes) {
    final EventClient eventClient = getMobileAnalyticsManager().getEventClient();
    for (final String key : attributes.keySet()) {
      final String value = attributes.get(key);
      if (TextUtils.isEmpty(eventType)) {
        if (!TextUtils.isEmpty(value))
          eventClient.addGlobalAttribute(key, value);
      } else {
        eventClient.addGlobalAttribute(eventType, key, value);
      }
    }
  }

  private MobileAnalyticsManager getMobileAnalyticsManager() {

    if (mobileAnalyticsManager == null) {
      this.mobileAnalyticsManager =
          MobileAnalyticsManager.getOrCreateInstance(context, applicationId,cognitoId);
    }

    return mobileAnalyticsManager;
  }

}
