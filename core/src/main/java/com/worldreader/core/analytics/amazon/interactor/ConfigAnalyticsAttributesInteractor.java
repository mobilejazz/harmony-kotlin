package com.worldreader.core.analytics.amazon.interactor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.analytics.amazon.AmazonMobileAnalytics;
import com.worldreader.core.analytics.amazon.model.AnalyticsInfoModel;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.*;

public class ConfigAnalyticsAttributesInteractor {

  private final GetAnalyticsInfoInteractor getAnalyticsInfoInteractor;
  private final ListeningExecutorService executorService;
  private final AmazonMobileAnalytics amazonMobileAnalytics;
  private final CountryCodeProvider countryCodeProvider;

  @Inject public ConfigAnalyticsAttributesInteractor(
      final GetAnalyticsInfoInteractor getAnalyticsInfoInteractor,
      final ListeningExecutorService executorService,
      final AmazonMobileAnalytics amazonMobileAnalytics, final CountryCodeProvider countryCodeProvider) {
    this.getAnalyticsInfoInteractor = getAnalyticsInfoInteractor;
    this.executorService = executorService;
    this.amazonMobileAnalytics = amazonMobileAnalytics;
    this.countryCodeProvider = countryCodeProvider;
  }

  public ListenableFuture<Void> execute(final Executor executor) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<AnalyticsInfoModel> getAnalyticsInfoFuture =
            getAnalyticsInfoInteractor.execute(MoreExecutors.directExecutor());

        final AnalyticsInfoModel analyticsInfoModel = getAnalyticsInfoFuture.get();
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("UserId", analyticsInfoModel.getUserId());
        attributes.put("DeviceId", analyticsInfoModel.getDeviceId());
        attributes.put("ClientId", analyticsInfoModel.getClientId());
        attributes.put("country-code", countryCodeProvider.getCountryIso3Code());

        /*attributes.put("Sim-country-code", countryCodeProvider.getSimCountryIsoCode());
        attributes.put("Network-country-code", countryCodeProvider.getNetworkCountryIsoCode());
        attributes.put("Device-IPV4", countryCodeProvider.getIPAddress(true));
        attributes.put("Device-IPV6", countryCodeProvider.getIPAddress(false));

        attributes.put("locale-language-code", countryCodeProvider.getLanguageIso3Code());*/

        amazonMobileAnalytics.addGlobalProperties(attributes);

        settableFuture.set(null);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  public ListenableFuture<Void> execute() {
    return execute(executorService);
  }
}
