package com.worldreader.core.datasource.network.retrofit2.services;

import com.worldreader.core.datasource.network.model.ReadingStatisticUpdateBody;
import com.worldreader.core.datasource.network.model.UserBookNetworkBody;
import com.worldreader.core.datasource.network.model.UserBookNetworkResponse;
import com.worldreader.core.datasource.network.model.UserBookStatsNetworkResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.*;

public interface UserBooksApiService {

  @GET("userbooks") Call<List<UserBookNetworkResponse>> userBooks();

  @POST("userbooks") Call<Void> updateUserBook(@Body UserBookNetworkBody body);

  @POST("userbooks/list") Call<List<UserBookNetworkResponse>> userBooks(
      @Body List<UserBookNetworkBody> body);

  @GET("userbooks/{bookId}") Call<UserBookNetworkResponse> userBook(@Path("bookId") String bookId);

  @PUT("userbooks/{bookId}") Call<UserBookNetworkResponse> updateUserBook(
      @Path("bookId") String bookId, @Body UserBookNetworkBody body);

  @DELETE("userbooks/delete/{bookId}") Call<Void> deleteUserBook(@Path("bookId") String bookId);

  @POST("userbooks/{bookId}/read") Call<UserBookStatsNetworkResponse> updateBookReadingStats(
      @Path("bookId") String bookId, @Body ReadingStatisticUpdateBody body);

  @POST("userbooks/{bookId}/favourite") Call<UserBookNetworkResponse> markBookAsFavorite(
      @Path("bookId") String bookId);

  @DELETE("userbooks/{bookId}/favourite") Call<UserBookNetworkResponse> removeBookAsFavorite(
      @Path("bookId") String bookId);

  @GET("userbooks/{bookId}/likebook") Call<UserBookNetworkResponse> isBookLiked(
      @Path("bookId") String bookId);

  @POST("userbooks/{bookId}/likebook") Call<UserBookNetworkResponse> likeBook(
      @Path("bookId") String bookId, @Body UserBookNetworkBody userBookNetworkBody);

  @GET("userbooks/liked") Call<List<UserBookNetworkResponse>> liked();

  @POST("userbooks/{bookId}/finish") Call<UserBookNetworkResponse> finishReadBook(
      @Path("bookId") String bookId, @Body UserBookNetworkBody body);

  @POST("userbooks/{bookId}/collectionids") Call<UserBookNetworkResponse> updateCollectionIds(
      @Path("bookId") String bookId, @Body UserBookNetworkBody body);
}
