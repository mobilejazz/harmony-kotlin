package com.worldreader.reader.wr.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import com.google.gson.Gson;
import com.worldreader.core.R;
import com.worldreader.reader.wr.helper.FontManager;
import net.nightwhistler.htmlspanner.FontFamily;

import java.util.*;

public class ReaderConfig {

  public static final String TAG = ReaderConfig.class.getSimpleName();

  private static final String READER_PREFERENCES = "reader.preferences";

  private static Map<String, Object> CURRENT_CONFIG;

  private static final String KEY_PERSISTENT_SETTINGS_ENABLED = "KEY.PERSISTENT.SETTINGS.ENABLED";
  private static final String KEY_STRIP_WHITESPACE = "KEY.STRIP.WHITESPACE";
  private static final String KEY_SCROLLING = "KEY.SCROLLING";
  private static final String KEY_SHARE_ENABLED = "KEY.SHARE.ENABLED";
  private static final String KEY_TEXT_SIZE = "KEY.TEXT.SIZE";
  private static final String KEY_MARGIN_H = "KEY.MARGIN.H";
  private static final String KEY_MARGIN_V = "KEY.MARGIN.V";
  private static final String KEY_LINE_SPACING = "KEY.LINE.SPACING";
  private static final String KEY_BRIGHTNESS = "KEY.PROFILE.BRIGHTNESS";
  private static final String KEY_READING_DIRECTION = "KEY.READING.DIRECTION";
  private static final String KEY_CURRENT_THEME = "KEY.CURRENT.THEME";
  private static final String KEY_SERIF_FONT = "KEY.SERIF.FONT";
  private static final String KEY_SANS_SERIF_FONT = "KEY.SANS.SERIF.FONT";

  private final SharedPreferences sp;
  private final Gson gson;
  private final FontManager fm;
  private final Map<String, Object> defaultConfig;

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

  public enum ReadingDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
  }

  public ReaderConfig(Context c, Gson g, Map<String, Object> defaults) {
    sp = c.getSharedPreferences(READER_PREFERENCES, Context.MODE_PRIVATE);
    gson = g;
    fm = FontManager.getInstance(c);
    defaultConfig = defaults;
    if (CURRENT_CONFIG == null) {
      CURRENT_CONFIG = new HashMap<>(defaultConfig);
    }
  }

  public ReadingDirection getReadingDirection() {
    return (ReadingDirection) CURRENT_CONFIG.get(KEY_READING_DIRECTION);
  }

  public boolean isStripWhiteSpaceEnabled() {
    return ((boolean) CURRENT_CONFIG.get(KEY_STRIP_WHITESPACE));
  }

  public boolean isShareEnabled() {
    return (boolean) CURRENT_CONFIG.get(KEY_SHARE_ENABLED);
  }

  public int getTextSize() {
    return ((int) CURRENT_CONFIG.get(KEY_TEXT_SIZE));
  }

  public void setTextSize(int size) {
    CURRENT_CONFIG.put(KEY_TEXT_SIZE, size);
    save();
  }

  public int getHorizontalMargin() {
    return ((int) CURRENT_CONFIG.get(KEY_MARGIN_H));
  }

  public int getVerticalMargin() {
    return ((int) CURRENT_CONFIG.get(KEY_MARGIN_V));
  }

  public int getLineSpacing() {
    return ((int) CURRENT_CONFIG.get(KEY_LINE_SPACING));
  }

  public Theme getTheme() {
    return (Theme) CURRENT_CONFIG.get(KEY_CURRENT_THEME);
  }

  public void setTheme(Theme theme) {
    CURRENT_CONFIG.put(KEY_CURRENT_THEME, theme);
    save();
  }

  public FontFamily getDefaultFontFamily() {
    final ReaderFontFamilies.FontFamily currentFontFamily = (ReaderFontFamilies.FontFamily) CURRENT_CONFIG.get(KEY_SERIF_FONT);
    return fm.getFontFamily(currentFontFamily);
  }

  public void setDefaultFontFamily(ReaderFontFamilies.FontFamily f) {
    CURRENT_CONFIG.put(KEY_SERIF_FONT, f);
    save();
  }

  public FontFamily getSerifFontFamily() {
    final ReaderFontFamilies.FontFamily serifFontFamily = (ReaderFontFamilies.FontFamily) CURRENT_CONFIG.get(KEY_SERIF_FONT);
    return fm.getFontFamily(serifFontFamily);
  }

  public FontFamily getSansSerifFontFamily() {
    final ReaderFontFamilies.FontFamily serifFontFamily = (ReaderFontFamilies.FontFamily) CURRENT_CONFIG.get(KEY_SANS_SERIF_FONT);
    return fm.getFontFamily(serifFontFamily);
  }

  public int getBrightness() {
    return ((int) CURRENT_CONFIG.get(KEY_BRIGHTNESS));
  }

  public void setBrightness(int level) {
    CURRENT_CONFIG.put(KEY_BRIGHTNESS, level);
    save();
  }

  public boolean isBookCssStylesEnabled() {
    final Theme theme = getTheme();
    return theme == Theme.DAY;
  }

  public void resetSettings() {
    CURRENT_CONFIG.clear();
    CURRENT_CONFIG.putAll(defaultConfig);
  }

  private boolean isPersistentSettingsEnabled() {
    return (boolean) CURRENT_CONFIG.get(KEY_PERSISTENT_SETTINGS_ENABLED);
  }

  private void save() {
    if (isPersistentSettingsEnabled()) {
      final SharedPreferences.Editor editor = sp.edit();
      final String json = gson.toJson(CURRENT_CONFIG);
      editor.putString("settings", json);
      editor.apply();
    }
  }

  public static class Factory {

    public static Map<String, Object> createStudentConfig(Context context) {
      return Collections.unmodifiableMap(new HashMap<String, Object>(createDefaultConfig(context)) {{
        put(KEY_PERSISTENT_SETTINGS_ENABLED, false);
        put(KEY_SHARE_ENABLED, false);
        put(KEY_SERIF_FONT, ReaderFontFamilies.POPPINS);
      }});
    }

    public static Map<String, Object> createR2KConfig(Context context) {
      return Collections.unmodifiableMap(createDefaultConfig(context));
    }

    public static Map<String, Object> createMainLibraryConfig(Context context) {
      return Collections.unmodifiableMap(createDefaultConfig(context));
    }

    private static Map<String, Object> createDefaultConfig(final Context context) {
      return new HashMap<String, Object>() {{
        put(KEY_PERSISTENT_SETTINGS_ENABLED, true);
        put(KEY_STRIP_WHITESPACE, false);
        put(KEY_SCROLLING, false);
        put(KEY_SHARE_ENABLED, true);
        put(KEY_TEXT_SIZE, context.getResources().getInteger(R.integer.default_reader_font_size));
        put(KEY_MARGIN_H, 60); // px
        put(KEY_MARGIN_V, 60); // px
        put(KEY_BRIGHTNESS, 50); // 50%
        put(KEY_LINE_SPACING, 0);
        put(KEY_READING_DIRECTION, getReadingDirection());
        put(KEY_CURRENT_THEME, Theme.DAY);
        put(KEY_SERIF_FONT, ReaderFontFamilies.LORA);
        put(KEY_SANS_SERIF_FONT, ReaderFontFamilies.OPEN_SANS);
      }};
    }

    private static ReadingDirection getReadingDirection() {
      return isLayoutDirectionRTL() ? ReadingDirection.RIGHT_TO_LEFT : ReadingDirection.LEFT_TO_RIGHT;
    }

    private static boolean isLayoutDirectionRTL() {
      return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
  }
}
