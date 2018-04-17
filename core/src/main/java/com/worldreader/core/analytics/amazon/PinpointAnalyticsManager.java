package com.worldreader.core.analytics.amazon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;

import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.internal.core.util.Preconditions;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.model.ChannelType;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.BasicAnalyticsEvent;
import org.json.JSONObject;

import java.util.*;

public class PinpointAnalyticsManager implements PinpointAnalytics {

  private PinpointManager pinpointAnalyticsManager;

  private final Context context;
  private final String cognitoId;
  private final String applicationId;
  private final Logger logger;
  private final static String TAG = "AmazonMobileAnalitycs";


  public PinpointAnalyticsManager(final Context context, final String cognitoId, final String applicationId, Logger logger) {
    this.context = context;
    this.cognitoId = cognitoId;
    this.applicationId = applicationId;
    this.logger = logger;
    logger.d(TAG, "context: " + this.context + ", cognitoId: " + this.cognitoId + ", applicationId: " + this.applicationId);

  }

  @Override public <T extends AnalyticsEvent> void sendEvent(@NonNull final T event) {
    Preconditions.checkNotNull(event, "event != null");
    Preconditions.checkArgument(event instanceof BasicAnalyticsEvent, "event != BasicAnalyticsEvent");

    final BasicAnalyticsEvent basicAnalyticsEvent = (BasicAnalyticsEvent) event;

    final AnalyticsClient eventClient = getPinpointManager().getAnalyticsClient();

    final com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent analyticsEvent = eventClient.createEvent(basicAnalyticsEvent.getEventName());

    final Map<String, String> attributes = basicAnalyticsEvent.getMap();
    for (final String key : attributes.keySet()) {
      analyticsEvent.addAttribute(key, attributes.get(key));
    }

    JSONObject jLogger = new JSONObject(analyticsEvent.getAllAttributes());
    logger.d(TAG, "eventName: " + basicAnalyticsEvent.getEventName() + ", attributes: " + jLogger.toString());

    getPinpointManager().getAnalyticsClient().recordEvent(analyticsEvent);
  }


  @Override public void onResume() {
    getPinpointManager().getSessionClient().resumeSession();
    getPinpointManager().getAnalyticsClient().submitEvents();
    //logger.d(TAG, "onResume() - sending event");
  }

  @Override public void onPause() {
    getPinpointManager().getSessionClient().pauseSession();
  }

  @Override public void addGlobalProperties(final HashMap<String, String> attributes) {
    addGlobalProperties(null, attributes);
  }


  @Override
  public void addGlobalProperties(final String eventType, final Map<String, String> attributes) {
    final AnalyticsClient eventClient = getPinpointManager().getAnalyticsClient();
    for (final String key : attributes.keySet()) {
      final String value = attributes.get(key);
      if (TextUtils.isEmpty(eventType)) {
        if (!TextUtils.isEmpty(value)) {
          eventClient.addGlobalAttribute(key, value);
        }
      } else {
        eventClient.addGlobalAttribute(eventType, key, value);
      }
    }
  }



  private PinpointManager getPinpointManager() {

    if (pinpointAnalyticsManager == null) {

      CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
          context,    /* get the context for the application */
          cognitoId,    /* Identity Pool ID */
          Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
      );
      PinpointConfiguration config = new PinpointConfiguration(context, applicationId, Regions.US_EAST_1, ChannelType.GCM, credentialsProvider);

      this.pinpointAnalyticsManager = new PinpointManager(config);

    }

    return pinpointAnalyticsManager;
  }

}
