package com.worldreader.reader.wr.configuration;

import android.text.TextUtils;

public class FontFamilies {

  public static final FontFamily POPPINS = new FontFamily("Poppins", "ttf", "Regular", "Bold", "Light");
  public static final FontFamily OPEN_SANS = new FontFamily("OpenSans", "ttf", "Regular", "Bold", "Italic");
  public static final FontFamily LORA = new FontFamily("Lora", "ttf", "Regular", "Bold", "Italic");

  public FontFamily fromName(String name) {
    if (TextUtils.isEmpty(name)) {
      throw new IllegalArgumentException("Font name is not valid!");
    }

    final String font = name.toUpperCase();

    switch (font) {
      case "POPPINS":
        return POPPINS;
      case "OPEN_SANS":
        return OPEN_SANS;
      case "LORA":
        return LORA;
      default:
        throw new IllegalArgumentException("Font name is not valid!");
    }
  }

  public static class FontFamily {

    private final String name;
    private final String extension;
    private final String regular;
    private final String bold;
    private final String italic;

    public FontFamily(String name, String extension, String regular, String bold, String italic) {
      this.name = name;
      this.extension = extension;
      this.regular = regular;
      this.bold = bold;
      this.italic = italic;
    }

    public String getName() {
      return name;
    }

    public String getRegularFont() {
      return regular + "." + extension;
    }

    public String getBoldFont() {
      return regular + "-" + bold + "." + extension;
    }

    public String getItalicFont() {
      return regular + "-" + italic + "." + extension;
    }
  }

  private FontFamilies() {
    throw new AssertionError("FontFamilies not intended fo instantiation!");
  }
}
