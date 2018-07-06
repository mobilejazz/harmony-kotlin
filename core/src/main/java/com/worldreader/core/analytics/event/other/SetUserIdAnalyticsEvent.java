package com.worldreader.core.analytics.event.other;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public class SetUserIdAnalyticsEvent implements AnalyticsEvent {

  private final String id;

  public static SetUserIdAnalyticsEvent of(@NonNull String id) {
    return new SetUserIdAnalyticsEvent(id);
  }

  private SetUserIdAnalyticsEvent(@NonNull String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

}
