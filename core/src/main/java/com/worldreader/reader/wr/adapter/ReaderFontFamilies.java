package com.worldreader.reader.wr.adapter;

import android.support.annotation.StringRes;
import com.worldreader.core.R;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.FontFamilies;

public enum ReaderFontFamilies {

  ORIGINAL(FontFamilies.OPEN_SANS.DEFAULT, R.string.ls_book_reading_font_family_open_sans),

  LORA(FontFamilies.LORA.DEFAULT, R.string.ls_book_reading_font_family_lora),

  POPPINS(FontFamilies.POPPINS.DEFAULT, R.string.ls_book_reading_font_family_poppins);

  private FontFamilies.FontFamily fontFamily;
  private int stringId;

  ReaderFontFamilies(FontFamilies.FontFamily fontFamily, @StringRes int stringId) {
    this.fontFamily = fontFamily;
    this.stringId = stringId;
  }

  public FontFamilies.FontFamily getFontFamily() {
    return fontFamily;
  }

  public int getStringId() {
    return this.stringId;
  }

  public static ReaderFontFamilies getByFontName(String serifFontFamily) {
    for (ReaderFontFamilies family : values()) {
      if (family.getFontFamily().fontName.equals(serifFontFamily)) {
        return family;
      }
    }
    return ORIGINAL;
  }
}
