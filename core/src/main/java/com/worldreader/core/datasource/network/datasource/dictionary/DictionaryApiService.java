package com.worldreader.core.datasource.network.datasource.dictionary;

import com.worldreader.core.datasource.model.WordDefinitionEntity;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

@Deprecated
public interface DictionaryApiService {

  @GET("/definition/") void definition(@Query("entry") String word, Callback<WordDefinitionEntity> callback);
}
