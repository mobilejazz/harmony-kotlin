package com.worldreader.core.domain.interactors.geolocation;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.datasource.model.GeolocationInfo;
import com.worldreader.core.domain.repository.GeolocationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton
public class GetGeolocationInfoInteractor {

  private final ListeningExecutorService executor;
  private final GeolocationRepository geolocationRepository;

  @Inject
  public GetGeolocationInfoInteractor(ListeningExecutorService executor, GeolocationRepository geolocationRepository) {
    this.executor = executor;
    this.geolocationRepository = geolocationRepository;
  }

  public ListenableFuture<Optional<GeolocationInfo>> execute() {
    return execute(executor);
  }

  public ListenableFuture<Optional<GeolocationInfo>> execute(ListeningExecutorService executor) {
    return executor.submit(new Callable<Optional<GeolocationInfo>>() {
      @Override public Optional<GeolocationInfo> call() throws Exception {
        return geolocationRepository.get().get();
      }
    });
  }

}