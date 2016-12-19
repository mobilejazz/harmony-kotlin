package com.worldreader.core.datasource.network.datasource.collection;

import com.worldreader.core.datasource.model.CollectionEntity;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import java.util.*;

public interface CollectionApiService {

  @GET("/collections") void collections(@Query("country") String country,
      Callback<List<CollectionEntity>> callback);

  @GET("/collections/{id}") void collection(@Path("id") int collectionId,
      @Query("country") String country, Callback<CollectionEntity> callback);

}
