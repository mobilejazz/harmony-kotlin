package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.network.quality.ImageResourceQuality;
import com.worldreader.core.datasource.network.quality.ImageResourceQualityProvider;
import com.worldreader.core.domain.model.BookImageQuality;

public class BookImageQualityToBookImageResourceQualityMapper implements Mapper<BookImageQuality, ImageResourceQuality> {

  private ImageResourceQualityProvider imageResourceQualityProvider;

  public BookImageQualityToBookImageResourceQualityMapper(ImageResourceQualityProvider imageResourceQualityProvider) {
    this.imageResourceQualityProvider = imageResourceQualityProvider;
  }

  @Override public ImageResourceQuality transform(BookImageQuality bookImageQuality) {
    ImageResourceQuality imageResourceQuality = null;
    switch (bookImageQuality) {
      case LOW:
        imageResourceQuality = ImageResourceQuality.LOW;
        break;
      case MEDIUM:
        imageResourceQuality = ImageResourceQuality.MEDIUM;
        break;
      case ORIGINAL:
        imageResourceQuality = ImageResourceQuality.ORIGINAL;
        break;
      case NETWORK_QUALITY_DEPENDANT:
        imageResourceQuality = imageResourceQualityProvider.provideQuality();
        break;
    }
    return imageResourceQuality;
  }
}
