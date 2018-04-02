package com.worldreader.reader.wr.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import com.google.gson.Gson;
import com.worldreader.core.R;
import com.worldreader.reader.wr.helper.FontManager;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;

import java.util.*;

import static jedi.option.Options.none;

public class ReaderConfig {

  public static final String TAG = ReaderConfig.class.getSimpleName();

  private static final String READER_PREFERENCES = "READER.PREFERENCES";
  private static final String READER_PREFERENCES_BOOK = "READER.PREFERENCES.BOOK";

  private static final String KEY_CURRENT_BOOK_POSITION = "offset:";
  private static final String KEY_CURRENT_BOOK_INDEX = "index:";
  public static final String KEY_OFFSETS = "KEY.OFFSETS";

  public static final String KEY_AUTO_SAVE_SETTINGS_ENABLED = "KEY.AUTO.SAVE.SETTINGS.ENABLED";
  public static final String KEY_STRIP_WHITESPACE = "KEY.STRIP.WHITESPACE";
  public static final String KEY_SCROLLING = "KEY.SCROLLING";
  public static final String KEY_SHARE_ENABLED = "KEY.SHARE.ENABLED";
  public static final String KEY_TEXT_SIZE = "KEY.TEXT.SIZE";
  public static final String KEY_MARGIN_H = "KEY.MARGIN.H";
  public static final String KEY_MARGIN_V = "KEY.MARGIN.V";
  public static final String KEY_LINE_SPACING = "KEY.LINE.SPACING";
  public static final String KEY_BRIGHTNESS = "KEY.PROFILE.BRIGHTNESS";
  public static final String KEY_READING_DIRECTION = "KEY.READING.DIRECTION";
  public static final String KEY_CURRENT_THEME = "KEY.CURRENT.THEME";
  public static final String KEY_CURRENT_FONT = "KEY.CURRENT.FONT";
  public static final String KEY_SERIF_FONT = "KEY.SERIF.FONT";
  public static final String KEY_SANS_SERIF_FONT = "KEY.SANS.SERIF.FONT";

  private static final String KEY_FONT_FACE = "KEY.FONT.FACE";

  private final Context context;
  private final SharedPreferences sp;
  private final Gson gson;
  private final FontManager fm;

  private final Map<String, Object> config;

  public enum Theme {
    DAY(Color.BLUE, Color.BLACK, Color.WHITE, R.drawable.shape_button_read_options_white_background),
    CREAM(Color.parseColor("#ffa500"), Color.GRAY, Color.parseColor("#f6f2e5"), R.drawable.shape_button_read_options_cream_background),
    NIGHT(Color.RED, Color.parseColor("#6f431c"), Color.BLACK, R.drawable.shape_button_read_options_black_background);

    public int linkColor;
    public int textColor;
    public int bgColor;
    public int drawableRes;

    Theme(int linkColor, int textColor, int bgColor, @DrawableRes int drawableRes) {
      this.linkColor = linkColor;
      this.textColor = textColor;
      this.bgColor = bgColor;
      this.drawableRes = drawableRes;
    }
  }

  @Deprecated
  public enum ColorProfile {
    DAY,
    NIGHT,
    CREAM
  }

