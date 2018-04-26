package com.worldreader.core.analytics.amazon;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.worldreader.core.analytics.Analytics;

import java.util.*;

public interface PinpointAnalytics extends Analytics {

  void onResume();

  void onPause();

  void addGlobalProperties(final String eventType, final Map<String, String> attributes);


}
