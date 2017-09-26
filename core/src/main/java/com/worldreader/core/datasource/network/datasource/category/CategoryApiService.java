package com.worldreader.core.datasource.network.datasource.category;

import com.worldreader.core.datasource.model.CategoryEntity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.*;

public interface CategoryApiService {

  @GET("categories/{language}") Call<List<CategoryEntity>> categories(@Path("language") String language);
}