  public enum ReadingDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
  }

  public static class FontSizes {

    public static final int SMALLEST = 12;
    public static final int SMALL = 14;
    public static final int NORMAL = 18;
    public static final int BIG = 20;
    public static final int BIGGEST = 24;
  }

  public ReaderConfig(Context context, Gson gson, Map<String, Object> config) {
    this.context = context;
    this.sp = context.getSharedPreferences(READER_PREFERENCES, Context.MODE_PRIVATE);
    this.gson = gson;
    this.config = new HashMap<>(config);
    fm = FontManager.getInstance(context);
  }

  ////////

  private SharedPreferences getReaderConfigForBook(String bookId) {
    final String bookHash = Integer.toHexString(bookId.hashCode());
    return context.getSharedPreferences(bookHash, Context.MODE_PRIVATE);
  }

  public void setPageOffsets(String bookId, List<List<Integer>> offsets) {
    // TODO Fix this
    //SharedPreferences bookPrefs = getReaderConfigForBook(bookId);
    //
    //PageOffsets offsetsObject = PageOffsets.fromValues(this, offsets);
    //
    //try {
    //  String json = offsetsObject.toJSON();
    //  updateValue(bookPrefs, KEY_OFFSETS, json);
    //} catch (JSONException js) {
    //  Log.e(TAG, "Error storing page offsets: " + js.getMessage());
    //}
  }

  public Option<List<List<Integer>>> getPageOffsets(String bookId) {
    // TODO: 02/04/2018 Fix this
    return none();

    //SharedPreferences bookPrefs = getReaderConfigForBook(bookId);
    //
    //String data = bookPrefs.getString(KEY_OFFSETS, "");
    //
    //if (data.length() == 0) {
    //  return none();
    //}
    //
    //try {
    //  final PageOffsets offsets = PageOffsets.fromJSON(data);
    //  if (offsets == null || !offsets.isValid(this)) {
    //    return none();
    //  }
    //  return option(offsets.getOffsets());
    //} catch (JSONException js) {
    //  Log.e(TAG, "Could not retrieve page offsets: " + js.getMessage());
    //  return none();
    //}
  }

  public void setLastPosition(String bookId, int position) {
    // TODO: 02/04/2018 Fix this
    //final SharedPreferences bookPrefs = getReaderConfigForBook(bookId);
    //updateValue(bookPrefs, KEY_CURRENT_BOOK_POSITION, position);
  }

  public int getLastPosition(String bookId) {
    // TODO: 02/04/2018 Fix this
    //final SharedPreferences bookPrefs = getReaderConfigForBook(bookId);
    //return bookPrefs.getInt(KEY_CURRENT_BOOK_POSITION, -1);
    return -1;
  }

  public void setLastIndex(String bookId, int index) {
    // TODO: 02/04/2018 Fix this
    //final SharedPreferences bookPrefs = getReaderConfigForBook(bookId);
    //updateValue(bookPrefs, KEY_CURRENT_BOOK_INDEX, index);
  }

  public int getLastIndex(String fileName) {
    //final SharedPreferences bookPrefs = getReaderConfigForBook(fileName);
    //return bookPrefs.getInt(KEY_CURRENT_BOOK_INDEX, -1);
    return -1;
  }

  /////////

  public ReadingDirection getReadingDirection() {
    return (ReadingDirection) config.get(KEY_READING_DIRECTION);
  }

  public boolean isStripWhiteSpaceEnabled() {
    return ((boolean) config.get(KEY_STRIP_WHITESPACE));
  }

  public boolean isShareEnabled() {
    return (boolean) config.get(KEY_SHARE_ENABLED);
  }

  public void setTextSize(int size) {
    // TODO: 02/04/2018 Finish implementation
  }

  public int getTextSize() {
    return ((int) config.get(KEY_TEXT_SIZE));
  }

  public int getHorizontalMargin() {
    return ((int) config.get(KEY_MARGIN_H));
  }

  public int getVerticalMargin() {
    return ((int) config.get(KEY_MARGIN_V));
  }

  public int getLineSpacing() {
    return ((int) config.get(KEY_LINE_SPACING));
  }

  public Theme getTheme() {
    return (Theme) config.get(KEY_CURRENT_THEME);
  }

  public void setTheme(Theme theme) {
    // TODO: 02/04/2018 Finish implementation
    save();
  }

  public FontFamily getDefaultFontFamily() {
    return getFontFamily(KEY_FONT_FACE, FontFamilies.LORA);
  }

  public FontFamily getSerifFontFamily() {
    return getFontFamily(KEY_SERIF_FONT, FontFamilies.LORA);
  }

  public void setSerifFontFamily(FontFamilies.FontFamily fontFamily) {
    // TODO: 02/04/2018 Fix this
    //updateValue(sp, KEY_SERIF_FONT, fontFamily.getName());
    save();
  }

  public String getSerifFontFamilyString() {
    return sp.getString(KEY_SERIF_FONT, FontFamilies.LORA.getName());
  }

  public FontFamily getSansSerifFontFamily() {
    return getFontFamily(KEY_SANS_SERIF_FONT, FontFamilies.OPEN_SANS);
  }

  public void setBrightness(int level) {
    // TODO: 02/04/2018 Implement this
    save();
  }

  public int getBrightness() {
    return ((int) config.get(KEY_BRIGHTNESS));
  }

  public boolean isBookCssStylesEnabled() {
    final Theme theme = getTheme();
    return theme == Theme.DAY;
  }

  private boolean isAutoSaveEnabled() {
    return (boolean) config.get(KEY_AUTO_SAVE_SETTINGS_ENABLED);
  }

  private void save() {
    if (isAutoSaveEnabled()) {
      final SharedPreferences.Editor editor = sp.edit();
      final String json = gson.toJson(config);
      editor.putString("settings", json);
      editor.apply();
    }
  }
}
