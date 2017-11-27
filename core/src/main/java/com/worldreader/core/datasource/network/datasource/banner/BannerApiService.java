package com.worldreader.core.datasource.network.datasource.banner;

import com.worldreader.core.datasource.model.BannerEntity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.*;

public interface BannerApiService {

  @GET("banners/main") Call<List<BannerEntity>> mainBanners(@Query("index") int index, @Query("limit") int limit, @Query("country") String country);

  @GET("banners/collection") Call<List<BannerEntity>> collectionBanners(@Query("index") int index, @Query("limit") int limit,
      @Query("country") String country);

  @GET("banners/{banner_identifier}") Call<List<BannerEntity>> banners(@Path("banner_identifier") String bannerIdentifier, @Query("index") int index,
      @Query("limit") int limit, @Query("country") String country);

  @GET("banners/{banner_identifier}/{id}") Call<BannerEntity> banner(@Path("id") int id, @Path("banner_identifier") String bannerIdentifier,
      @Query("country") String country);
}
