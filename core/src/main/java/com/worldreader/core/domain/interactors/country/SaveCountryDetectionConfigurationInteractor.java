package com.worldreader.core.domain.interactors.country;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.mobilejazz.kotlin.core.di.ActivityScope;
import com.worldreader.core.datasource.CountryDetectionConfigurationDataSource;
import com.worldreader.core.domain.model.CountryDetectionConfiguration;

import javax.inject.Inject;
import java.util.concurrent.*;

@ActivityScope public class SaveCountryDetectionConfigurationInteractor {

  private final ListeningExecutorService executorService;
  private final CountryDetectionConfigurationDataSource countryDetectionConfigurationRepository;

  @Inject public SaveCountryDetectionConfigurationInteractor(ListeningExecutorService executorService,
      CountryDetectionConfigurationDataSource CountryDetectionConfigurationDataSource) {
    this.executorService = executorService;
    this.countryDetectionConfigurationRepository = CountryDetectionConfigurationDataSource;
  }

  public ListenableFuture<Void> execute(final CountryDetectionConfiguration countryDetectionConfiguration) {
    return execute(this.executorService, countryDetectionConfiguration);
  }

  public ListenableFuture<Void> execute(ListeningExecutorService executor, final CountryDetectionConfiguration countryDetectionConfiguration) {
    return executor.submit(new Callable<Void>() {
      @Override public Void call() throws Exception {
        countryDetectionConfigurationRepository.put(countryDetectionConfiguration);
        return null;
      }
    });
  }

}
