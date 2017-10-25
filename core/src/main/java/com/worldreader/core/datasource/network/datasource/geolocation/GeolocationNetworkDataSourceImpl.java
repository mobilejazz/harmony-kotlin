package com.worldreader.core.datasource.network.datasource.geolocation;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.common.deprecated.error.adapter.ErrorAdapter;
import com.worldreader.core.datasource.network.general.retrofit.exception.Retrofit2Error;
import com.worldreader.core.datasource.network.model.GeolocationInfoEntity;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.util.*;

public class GeolocationNetworkDataSourceImpl implements GeolocationNetworkDataSource {

  private final GeocodingService geocodingService;
  private String token;
  private final ErrorAdapter<Throwable> errorAdapter;

  public GeolocationNetworkDataSourceImpl(GeocodingService geocodingService, String token, ErrorAdapter<Throwable> errorAdapter) {
    this.geocodingService = geocodingService;
    this.token = token;
    this.errorAdapter = errorAdapter;
  }

  @Override
  public ListenableFuture<Optional<GeolocationInfoEntity>> getGeocodeInformation(double latitude, double longitude) {
    try {
      Response<ResponseBody> response =
          geocodingService.geocodeFromCoordinates(latitude + "," + longitude, "en", token).execute();
      if (response.isSuccessful()) {
        final List<GeolocationInfoEntity> geolocationInfoEntities = GeolocationInfoEntity.parseFromNetworkResponse(response.body().string());
        return Futures.immediateFuture(Optional.fromNullable(!geolocationInfoEntities.isEmpty() ? geolocationInfoEntities.get(0) : null));
      } else {
        return Futures.immediateFailedFuture(errorAdapter.of(Retrofit2Error.httpError(response)).getCause());
      }
    } catch (Exception e) {
      return Futures.immediateFailedFuture(errorAdapter.of(e).getCause());
    }
  }


}
