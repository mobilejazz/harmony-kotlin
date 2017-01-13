package com.worldreader.core.datasource.network.datasource.banner;

import com.worldreader.core.datasource.model.BannerEntity;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import java.util.*;

public interface BannerApiService {

  @GET("/banners/main") void mainBanners(@Query("index") int index, @Query("limit") int limit,
      @Query("country") String country, Callback<List<BannerEntity>> callback);

  @GET("/banners/collection") void collectionBanners(@Query("index") int index,
      @Query("limit") int limit, @Query("country") String country,
      Callback<List<BannerEntity>> callback);

  @GET("/banners/{banner_identifier}") void banners(
      @Path("banner_identifier") String bannerIdentifier, @Query("index") int index,
      @Query("limit") int limit, @Query("country") String country,
      Callback<List<BannerEntity>> callback);

  @GET("/banners/{banner_identifier}/{id}") void banner(@Path("id") int id,
      @Path("banner_identifier") String bannerIdentifier, @Query("country") String country,
      Callback<BannerEntity> callback);
}
