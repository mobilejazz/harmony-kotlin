

package com.worldreader.core.domain.interactors.geolocation;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.mobilejazz.kotlin.core.di.ActivityScope;
import com.worldreader.core.domain.repository.GeolocationRepository;
import java.util.concurrent.Callable;
import javax.inject.Inject;

@ActivityScope
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