package net.nightwhistler.htmlspanner;

import android.graphics.Typeface;
import android.util.Log;

public class SystemFontResolver implements FontResolver {

  private FontFamily defaultFont;

  private FontFamily serifFont;
  private FontFamily sansSerifFont;
  private FontFamily monoSpaceFont;

  public SystemFontResolver() {
    this.defaultFont = new FontFamily("default", Typeface.DEFAULT);
    this.serifFont = new FontFamily("serif", Typeface.SERIF);
    this.sansSerifFont = new FontFamily("sans-serif", Typeface.SANS_SERIF);
    this.monoSpaceFont = new FontFamily("monospace", Typeface.MONOSPACE);
  }

  public FontFamily getDefaultFont() {
    return defaultFont;
  }

  public void setDefaultFont(FontFamily defaultFont) {
    this.defaultFont = defaultFont;
  }

  public FontFamily getSansSerifFont() {
    return sansSerifFont;
  }

  public void setSansSerifFont(FontFamily sansSerifFont) {
    this.sansSerifFont = sansSerifFont;
  }

  public FontFamily getSerifFont() {
    return serifFont;
  }

  public void setSerifFont(FontFamily serifFont) {
    this.serifFont = serifFont;
  }

  public FontFamily getMonoSpaceFont() {
    return monoSpaceFont;
  }

  public FontFamily getFont(String name) {

    if (name != null && name.length() > 0) {

      String[] parts = name.split(",(\\s)*");

      for (String part : parts) {
        String fontName = part;

        if (fontName.startsWith("\"") && fontName.endsWith("\"")) {
          fontName = fontName.substring(1, fontName.length() - 1);
        }

        if (fontName.startsWith("\'") && fontName.endsWith("\'")) {
          fontName = fontName.substring(1, fontName.length() - 1);
        }

        FontFamily fam = resolveFont(fontName);
        if (fam != null) {
          return fam;
        }
      }
    }

    return getDefaultFont();
  }

  private FontFamily resolveFont(String name) {
    Log.d("SystemFontResolver", "Trying to resolve font " + name);

    if (name.equalsIgnoreCase("serif")) {
      return getSerifFont();
    } else if (name.equalsIgnoreCase("sans-serif")) {
      return getSansSerifFont();
    } else if (name.equalsIgnoreCase("monospace")) {
      return monoSpaceFont;
    }

    return null;
  }

}
