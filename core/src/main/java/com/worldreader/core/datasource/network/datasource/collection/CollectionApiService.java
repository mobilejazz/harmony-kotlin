package com.worldreader.core.datasource.network.datasource.collection;

import com.worldreader.core.datasource.model.CollectionEntity;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CollectionApiService {

  @GET("collections") Call<List<CollectionEntity>> collections(@Query("country") String country);

  @GET("collections/{id}") Call<CollectionEntity> collection(@Path("id") int collectionId, @Query("country") String country);

}
