package com.worldreader.core.datasource.network.model;

import com.mobilejazz.vastra.strategies.timestamp.TimestampValidationStrategyDataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.*;

public final class GeolocationInfoEntity implements TimestampValidationStrategyDataSource {

  private String street;
  private String streetNumber;
  private String postalCode;
  private String locality;
  private List<String> administrativeAreas;
  private String country;
  private String countryCode;
  private String formattedAddress;

  private double latitude;
  private double longitude;

  private Date lastUpdate;

  private GeolocationInfoEntity() {
    this.administrativeAreas = new ArrayList<>();
    this.lastUpdate = new Date();
  }

  public static List<GeolocationInfoEntity> parseFromNetworkResponse(String rawResponse) throws JSONException {
    List<GeolocationInfoEntity> geocodingInfoEntities = new ArrayList<>();

    JSONObject jsonResponse = new JSONObject(rawResponse);

    String status = jsonResponse.getString("status");

    if (!status.equalsIgnoreCase("ZERO_RESULTS")
        && status.equalsIgnoreCase("OK")) {
      JSONArray jsonResults = jsonResponse.getJSONArray("results");
      if (jsonResults.length() > 0) {
        GeolocationInfoEntity geolocationInfoEntity = new GeolocationInfoEntity();

        JSONObject jsonResult = jsonResults.getJSONObject(0);

        // First result address components
        JSONArray jsonAddressComponents = jsonResult.getJSONArray("address_components");
        for (int i = 0; i < jsonAddressComponents.length(); i++) {
          JSONObject jsonAddressComponent = jsonAddressComponents.getJSONObject(i);

          String addressComponentType = jsonAddressComponent.getJSONArray("types").getString(0);
          String longName = jsonAddressComponent.getString("long_name");
          String shortName = jsonAddressComponent.optString("short_name");
          switch (addressComponentType) {
            case "route":
              geolocationInfoEntity.street = longName;
              break;
            case "street_number":
              geolocationInfoEntity.streetNumber = longName;
              break;
            case "postal_code":
              geolocationInfoEntity.postalCode = longName;
              break;
            case "locality":
              geolocationInfoEntity.locality = longName;
              break;
            case "country":
              geolocationInfoEntity.country = longName;
              geolocationInfoEntity.countryCode = shortName;
              break;
            default:
              if (addressComponentType.contains("administrative_area")) {
                geolocationInfoEntity.administrativeAreas.add(longName);
              }
              break;
          }
        }
        // First result formatted address
        geolocationInfoEntity.formattedAddress = jsonResult.getString("formatted_address");

        // First result coordinates
        JSONObject jsonLocation = jsonResult.getJSONObject("geometry").getJSONObject("location");
        geolocationInfoEntity.latitude = jsonLocation.getDouble("lat");
        geolocationInfoEntity.longitude = jsonLocation.getDouble("lng");

        geocodingInfoEntities.add(geolocationInfoEntity);
      }
    }

    return geocodingInfoEntities;
  }

  public String getStreet() {
    return street;
  }

  public String getStreetNumber() {
    return streetNumber;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getLocality() {
    return locality;
  }

  public List<String> getAdministrativeAreas() {
    return administrativeAreas;
  }

  public String getCountry() {
    return country;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getFormattedAddress() {
    return formattedAddress;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  @Override public Date lastUpdate() {
    return lastUpdate != null ? lastUpdate : new Date();
  }

  @Override public long expiryTime() {
    return TimeUnit.DAYS.toMillis(24);
  }
}
