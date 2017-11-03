package com.worldreader.core.common.helper;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.datasource.helper.locale.CountryCodeProvider;

import javax.inject.Inject;
import java.util.*;

@PerActivity public class CountryChecker {

  private static final String TAG = CountryChecker.class.getSimpleName();

  private Context context;
  private CountryCodeProvider countryCodeProvider;
  private Logger logger;

  @Inject public CountryChecker(Context context, CountryCodeProvider countryCodeProvider, Logger logger) {
    this.context = context;
    this.countryCodeProvider = countryCodeProvider;
    this.logger = logger;
  }

  public boolean isLocatedInCountry(String countryIso2Code) {
    return countryCodeProvider.getCountryCode().equals(countryIso2Code)
        || isLocaleFromCountry(countryIso2Code)
        || isSimFromCountry(countryIso2Code)
        || isNetworkFromCountry(countryIso2Code)
        || isKeyboardFromCountry(countryIso2Code);
  }

  private boolean isLocaleFromCountry(String countryIso2Code) {
    final List<Locale> locales = getLocales();
    boolean countryFound = false;
    for (Locale locale : locales) {
      logger.d(TAG, "Current Locale country code: " + locale);
      countryFound = countryIso2Code.equalsIgnoreCase(locale.getCountry());
      if (countryFound) {
        break;
      }
    }
    return countryFound;
  }

  private List<Locale> getLocales() {
    List<Locale> locales = new ArrayList<>();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      final LocaleList localeList = LocaleList.getDefault();
      for (int i = 0; i < localeList.size(); i++) {
        locales.add(localeList.get(i));
      }
    } else {
      locales.add(Locale.getDefault());
    }
    return locales;
  }

  private boolean isSimFromCountry(String countryIso2Code) {
    String simCountry = countryCodeProvider.getSimCountryIsoCode();
    logger.d(TAG, "Current SIM country code: " + simCountry);
    return countryIso2Code.equalsIgnoreCase(simCountry);
  }

  private boolean isNetworkFromCountry(String countryIso2Code) {
    String networkCountry = countryCodeProvider.getNetworkCountryIsoCode();
    logger.d(TAG, "Current Network country code: " + networkCountry);
    return countryIso2Code.equalsIgnoreCase(networkCountry);
  }

  private boolean isKeyboardFromCountry(String countryIso2Code) {
    List<String> keyboardLocales = getKeyboardLocales();
    boolean found = false;
    for (String keyboardLocale : keyboardLocales) {
      logger.d(TAG, "Current Input country code: " + keyboardLocale);

      found = keyboardLocale.toLowerCase().contains("_" + countryIso2Code.toLowerCase());
      if (found) {
        break;
      }
    }
    return found;
  }

  @SuppressWarnings("deprecation") private List<String> getKeyboardLocales() {
    List<String> locales = new ArrayList<>();
    final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    List<InputMethodInfo> ims = imm.getEnabledInputMethodList();

    for (InputMethodInfo method : ims) {
      List<InputMethodSubtype> submethods = imm.getEnabledInputMethodSubtypeList(method, true);
      for (InputMethodSubtype submethod : submethods) {
        if (submethod.getMode().equals("keyboard")) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locales.add(submethod.getLocale());
          }
        }
      }
    }
    return locales;

  }


}
