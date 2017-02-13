package com.worldreader.core.analytics;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

public interface Analytics {

  <T extends AnalyticsEvent> void sendEvent(@NonNull T event);

}
