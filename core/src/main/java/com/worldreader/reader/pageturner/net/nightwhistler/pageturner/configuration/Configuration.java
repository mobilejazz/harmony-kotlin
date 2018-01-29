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
import android.preference.PreferenceManager;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageOffsets;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import org.json.JSONException;

import java.util.*;

import static jedi.option.Options.none;
import static jedi.option.Options.option;

/**
 * Application configuration class which provides a friendly API to the various settings available.
 */
public class Configuration {

  public static final String TAG = Configuration.class.getSimpleName();

  private static final String KEY_POS = "offset:";
  private static final String KEY_IDX = "index:";
  private static final String KEY_STRIP_WHITESPACE = "strip_whitespace";
  private static final String KEY_SCROLLING = "scrolling";
  private static final String KEY_TEXT_SIZE = "itext_size";
  private static final String KEY_MARGIN_H = "margin_h";
  private static final String KEY_MARGIN_V = "margin_v";
  private static final String KEY_LINE_SPACING = "line_spacing";
  private static final String KEY_NIGHT_MODE = "night_mode";
  private static final String KEY_FONT_FACE = "font_face";
  private static final String KEY_SERIF_FONT = "serif_font";
  private static final String KEY_SANS_SERIF_FONT = "sans_serif_font";
  private static final String PREFIX_DAY = "day";
  private static final String PREFIX_NIGHT = "night";
  private static final String PREFIX_CREAM = "cream";
  private static final String KEY_BRIGHTNESS = "bright";
  private static final String KEY_BACKGROUND = "bg";
  private static final String KEY_LINK = "link";
  private static final String KEY_TEXT = "text";
  private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
  private static final String KEY_OFFSETS = "offsets";
  private static final String KEY_READING_DIRECTION = "reading_direction";
  private static final String KEY_ALLOW_STYLE_COLOURS = "allow_style_colours";

  private SharedPreferences settings;
  private Context context;
  private Map<String, FontFamily> fontCache = new HashMap<>();

  public enum ColorProfile {
    DAY, NIGHT, CREAM
  }

  public enum ReadingDirection {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT
  }

  public enum ScrollStyle {
    ROLLING_BLIND, PAGE_TIMER
  }

  public static class FontSizes {

    public static final int SMALLEST = 12;
    public static final int SMALL = 14;
    public static final int NORMAL = 18;
    public static final int BIG = 20;
    public static final int BIGGEST = 24;
  }

  public Configuration(Context context) {
    this.settings = PreferenceManager.getDefaultSharedPreferences(context);
    this.context = context;
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

  private SharedPreferences getPrefsForBook(final String fileName) {
    final String bookHash = Integer.toHexString(fileName.hashCode());
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
      final PageOffsets offsets = PageOffsets.fromJSON(data);
      if (offsets == null || !offsets.isValid(this)) {
        return none();
      }
      return option(offsets.getOffsets());
    } catch (JSONException js) {
      Log.e(TAG, "Could not retrieve page offsets: " + js.getMessage());
      return none();
    }
  }

  public ReadingDirection getReadingDirection() {
    String value = settings.getString(KEY_READING_DIRECTION, isLayoutDirectionRTL()
                                                             ? ReadingDirection.RIGHT_TO_LEFT.name() :
                                                             ReadingDirection.LEFT_TO_RIGHT.name());
    return ReadingDirection.valueOf(value.toUpperCase(Locale.US));
  }

  private boolean isLayoutDirectionRTL() {
    return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
  }

  public void setLastPosition(String fileName, int position) {
    SharedPreferences bookPrefs = getPrefsForBook(fileName);

    updateValue(bookPrefs, KEY_POS, position);
  }

  public int getLastIndex(String fileName) {
    final SharedPreferences bookPrefs = getPrefsForBook(fileName);

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

  public void setColorProfile(ColorProfile profile) {
    if (profile == ColorProfile.DAY) {
      updateValue(KEY_NIGHT_MODE, ColorProfile.DAY.toString());
    } else if (profile == ColorProfile.NIGHT) {
      updateValue(KEY_NIGHT_MODE, ColorProfile.NIGHT.toString());
    } else {
      updateValue(KEY_NIGHT_MODE, ColorProfile.CREAM.toString());
    }
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
    final String basePath = "fonts/";

    final Typeface basic = Typeface.createFromAsset(context.getAssets(), basePath + fontFamily.getFont());
    final Typeface boldFace = Typeface.createFromAsset(context.getAssets(), basePath + fontFamily.getBoldFont());
    final Typeface italicFace = Typeface.createFromAsset(context.getAssets(), basePath + fontFamily.getItalicFont());

    final FontFamily fam = new FontFamily(key, basic);
    fam.setBoldTypeface(boldFace);
    fam.setItalicTypeface(italicFace);

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
    final String setting = KEY_ALLOW_STYLE_COLOURS;
    final boolean nightDefault = false;
    final boolean creamDefault = false;
    final boolean dayDefault = true;
    final ColorProfile colorProfile = getColourProfile();

    if (colorProfile == ColorProfile.NIGHT) {
      return settings.getBoolean(PREFIX_NIGHT + "_" + setting, nightDefault);
    } else if (colorProfile == ColorProfile.CREAM) {
      return settings.getBoolean(PREFIX_CREAM + "_" + setting, creamDefault);
    } else {
      return settings.getBoolean(PREFIX_DAY + "_" + setting, dayDefault);
    }
  }

  private int getProfileSetting(String setting, int dayDefault, int nightDefault, int creamDefault) {
    if (getColourProfile() == ColorProfile.NIGHT) {
      return settings.getInt(PREFIX_NIGHT + "_" + setting, nightDefault);
    } else if (getColourProfile() == ColorProfile.CREAM) {
      return settings.getInt(PREFIX_CREAM + "_" + setting, creamDefault);
    } else {
      return settings.getInt(PREFIX_DAY + "_" + setting, dayDefault);
    }
  }
}
