package com.worldreader.core.datasource.network.datasource.book;

import com.worldreader.core.datasource.model.BookEntity;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import java.util.*;

public interface BookApiService {

  @GET("/books") void books(@Query("index") int index, @Query("limit") int limit,
      @Query(value = "sort", encodeValue = false) List<String> sort, @Query("list") String list,
      @Query("category") List<Integer> categories, @Query("country") String country,
      @Query("opensCountry") String countryCode, @Query("language") String language,
      Callback<List<BookEntity>> callback);

  @GET("/books") void searchBooksByTitle(@Query("index") int index, @Query("limit") int limit,
      @Query("country") String country, @Query("title") String title,
      Callback<List<BookEntity>> callback);

  @GET("/books") void searchBooksByAuthor(@Query("index") int index, @Query("limit") int limit,
      @Query("country") String country, @Query("author") String author,
      Callback<List<BookEntity>> callback);

  @GET("/books/{id}/{version}") void bookDetail(@Path("id") String id,
      @Path("version") String version, @Query("country") String countryCode,
      Callback<BookEntity> callback);

}
