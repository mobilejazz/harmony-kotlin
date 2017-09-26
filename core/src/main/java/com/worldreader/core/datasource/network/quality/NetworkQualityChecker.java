package com.worldreader.core.datasource.network.quality;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton
public final class NetworkQualityChecker {

  public enum NetworkQuality {
    POOR,
    GOOD,
    EXCELLENT,
    UNKNOWN,
  }

  @Inject public NetworkQualityChecker() {
  }

  /**
   * Method call to start sampling for download bandwidth.
   */
  public void startSampling() {
    if (!DeviceBandwidthSampler.getInstance().isSampling()) {
      DeviceBandwidthSampler.getInstance().startSampling();
    }
    // This fake bandwidth is used to avoid low (and unreal) bandwidth values when not much data has been downloaded
    ConnectionClassManager.getInstance().addBandwidth(150, TimeUnit.SECONDS.toMillis(1));
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

  public NetworkQuality getNetworkQuality() {
    switch (ConnectionClassManager.getInstance().getCurrentBandwidthQuality()) {
      case POOR:
        return NetworkQuality.POOR;
      case MODERATE:
        return NetworkQuality.GOOD;
      case GOOD:
        return NetworkQuality.GOOD;
      case EXCELLENT:
        return NetworkQuality.EXCELLENT;
      default:
        return NetworkQuality.UNKNOWN;
    }
  }
}
