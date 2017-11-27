package com.worldreader.core.analytics.amazon.interactor;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.worldreader.core.analytics.amazon.AmazonMobileAnalytics;
import com.worldreader.core.analytics.amazon.model.AnalyticsInfoModel;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.application.helper.reachability.Reachability;
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
  protected Reachability reachability;

  @Inject public ConfigAnalyticsAttributesInteractor(
      final GetAnalyticsInfoInteractor getAnalyticsInfoInteractor,
      final ListeningExecutorService executorService,
      final AmazonMobileAnalytics amazonMobileAnalytics, final CountryCodeProvider countryCodeProvider, final Reachability reachability) {
    this.getAnalyticsInfoInteractor = getAnalyticsInfoInteractor;
    this.executorService = executorService;
    this.amazonMobileAnalytics = amazonMobileAnalytics;
    this.countryCodeProvider = countryCodeProvider;
    this.reachability = reachability;

  }

  public ListenableFuture<Void> execute(final Executor executor) {
    final SettableFuture<Void> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final ListenableFuture<AnalyticsInfoModel> getAnalyticsInfoFuture =
            getAnalyticsInfoInteractor.execute(MoreExecutors.directExecutor());

        final AnalyticsInfoModel analyticsInfoModel = getAnalyticsInfoFuture.get();
        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("userId", analyticsInfoModel.getUserId());
        attributes.put("deviceId", analyticsInfoModel.getDeviceId());
        attributes.put("clientId", analyticsInfoModel.getClientId());
        attributes.put(AnalyticsEventConstants.APP_IN_OFFLINE, String.valueOf((reachability.isReachable()) ? 0 : 1));
        attributes.put("countryCode", countryCodeProvider.getCountryCode());//This is the logic to get this value: tries to get geo, if not available  ->
        // SIM, if not available -> default:US
        attributes.put("geolocationCountryCode", countryCodeProvider.getGeolocationCountryIsoCode().isPresent() ? countryCodeProvider
            .getGeolocationCountryIsoCode()
            .get()
            : "");//this value is the actual country code obtained using Google API with obtained lat,long from GPS. If we couldn't obtain that info, empty
        // value
        //When generating logs for Opera, I use only this attribute and
          // disable the following 5 ones.

        attributes.put("simCountryCode", countryCodeProvider.getSimCountryIsoCode());
        attributes.put("networkCountryCode", countryCodeProvider.getNetworkCountryIsoCode());
        attributes.put("deviceIPV4", countryCodeProvider.getIPAddress(true));
        attributes.put("deviceIPV6", countryCodeProvider.getIPAddress(false));
        attributes.put("localeLanguageCode", countryCodeProvider.getLanguageIso3Code());

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
