package com.worldreader.core.datasource.network.datasource.category;

import com.worldreader.core.datasource.model.CategoryEntity;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import java.util.*;

public interface CategoryApiService {

  @GET("/categories/{language}") void categories(@Path("language") String language,
      Callback<List<CategoryEntity>> callback);
}
