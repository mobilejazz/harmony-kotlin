/*
 * Copyright (C) 2012 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import jedi.functional.Filter;
import jedi.functional.Functor;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.dto.PageOffsets;
import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static jedi.functional.FunctionalPrimitives.*;
import static jedi.option.Options.none;
import static jedi.option.Options.option;

/**
 * Application configuration class which provides a friendly API to the various
 * settings available.
 *
 * @author Alex Kuiper
 */
public class Configuration {

  public static final String TAG = Configuration.class.getSimpleName();
  public static final String KEY_POS = "offset:";
  public static final String KEY_IDX = "index:";
  public static final String KEY_NAV_TAP_V = "nav_tap_v";
  public static final String KEY_NAV_TAP_H = "nav_tap_h";
  public static final String KEY_NAV_SWIPE_H = "nav_swipe_h";
  public static final String KEY_NAV_SWIPE_V = "nav_swipe_v";
  public static final String KEY_STRIP_WHITESPACE = "strip_whitespace";
  public static final String KEY_SCROLLING = "scrolling";
  public static final String KEY_TEXT_SIZE = "itext_size";
  public static final String KEY_MARGIN_H = "margin_h";
  public static final String KEY_MARGIN_V = "margin_v";
  public static final String KEY_LINE_SPACING = "line_spacing";
  public static final String KEY_NIGHT_MODE = "night_mode";
  public static final String KEY_SCREEN_ORIENTATION = "screen_orientation";
  public static final String KEY_FONT_FACE = "font_face";
  public static final String KEY_SERIF_FONT = "serif_font";
  public static final String KEY_SANS_SERIF_FONT = "sans_serif_font";
  public static final String PREFIX_DAY = "day";
  public static final String PREFIX_NIGHT = "night";
  public static final String PREFIX_CREAM = "cream";
  public static final String KEY_BRIGHTNESS = "bright";
  public static final String KEY_BACKGROUND = "bg";
  public static final String KEY_LINK = "link";
  public static final String KEY_TEXT = "text";
  public static final String KEY_BRIGHTNESS_CTRL = "set_brightness";
  public static final String KEY_SCROLL_STYLE = "scroll_style";
  public static final String KEY_SCROLL_SPEED = "scroll_speed";
  public static final String KEY_H_ANIMATION = "h_animation";
  public static final String KEY_V_ANIMATION = "v_animation";
  public static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
  public static final String KEY_OFFSETS = "offsets";
  public static final String KEY_SHOW_PAGENUM = "show_pagenum";
  public static final String KEY_READING_DIRECTION = "reading_direction";
  public static final String KEY_DIM_SYSTEM_UI = "dim_system_ui";
  public static final String KEY_LONG_SHORT = "long_short";
  public static final String KEY_ALLOW_STYLING = "allow_styling";
  public static final String KEY_ALLOW_STYLE_COLOURS = "allow_style_colours";
  public static final String KEY_LAST_TITLE = "last_title";
  //Which platform version to start text selection on.
  public static final int TEXT_SELECTION_PLATFORM_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;
  private SharedPreferences settings;
  private Context context;
  private Map<String, FontFamily> fontCache = new HashMap<>();

  public Configuration(Context context) {
    this.settings = PreferenceManager.getDefaultSharedPreferences(context);
    this.context = context;
  }

  /**
   * Returns the bytes of available memory left on the heap. Not totally sure
   * if it works reliably.
   */
  public static double getMemoryUsage() {
    long max = Runtime.getRuntime().maxMemory();
    long used = Runtime.getRuntime().totalMemory();

    return (double) used / (double) max;
  }

