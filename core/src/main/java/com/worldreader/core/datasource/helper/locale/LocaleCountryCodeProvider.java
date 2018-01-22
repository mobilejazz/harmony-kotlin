package com.worldreader.core.datasource.helper.locale;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.datasource.model.GeolocationInfo;
import com.worldreader.core.domain.interactors.country.GetCountryDetectionConfigurationInteractor;
import com.worldreader.core.domain.interactors.geolocation.GetGeolocationInfoInteractor;
import com.worldreader.core.domain.model.CountryDetectionConfiguration;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.*;

public class LocaleCountryCodeProvider implements CountryCodeProvider {

  private final Context context;
  // These interactors are optional due to the fact that we are not interested in using them in all apps depending on core.
  private final Optional<GetCountryDetectionConfigurationInteractor> getCountryDetectionConfigurationInteractor;
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final Optional<GetGeolocationInfoInteractor> getGeolocationInfoInteractor;

  // If support for location implemented in Main Library remove this constructor and make interactors non-optional
  public LocaleCountryCodeProvider(Context context) {
    this.context = context;
    this.getGeolocationInfoInteractor = Optional.absent();
    this.getCountryDetectionConfigurationInteractor = Optional.absent();
  }

  @Inject public LocaleCountryCodeProvider(Context context,
      GetGeolocationInfoInteractor getGeolocationInfoInteractor,
      GetCountryDetectionConfigurationInteractor getCountryDetectionConfigurationInteractor) {
    this.context = context;
    this.getGeolocationInfoInteractor = Optional.of(getGeolocationInfoInteractor);
    this.getCountryDetectionConfigurationInteractor = Optional.of(getCountryDetectionConfigurationInteractor);
  }

  @Override public String getCountryCode() {
    String countryISO;
    final Optional<String> geolocationCountryIsoCode = getGeolocationCountryIsoCode();
    if (geolocationCountryIsoCode.isPresent()) {
      countryISO = geolocationCountryIsoCode.get();
    } else {
      countryISO = getSimCountryIsoCode();

      // Trick to make possible work with the emulator.
      if (TextUtils.isEmpty(countryISO)) {
        countryISO = "US";
      }
    }
    return countryISO;
  }

  @Override
  public Optional<String> getGeolocationCountryIsoCode() {
    Optional<String> forcedCountryIsoCode = getForcedCountryIsoCode();
    if (forcedCountryIsoCode.isPresent()) {
      return forcedCountryIsoCode;
    }

    if (!getGeolocationInfoInteractor.isPresent()) {
      return Optional.absent();
    }
    try {
      return getGeolocationInfoInteractor.get().execute(MoreExecutors.newDirectExecutorService()).get()
          .transform(new Function<GeolocationInfo, String>() {
            @Nullable @Override public String apply(GeolocationInfo input) {
              return input.getCountryCode();
            }
          });
    } catch (Throwable throwable) {
      return Optional.absent();
    }
  }

  /**
   *   Get the forced country code in case it exists
   */
  private Optional<String> getForcedCountryIsoCode() {
    if (!getCountryDetectionConfigurationInteractor.isPresent()) {
      return Optional.absent();
    }

    try {
      CountryDetectionConfiguration countryDetectionConfiguration =
          getCountryDetectionConfigurationInteractor.get().execute(MoreExecutors.newDirectExecutorService()).get();

      if (countryDetectionConfiguration.isAutomatic()) {
        return Optional.absent();
      } else {
        return Optional.of(countryDetectionConfiguration.getForcedCountry());
      }
    } catch (Throwable throwable) {
      return Optional.absent();
    }
  }

  @Override public String getNetworkCountryIsoCode() {
    Optional<String> forcedCountryIsoCode = getForcedCountryIsoCode();
    if (forcedCountryIsoCode.isPresent()) {
      return forcedCountryIsoCode.get();
    }

    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.getNetworkCountryIso();
  }

  @Override public String getSimCountryIsoCode() {
    Optional<String> forcedCountryIsoCode = getForcedCountryIsoCode();
    if (forcedCountryIsoCode.isPresent()) {
      return forcedCountryIsoCode.get();
    }

    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.getSimCountryIso();
  }

  @Override public String getCountryIso3Code() {
    return getLocale().getISO3Country();
  }

  @Override public String getCountry() {
    return getLocale().getCountry();
  }

  @Override public String getLanguageIso3Code() {
    return getLocale().getISO3Language();
  }

  private Locale getLocale() {
    return context.getResources().getConfiguration().locale;
  }

  @Override public String getDisplayLanguage() {
    return getLocale().getDisplayLanguage();
  }

  @Override public String getDisplayLanguage(String languageIsoCode3) {
    Locale[] availableLocales = Locale.getAvailableLocales();
    for (Locale availableLocale : availableLocales) {
      if (availableLocale.getISO3Language().equalsIgnoreCase(languageIsoCode3)) {
        return availableLocale.getDisplayLanguage();
      }
    }
    return null;
  }

  @Override public String getIPAddress(boolean useIPv4) {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
          if (!addr.isLoopbackAddress()) {
            String sAddr = addr.getHostAddress();
            boolean isIPv4 = sAddr.indexOf(':') < 0;
            if (useIPv4) {
              if (isIPv4) {
                return sAddr;
              }
            } else {
              if (!isIPv4) {
                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
              }
            }
          }
        }
      }
    } catch (Exception ex) {
    } // for now eat exceptions
    return "";
  }
}
