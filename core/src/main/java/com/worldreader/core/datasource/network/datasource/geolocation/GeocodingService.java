package com.worldreader.core.datasource.network.datasource.geolocation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingService {

  @GET("http://maps.googleapis.com/maps/api/geocode/json")
  Call<ResponseBody> geocodeFromAddress(@Query("address") String address, @Query("language") String langCode, @Query("token") String token);

  @GET("http://maps.googleapis.com/maps/api/geocode/json")
  Call<ResponseBody> geocodeFromCoordinates(@Query("latlng") String latLng, @Query("language") String langCode, @Query("token") String token);

}

