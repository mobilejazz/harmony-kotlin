package com.worldreader.core.datasource.helper.locale;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.MoreExecutors;
import com.worldreader.core.datasource.model.GeolocationInfo;
import com.worldreader.core.domain.interactors.geolocation.GetGeolocationInfoInteractor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

public class LocaleCountryCodeProvider implements CountryCodeProvider {

  private final Context context;
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final Optional<GetGeolocationInfoInteractor> getGeolocationInfoInteractor;

  // If support for location implemented in main library deprecate this constructor
  public LocaleCountryCodeProvider(Context context) {
    this.context = context;
    this.getGeolocationInfoInteractor = Optional.absent();
  }

  @Inject public LocaleCountryCodeProvider(Context context,
      GetGeolocationInfoInteractor getGeolocationInfoInteractor) {
    this.context = context;
    this.getGeolocationInfoInteractor = Optional.of(getGeolocationInfoInteractor);
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

  private Optional<String> getGeolocationCountryIsoCode() {
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

  @Override public String getNetworkCountryIsoCode() {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.getNetworkCountryIso();
  }

  @Override public String getSimCountryIsoCode() {
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
