package com.worldreader.core.datasource.network.datasource.dictionary;

import com.worldreader.core.datasource.model.WordDefinitionEntity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DictionaryApiService2 {

  @GET("definition/") Call<WordDefinitionEntity> definition(@Query("entry") String word);

}
