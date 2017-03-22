package com.worldreader.core.datasource.network.datasource.book;

import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.datasource.model.ContentOpfLocationEntity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface StreamingBookApiService2 {

  String VERSION_LATEST = "latest";
  String GET_BOOK_RESOURCE_URL = "/books/%s/latest/content/%s";

  @GET Call<ResponseBody> getBookResource(@Url String url);

  @GET("/books/{id}/{version}/content/META-INF/container.xml") Call<ContentOpfLocationEntity> getContentOpfLocationEntity(@Path("id") String id,
      @Path("version") String version);

  @GET("/books/{id}/{version}/content/{resource}") Call<ContentOpfEntity> getContentOpfEntity(@Path("id") String id, @Path("version") String version,
      @Path(value = "resource", encoded = true) String resource);
}
