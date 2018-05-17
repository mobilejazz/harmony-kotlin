package com.worldreader.core.analytics.event.notifications;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class NotificationClickedAnalyticsEvent implements AnalyticsEvent {

  private String notificationId;

  private NotificationClickedAnalyticsEvent(@NonNull String notificationId) {
    this.notificationId = notificationId;
  }

  public static NotificationClickedAnalyticsEvent of(@NonNull String notificationId) {
    return new NotificationClickedAnalyticsEvent(notificationId);
  }

  public String getNotificationId() {
    return notificationId;
  }

}
