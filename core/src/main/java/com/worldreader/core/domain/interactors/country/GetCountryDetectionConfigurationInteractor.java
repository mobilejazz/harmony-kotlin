package com.worldreader.core.domain.interactors.country;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.datasource.CountryDetectionConfigurationDataSource;
import com.worldreader.core.domain.model.CountryDetectionConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class GetCountryDetectionConfigurationInteractor {

  private final ListeningExecutorService executorService;
  private final CountryDetectionConfigurationDataSource countryDetectionConfigurationRepository;

  @Inject public GetCountryDetectionConfigurationInteractor(ListeningExecutorService executorService,
      CountryDetectionConfigurationDataSource countryDetectionConfigurationDataSource) {
    this.executorService = executorService;
    this.countryDetectionConfigurationRepository = countryDetectionConfigurationDataSource;
  }

  public ListenableFuture<CountryDetectionConfiguration> execute() {
    return this.execute(this.executorService);
  }

  public ListenableFuture<CountryDetectionConfiguration> execute(ListeningExecutorService executorService) {
    return executorService.submit(new Callable<CountryDetectionConfiguration>() {
      @Override public CountryDetectionConfiguration call() throws Exception {
        return countryDetectionConfigurationRepository.get();
      }
    });
  }
}
