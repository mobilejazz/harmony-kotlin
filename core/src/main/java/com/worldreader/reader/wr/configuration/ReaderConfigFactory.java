package com.worldreader.reader.wr.configuration;

import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;

import java.util.*;

import static com.worldreader.reader.wr.configuration.ReaderConfig.*;

public class ReaderConfigFactory {

  public static Map<String, Object> createStudentConfig() {
    return new HashMap<String, Object>(createDefaultConfig()) {{
      put(KEY_AUTO_SAVE_SETTINGS_ENABLED, false);
      put(KEY_CURRENT_FONT, FontFamilies.POPPINS);
      put(KEY_SERIF_FONT, FontFamilies.POPPINS);
    }};
  }

  public static Map<String, Object> createR2KConfig() {
    return createDefaultConfig();
  }

  public static Map<String, Object> createMainLibraryConfig() {
    return createDefaultConfig();
  }

  private static Map<String, Object> createDefaultConfig() {
    return new HashMap<String, Object>() {{
      put(KEY_AUTO_SAVE_SETTINGS_ENABLED, true);
      put(KEY_STRIP_WHITESPACE, false);
      put(KEY_SCROLLING, false);
      put(KEY_SHARE_ENABLED, true);
      put(KEY_TEXT_SIZE, FontSizes.BIG);
      put(KEY_MARGIN_H, 60); // px
      put(KEY_MARGIN_V, 60); // px
      put(KEY_BRIGHTNESS, 50); // 50%
      put(KEY_LINE_SPACING, 0);
      put(KEY_READING_DIRECTION, getReadingDirection());
      put(KEY_CURRENT_THEME, Theme.DAY);
      put(KEY_CURRENT_FONT, FontFamilies.LORA);
      put(KEY_SERIF_FONT, FontFamilies.LORA);
      put(KEY_SANS_SERIF_FONT, FontFamilies.OPEN_SANS);
    }};
  }

  private static ReadingDirection getReadingDirection() {
    return isLayoutDirectionRTL() ? ReadingDirection.RIGHT_TO_LEFT : ReadingDirection.LEFT_TO_RIGHT;
  }

  private static  boolean isLayoutDirectionRTL() {
    return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
  }

}
