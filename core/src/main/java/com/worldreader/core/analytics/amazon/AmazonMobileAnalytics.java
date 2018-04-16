package com.worldreader.core.analytics.amazon;

import com.worldreader.core.analytics.Analytics;

import java.util.*;

public interface AmazonMobileAnalytics extends Analytics {

  void onResume();

  void onPause();

  void addGlobalProperties(final String eventType, final Map<String, String> attributes);

}
