package com.worldreader.core.datasource.network.datasource.book;

import com.worldreader.core.datasource.model.BookEntity;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApiService {

  @GET("books") Call<List<BookEntity>> books(
      @Query("index") int index,
      @Query("limit") int limit,
      @Query(value = "sort") List<String> sort,
      @Query("list") String list,
      @Query("category") List<Integer> categories,
      @Query("country") String country,
      @Query("opensCountry") String countryCode,
      @Query("language") String language
  );

  @GET("books/{id}/{version}") Call<BookEntity> bookDetail(
      @Path("id") String id,
      @Path("version") String version,
      @Query("country") String countryCode
  );

  @GET("books") Call<List<BookEntity>> search(
      @Query("index") int index,
      @Query("limit") int limit,
      @Query("country") String country,
      @Query("title") String title,
      @Query("author") String author,
      @Query("category") List<Integer> categories,
      @Query ("language") List<String> languages,
      @Query(value = "tag", encoded = true) List<String> ages
  );
}
