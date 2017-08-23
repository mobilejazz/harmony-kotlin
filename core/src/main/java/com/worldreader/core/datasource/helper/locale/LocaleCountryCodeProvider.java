package com.worldreader.core.datasource.helper.locale;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

public class LocaleCountryCodeProvider implements CountryCodeProvider {

  private Context context;

  @Inject public LocaleCountryCodeProvider(Context context) {
    this.context = context;
  }

  @Override public String getCountryCode() {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    String countryISO = tm.getSimCountryIso();

    // Trick to make possible work with the emulator.
    if (TextUtils.isEmpty(countryISO)) {
      countryISO = "US";
    }

    return countryISO;
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
            boolean isIPv4 = sAddr.indexOf(':')<0;
            if (useIPv4) {
              if (isIPv4)
                return sAddr;
            } else {
              if (!isIPv4) {
                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
              }
            }
          }
        }
      }
    } catch (Exception ex) { } // for now eat exceptions
    return "";
  }
}
