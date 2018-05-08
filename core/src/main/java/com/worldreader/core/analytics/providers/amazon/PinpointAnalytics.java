package com.worldreader.core.analytics.providers.amazon;

import com.worldreader.core.analytics.Analytics;
import java.util.Map;

public interface PinpointAnalytics extends Analytics {

  void onResume();

  void onPause();

  void addGlobalProperties(final String eventType, final Map<String, String> attributes);
}
