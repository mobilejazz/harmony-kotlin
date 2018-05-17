package com.worldreader.core.analytics.event.notifications;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class NotificationDisplayedAnalyticsEvent implements AnalyticsEvent {

  private String notificationId;

  private NotificationDisplayedAnalyticsEvent(@NonNull String notificationId) {
    this.notificationId = notificationId;
  }

  public static NotificationDisplayedAnalyticsEvent of(@NonNull String notificationId) {
    return new NotificationDisplayedAnalyticsEvent(notificationId);
  }

  public String getNotificationId() {
    return notificationId;
  }

}
