package com.worldreader.core.datasource.network.quality;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public final class ImageResourceQualityProvider {

  private static final double LOW_QUALITY_DOWNLOAD_RATE_THRESHOLD = 150;
  private static final double MEDIUM_QUALITY_DOWNLOAD_RATE_THRESHOLD = 2000;

  private static final ImageResourceQuality DEFAULT_QUALITY = ImageResourceQuality.MEDIUM;

  private NetworkQualityChecker networkQualityChecker;

  @Inject public ImageResourceQualityProvider(NetworkQualityChecker networkQualityChecker) {
    this.networkQualityChecker = networkQualityChecker;
  }

  public ImageResourceQuality provideQuality() {
    final double downloadKBitsPerSecond = networkQualityChecker.getAverageBandwidthOnKBitsPerSecond();

    //Log.d(ImageResourceQualityProvider.class.getSimpleName(), String.valueOf(downloadKBitsPerSecond));

    if (downloadKBitsPerSecond < 0) {
      return DEFAULT_QUALITY;
    } else if (downloadKBitsPerSecond <= LOW_QUALITY_DOWNLOAD_RATE_THRESHOLD) {
      return ImageResourceQuality.LOW;
    } else if (downloadKBitsPerSecond <= MEDIUM_QUALITY_DOWNLOAD_RATE_THRESHOLD) {
      return ImageResourceQuality.MEDIUM;
    } else {
      return ImageResourceQuality.ORIGINAL;
    }
  }
}
