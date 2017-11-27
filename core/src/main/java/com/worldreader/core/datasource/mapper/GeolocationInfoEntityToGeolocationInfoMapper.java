package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.model.GeolocationInfo;
import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;

public class GeolocationInfoEntityToGeolocationInfoMapper implements Mapper<GeolocationInfoEntity, GeolocationInfo> {

  @Override public GeolocationInfo transform(GeolocationInfoEntity from) {
    return new GeolocationInfo(
        from.getLatitude(),
        from.getLongitude(),
        from.getCountryCode()
    );
  }
}
