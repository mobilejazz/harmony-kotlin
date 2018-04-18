package com.worldreader.core.analytics;

import android.support.annotation.NonNull;
import com.worldreader.core.analytics.event.AnalyticsEvent;

import java.util.*;

public interface Analytics {

  <T extends AnalyticsEvent> void sendEvent(@NonNull T event);

  void addGlobalProperties(Map<String, String> attributes);

}
