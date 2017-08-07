package com.worldreader.core.datasource.network.datasource.category;

import com.worldreader.core.datasource.model.CategoryEntity;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryApiService {

  @GET("categories/{language}") Call<List<CategoryEntity>> categories(@Path("language") String language);
}
