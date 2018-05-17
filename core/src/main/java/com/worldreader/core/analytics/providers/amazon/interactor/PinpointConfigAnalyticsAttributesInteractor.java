package com.worldreader.core.analytics.providers.amazon.interactor;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.BuildConfig;
import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.interactors.GetUserInfoAnalyticsInteractor;
import com.worldreader.core.analytics.models.UserInfoAnalyticsModel;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.concurrency.SafeRunnable;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;
import com.worldreader.core.domain.thread.MainThread;
import java.util.HashMap;
import java.util.concurrent.Executor;
import javax.inject.Inject;

public class PinpointConfigAnalyticsAttributesInteractor {

  private static final String TAG = PinpointConfigAnalyticsAttributesInteractor.class.getSimpleName();

  private final GetUserInfoAnalyticsInteractor getAnalyticsInfoInteractor;
  private final ListeningExecutorService executorService;

  private final Analytics analytics;

  private final CountryCodeProvider countryCodeProvider;
  private final Logger logger;
  private final MainThread mainThread;
  protected Reachability reachability;

  @Inject public PinpointConfigAnalyticsAttributesInteractor(
      final GetUserInfoAnalyticsInteractor getAnalyticsInfoInteractor,
      final ListeningExecutorService executorService,
      final Analytics analytics, final CountryCodeProvider countryCodeProvider, final Reachability reachability,
      Logger logger, MainThread mainThread) {
    this.getAnalyticsInfoInteractor = getAnalyticsInfoInteractor;
    this.executorService = executorService;
    this.analytics = analytics;
    this.countryCodeProvider = countryCodeProvider;
    this.reachability = reachability;
    this.logger = logger;
    this.mainThread = mainThread;
  }

  public ListenableFuture<Void> execute(final Executor executor) {

    final SettableFuture<Void> settableFuture = SettableFuture.create();

    executor.execute(new SafeRunnable() {
      @Override protected void safeRun() throws Throwable {
        final UserInfoAnalyticsModel userInfo = getAnalyticsInfoInteractor.execute(MoreExecutors.directExecutor()).get();

        final HashMap<String, String> attributes = new HashMap<>();
        attributes.put("userId", userInfo.userId);
        attributes.put("deviceId", userInfo.deviceId);
        attributes.put("clientId", userInfo.clientId);
        attributes.put(AnalyticsEventConstants.APP_IN_OFFLINE, String.valueOf((reachability.isReachable()) ? 0 : 1));
        attributes.put("countryCode", countryCodeProvider.getCountryCode()); //This is the logic to get this value: tries to get geo, if not available  ->
        // SIM, if not available -> default:US
        attributes.put("geolocationCountryCode", countryCodeProvider.getGeolocationCountryIsoCode().isPresent()
            ? countryCodeProvider.getGeolocationCountryIsoCode().get()
            : ""); //This value is the actual country code obtained using Google API with obtained lat,long from GPS. If
        // we couldn't obtain that info, empty value

        //When generating logs for Opera, I use only this attribute and disable the following 5 ones.

        attributes.put("simCountryCode", countryCodeProvider.getSimCountryIsoCode());
        attributes.put("networkCountryCode", countryCodeProvider.getNetworkCountryIsoCode());
        attributes.put("deviceIPV4", countryCodeProvider.getIPAddress(true));
        attributes.put("deviceIPV6", countryCodeProvider.getIPAddress(false));
        attributes.put("localeLanguageCode", countryCodeProvider.getLanguageIso3Code());

        analytics.addGlobalProperties(attributes);
        settableFuture.set(null);
      }

      @Override protected void onExceptionThrown(final Throwable t) {
        logger.e(TAG, Throwables.getStackTraceAsString(t));
        mainThread.getMainThreadExecutor().execute(new Runnable() {
          @Override public void run() {
            if (BuildConfig.DEBUG) {
              throw new RuntimeException("Problem configuring analytics attributes!! " + Throwables.getStackTraceAsString(t));
            }
          }
        });
        settableFuture.setException(t);
      }
    });

    return settableFuture;
  }

  public ListenableFuture<Void> execute() {
    return execute(executorService);
  }
}