  /*
      Returns the available bitmap memory.
      On newer Android versions this is the same as the normaL
      heap memory.
   */
  public static double getBitmapMemoryUsage() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      return getMemoryUsage();
    }

    long max = Runtime.getRuntime().maxMemory();
    long used = Debug.getNativeHeapAllocatedSize();

    return (double) used / (double) max;
  }

  public boolean isVerticalTappingEnabled() {
    return !isScrollingEnabled() && settings.getBoolean(KEY_NAV_TAP_V, false);
  }

  public boolean isHorizontalTappingEnabled() {
    return !isScrollingEnabled() && settings.getBoolean(KEY_NAV_TAP_H, true);
  }

  public boolean isHorizontalSwipeEnabled() {
    return !isScrollingEnabled() && settings.getBoolean(KEY_NAV_SWIPE_H, true);
  }

  public boolean isVerticalSwipeEnabled() {
    return settings.getBoolean(KEY_NAV_SWIPE_V, false) && !isScrollingEnabled();
  }

  public boolean isAllowStyling() {
    return settings.getBoolean(KEY_ALLOW_STYLING, false);
  }

  public int getLastPosition(String fileName) {
    SharedPreferences bookPrefs = getPrefsForBook(fileName);

    int pos = bookPrefs.getInt(KEY_POS, -1);

    if (pos != -1) {
      return pos;
    }

    //Fall-back to older settings
    String bookHash = Integer.toHexString(fileName.hashCode());

    pos = settings.getInt(KEY_POS + bookHash, -1);

    if (pos != -1) {
      return pos;
    }

    // Fall-back for even older settings.
    return settings.getInt(KEY_POS + fileName, -1);
  }

  private SharedPreferences getPrefsForBook(String fileName) {
    String bookHash = Integer.toHexString(fileName.hashCode());
    return context.getSharedPreferences(bookHash, 0);
  }

  public void setPageOffsets(String fileName, List<List<Integer>> offsets) {
    SharedPreferences bookPrefs = getPrefsForBook(fileName);

    PageOffsets offsetsObject = PageOffsets.fromValues(this, offsets);

    try {
      String json = offsetsObject.toJSON();
      updateValue(bookPrefs, KEY_OFFSETS, json);
    } catch (JSONException js) {
      Log.e(TAG, "Error storing page offsets: " + js.getMessage());
    }
  }

  public Option<List<List<Integer>>> getPageOffsets(String fileName) {
    SharedPreferences bookPrefs = getPrefsForBook(fileName);

    String data = bookPrefs.getString(KEY_OFFSETS, "");

    if (data.length() == 0) {
      return none();
    }

    try {
      PageOffsets offsets = PageOffsets.fromJSON(data);

      if (offsets == null || !offsets.isValid(this)) {
        return none();
      }

      return option(offsets.getOffsets());
    } catch (JSONException js) {
      Log.e(TAG, "Could not retrieve page offsets: " + js.getMessage());
      return none();
    }
  }

  public LongShortPressBehaviour getLongShortPressBehaviour() {
    String value = settings.getString(KEY_LONG_SHORT, LongShortPressBehaviour.NORMAL.name());
    return LongShortPressBehaviour.valueOf(value.toUpperCase(Locale.US));
  }

  public ReadingDirection getReadingDirection() {
    String value = settings.getString(KEY_READING_DIRECTION, ReadingDirection.LEFT_TO_RIGHT.name());
    return ReadingDirection.valueOf(value.toUpperCase(Locale.US));
  }

  public void setLastPosition(String fileName, int position) {
    SharedPreferences bookPrefs = getPrefsForBook(fileName);

    updateValue(bookPrefs, KEY_POS, position);
  }

  public int getLastIndex(String fileName) {
    SharedPreferences bookPrefs = getPrefsForBook(fileName);

    int pos = bookPrefs.getInt(KEY_IDX, -1);

    if (pos != -1) {
      return pos;
    }

    //Fall-backs to older setting in central file
    String bookHash = Integer.toHexString(fileName.hashCode());

    pos = settings.getInt(KEY_IDX + bookHash, -1);

    if (pos != -1) {
      return pos;
    }

    // Fall-back for even older settings.
    return settings.getInt(KEY_IDX + fileName, -1);
  }

  public void setLastIndex(String fileName, int index) {
    SharedPreferences bookPrefs = getPrefsForBook(fileName);
    updateValue(bookPrefs, KEY_IDX, index);
  }

  public boolean isShowPageNumbers() {
    return settings.getBoolean(KEY_SHOW_PAGENUM, true);
  }

  public boolean isDimSystemUI() {
    return settings.getBoolean(KEY_DIM_SYSTEM_UI, false);
  }

  public boolean isStripWhiteSpaceEnabled() {
    return settings.getBoolean(KEY_STRIP_WHITESPACE, false);
  }

  public boolean isScrollingEnabled() {
    return settings.getBoolean(KEY_SCROLLING, false);
  }

  public int getTextSize() {
    return settings.getInt(KEY_TEXT_SIZE, FontSizes.BIG);
  }

  public void setTextSize(int textSize) {
    updateValue(KEY_TEXT_SIZE, textSize);
  }

  public int getHorizontalMargin() {
    return settings.getInt(KEY_MARGIN_H, 60);
  }

  public int getVerticalMargin() {
    return settings.getInt(KEY_MARGIN_V, 60);
  }

  public int getLineSpacing() {
    return settings.getInt(KEY_LINE_SPACING, 0);
  }

  public boolean isKeepScreenOn() {
    return settings.getBoolean(KEY_KEEP_SCREEN_ON, true);
  }

  public ColorProfile getColourProfile() {
    String stringValue = settings.getString(KEY_NIGHT_MODE, ColorProfile.DAY.toString());
    return ColorProfile.valueOf(stringValue);
  }

  public void setColourProfile(ColorProfile profile) {
    if (profile == ColorProfile.DAY) {
      updateValue(KEY_NIGHT_MODE, ColorProfile.DAY.toString());
    } else if (profile == ColorProfile.NIGHT) {
      updateValue(KEY_NIGHT_MODE, ColorProfile.NIGHT.toString());
    } else {
      updateValue(KEY_NIGHT_MODE, ColorProfile.CREAM.toString());
    }
  }

  public Orientation getScreenOrientation() {
    String orientation = settings.getString(KEY_SCREEN_ORIENTATION,
        Orientation.PORTRAIT.name().toLowerCase(Locale.US));
    return Orientation.valueOf(orientation.toUpperCase(Locale.US));
  }

  private void updateValue(SharedPreferences prefs, String key, Object value) {
    SharedPreferences.Editor editor = prefs.edit();

    if (value == null) {
      editor.remove(key);
    } else if (value instanceof String) {
      editor.putString(key, (String) value);
    } else if (value instanceof Integer) {
      editor.putInt(key, (Integer) value);
    } else if (value instanceof Boolean) {
      editor.putBoolean(key, (Boolean) value);
    } else {
      throw new IllegalArgumentException("Unsupported type: " + value.getClass().getSimpleName());
    }

    editor.commit();
  }

  private void updateValue(String key, Object value) {
    updateValue(settings, key, value);
  }

  private FontFamily loadFamilyFromAssets(String key, FontFamilies.FontFamily fontFamily) {
    String basePath = "fonts/";

    Typeface basic = Typeface.createFromAsset(context.getAssets(), basePath + fontFamily.getFont());
    Typeface boldFace =
        Typeface.createFromAsset(context.getAssets(), basePath + fontFamily.getBoldFont());
    Typeface italicFace =
        Typeface.createFromAsset(context.getAssets(), basePath + fontFamily.getItalicFont());
    //Typeface biFace =
    //    Typeface.createFromAsset(context.getAssets(), basePath + fontFamily.getBoldItalicFont());

    FontFamily fam = new FontFamily(key, basic);
    fam.setBoldTypeface(boldFace);
    fam.setItalicTypeface(italicFace);
    //fam.setBoldItalicTypeface(biFace);

    return fam;
  }

  private FontFamily getFontFamily(String fontKey, FontFamilies.FontFamily fontFamily) {
    String fontFace = settings.getString(fontKey, fontFamily.fontName);

    if (!fontCache.containsKey(fontFace)) {
      if (fontFace.equals(FontFamilies.LORA.DEFAULT.fontName)) {
        fontCache.put(fontFace, loadFamilyFromAssets(fontFace, FontFamilies.LORA.DEFAULT));
      } else if (fontFace.equals(FontFamilies.OPEN_SANS.DEFAULT.fontName)) {
        fontCache.put(fontFace, loadFamilyFromAssets(fontFace, FontFamilies.OPEN_SANS.DEFAULT));
      } else if (fontFace.equals(FontFamilies.POPPINS.DEFAULT.fontName)) {
        fontCache.put(fontFace, loadFamilyFromAssets(fontFace, FontFamilies.POPPINS.DEFAULT));
      } else {
        Typeface face = null;
        if (FontFamilies.FAMILIES.SANS.equals(fontFace)) {
          face = Typeface.SANS_SERIF;
        } else if (FontFamilies.FAMILIES.SERIF.equals(fontFace)) {
          face = Typeface.SERIF;
        } else if (FontFamilies.FAMILIES.MONO.equals(fontFace)) {
          face = Typeface.MONOSPACE;
        } else if (FontFamilies.FAMILIES.DEFAULT.equals(fontFace)) {
          face = Typeface.DEFAULT;
        }
        fontCache.put(fontFace, new FontFamily(fontFace, face));
      }
    }

    return fontCache.get(fontFace);
  }

  public FontFamily getSerifFontFamily() {
    return getFontFamily(KEY_SERIF_FONT, FontFamilies.LORA.DEFAULT);
  }

  public void setSerifFontFamily(FontFamilies.FontFamily fontFamily) {
    updateValue(KEY_SERIF_FONT, fontFamily.fontName);
  }

  public String getSerifFontFamilyString() {
    return settings.getString(KEY_SERIF_FONT, FontFamilies.LORA.DEFAULT.fontName);
  }

  public FontFamily getSansSerifFontFamily() {
    return getFontFamily(KEY_SANS_SERIF_FONT, FontFamilies.OPEN_SANS.DEFAULT);
  }

  public FontFamily getDefaultFontFamily() {
    return getFontFamily(KEY_FONT_FACE, FontFamilies.LORA.DEFAULT);
  }

  public int getBrightness() {
    // Brightness 0 means black screen :)
    return Math.max(1, getProfileSetting(KEY_BRIGHTNESS, 50, 50, 50));
  }

  public void setBrightness(int brightness) {
    if (getColourProfile() == ColorProfile.DAY) {
      updateValue("day_bright", brightness);
    } else {
      updateValue("night_bright", brightness);
    }
  }

  public int getBackgroundColor() {
    return getProfileSetting(KEY_BACKGROUND, Color.WHITE, Color.BLACK, Color.rgb(246, 242, 229));
  }

  public int getTextColor() {
    return getProfileSetting(KEY_TEXT, Color.BLACK, Color.GRAY, Color.rgb(111, 67, 28));
  }

  public int getLinkColor() {
    return getProfileSetting(KEY_LINK, Color.BLUE, Color.rgb(255, 165, 0), Color.RED);
  }

  public boolean isUseColoursFromCSS() {
    String setting = KEY_ALLOW_STYLE_COLOURS;
    boolean nightDefault = false;
    boolean creamDefault = false;
    boolean dayDefault = true;

    if (getColourProfile() == ColorProfile.NIGHT) {
      return settings.getBoolean(PREFIX_NIGHT + "_" + setting, nightDefault);
    }
    if (getColourProfile() == ColorProfile.CREAM) {
      return settings.getBoolean(PREFIX_CREAM + "_" + setting, creamDefault);
    } else {
      return settings.getBoolean(PREFIX_DAY + "_" + setting, dayDefault);
    }
  }

  private int getProfileSetting(String setting, int dayDefault, int nightDefault,
      int creamDefault) {
    if (getColourProfile() == ColorProfile.NIGHT) {
      return settings.getInt(PREFIX_NIGHT + "_" + setting, nightDefault);
    } else if (getColourProfile() == ColorProfile.CREAM) {
      return settings.getInt(PREFIX_CREAM + "_" + setting, creamDefault);
    } else {
      return settings.getInt(PREFIX_DAY + "_" + setting, dayDefault);
    }
  }

  public ScrollStyle getAutoScrollStyle() {
    String style = settings.getString(KEY_SCROLL_STYLE,
        ScrollStyle.ROLLING_BLIND.name().toLowerCase(Locale.US));
    if ("rolling_blind".equals(style)) {
      return ScrollStyle.ROLLING_BLIND;
    } else {
      return ScrollStyle.PAGE_TIMER;
    }
  }

  public int getScrollSpeed() {
    return settings.getInt(KEY_SCROLL_SPEED, 20);
  }

  public AnimationStyle getHorizontalAnim() {
    String animH =
        settings.getString(KEY_H_ANIMATION, AnimationStyle.CURL.name().toLowerCase(Locale.US));
    return AnimationStyle.valueOf(animH.toUpperCase(Locale.US));
  }

  public AnimationStyle getVerticalAnim() {
    String animV =
        settings.getString(KEY_V_ANIMATION, AnimationStyle.CURL.name().toLowerCase(Locale.US));
    return AnimationStyle.valueOf(animV.toUpperCase(Locale.US));
  }

  public Option<File> getStorageBase() {
    return option(Environment.getExternalStorageDirectory());
  }

  public Option<File> getDownloadsFolder() {
    return firstOption(asList(ContextCompat.getExternalFilesDirs(context, "Downloads")));
  }

  public Option<File> getLibraryFolder() {
    Option<File> libraryFolder = getStorageBase().map(new Functor<File, File>() {
      @Override public File execute(File baseFolder) {
        return new File(baseFolder.getAbsolutePath() + "/PageTurner/Books");
      }
    });

    //If the library-folder on external storage exists, return it
    if (!isEmpty(select(libraryFolder, new Filter<File>() {
      @Override public Boolean execute(File file) {
        return file.exists();
      }
    }))) {
      return libraryFolder;
    }

    if (!isEmpty(libraryFolder)) {
      try {
        boolean result = libraryFolder.unsafeGet().mkdirs();

        if (result) {
          return libraryFolder;
        }
      } catch (Exception e) {
      }
    }

    return firstOption(asList(ContextCompat.getExternalFilesDirs(context, "Books")));
  }

  public Option<File> getTTSFolder() {
    return firstOption(asList(ContextCompat.getExternalCacheDirs(context)));
  }

  /**
   * Return the default folder path which is shown for the "scan for books" custom directory
   */
  private String getDefaultScanFolder() {
    return getStorageBase().unsafeGet().getAbsolutePath() + "/eBooks";
  }
}
