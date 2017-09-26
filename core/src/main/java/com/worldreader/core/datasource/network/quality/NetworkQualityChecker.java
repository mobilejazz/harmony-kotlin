package com.worldreader.core.datasource.network.quality;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class NetworkQualityChecker {

  @Inject public NetworkQualityChecker() {
  }

  /**
   * Method call to start sampling for download bandwidth.
   */
  public void startSampling() {
    if (!DeviceBandwidthSampler.getInstance().isSampling()) {
      DeviceBandwidthSampler.getInstance().startSampling();
    }
  }

  /**
   * Finish sampling and prevent further changes to the ConnectionClass until another timer is started.
   */
  public void stopSampling() {
    if (DeviceBandwidthSampler.getInstance().isSampling()) {
      DeviceBandwidthSampler.getInstance().stopSampling();
    }
  }

  /**
   * Accessor method for the current bandwidth average.
   * @return The current bandwidth average on KBytes per second, or -1 if no average has been recorded.
   */
  public double getAverageBandwidthOnKBitsPerSecond() {
    return ConnectionClassManager.getInstance().getDownloadKBitsPerSecond();
  }
}
