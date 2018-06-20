package com.worldreader.core.analytics.providers.pinpoint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.model.ChannelType;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.GenericAnalyticsEvent;
import com.worldreader.core.analytics.providers.pinpoint.mappers.PinpointAnalyticsEventMappers;
import com.worldreader.core.analytics.providers.pinpoint.mappers.PinpointAnalyticsMapper;
import org.json.JSONObject;

import java.util.*;

public class PinpointAnalytics implements Analytics {

  private final static String TAG = "AmazonMobileAnalytics";

  private static PinpointManager INSTANCE;

  private final Context context;
  private final String cognitoId;
  private final String applicationId;
  private final Logger logger;
  private final AnalyticsClient analyticsClient;
  private final PinpointAnalyticsEventMappers mappers;

  public PinpointAnalytics(final Context context, final String cognitoId, final String applicationId, Logger logger) {
    this.context = context;
    this.cognitoId = cognitoId;
    this.applicationId = applicationId;
    this.logger = logger;
    this.analyticsClient = getPinpointManager().getAnalyticsClient();
    this.mappers = new PinpointAnalyticsEventMappers(analyticsClient);
    this.logger.d(TAG, "context: " + context + ", cognitoId: " + cognitoId + ", applicationId: " + applicationId);
  }

  @Override public <T extends AnalyticsEvent> void sendEvent(@NonNull final T event) {
    // TODO: Remove once all GenericAnalyticsEvent are refactored
    if (event instanceof GenericAnalyticsEvent) {
      final GenericAnalyticsEvent simpleAnalyticsEvent = (GenericAnalyticsEvent) event;

      final AnalyticsClient eventClient = getPinpointManager().getAnalyticsClient();
      final com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent analyticsEvent = eventClient.createEvent(simpleAnalyticsEvent.getEventName());
      final Map<String, Object> attributes = simpleAnalyticsEvent.getValues();

      for (final String key : attributes.keySet()) {
        final String attr = ((String) attributes.get(key));
        analyticsEvent.addAttribute(key, attr);
      }

      final JSONObject jLogger = new JSONObject(analyticsEvent.getAllAttributes());
      logger.d(TAG, "eventName: " + simpleAnalyticsEvent.getEventName() + ", attributes: " + jLogger.toString());
      getPinpointManager().getAnalyticsClient().recordEvent(analyticsEvent);
      return;
    }
    // TODO: End of removal

    final PinpointAnalyticsMapper mapper = mappers.obtain(event.getClass());
    if (mapper != null) {
      @SuppressWarnings("unchecked") final com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent eventActions = mapper.transform(event);
      final JSONObject jLogger = new JSONObject(eventActions.getAllAttributes());
      logger.d(TAG, "eventName: " + eventActions.toString() + ", attributes: " + jLogger.toString());
      getPinpointManager().getAnalyticsClient().recordEvent(eventActions);
    }
  }

  public void onStart(){
    getPinpointManager().getSessionClient().startSession();
    getPinpointManager().getAnalyticsClient().submitEvents();
  }
  public void onResume() {
    getPinpointManager().getSessionClient().resumeSession();
    getPinpointManager().getAnalyticsClient().submitEvents();
  }

  public void onPause() {
    getPinpointManager().getSessionClient().pauseSession();
    //getPinpointManager().getSessionClient().stopSession();
    getPinpointManager().getAnalyticsClient().submitEvents();
  }

  public void onStop() {
    getPinpointManager().getSessionClient().stopSession();
    getPinpointManager().getAnalyticsClient().submitEvents();
  }


  @Override public void addGlobalProperties(final Map<String, String> attributes) {
    addGlobalProperties(null, attributes);
  }

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

  private synchronized PinpointManager getPinpointManager() {
    if (INSTANCE == null) {
      CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
          context,    /* get the context for the application */
          cognitoId,    /* Identity Pool ID */
          Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
      );
      PinpointConfiguration config = new PinpointConfiguration(context, applicationId, Regions.US_EAST_1, ChannelType.GCM, credentialsProvider);

      /*AWSMobileClient.getInstance().initialize(context).execute();
      PinpointConfiguration config = new PinpointConfiguration(
          context,
          AWSMobileClient.getInstance().getCredentialsProvider(),
          AWSMobileClient.getInstance().getConfiguration()
      );*/

      INSTANCE = new PinpointManager(config);
    }

    return INSTANCE;
  }
}
