package com.worldreader.core.domain.interactors.geolocation;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.domain.repository.GeolocationRepository;

import javax.inject.Inject;
import java.util.concurrent.*;

@PerActivity
public class UpdateGeolocationInfoInteractor {

  private final ListeningExecutorService executor;
  private final GeolocationRepository geolocationRepository;

  @Inject
  public UpdateGeolocationInfoInteractor(ListeningExecutorService executor, GeolocationRepository geolocationRepository) {
    this.executor = executor;
    this.geolocationRepository = geolocationRepository;
  }

  public ListenableFuture<Boolean> execute(final double lat, final double lon) {
    return execute(lat, lon, executor);
  }

  public ListenableFuture<Boolean> execute(final double lat, final double lon, ListeningExecutorService executor) {
    return executor.submit(new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        return geolocationRepository.update(lat, lon).get();
      }
    });
  }

}