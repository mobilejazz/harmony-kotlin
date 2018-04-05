package com.worldreader.reader.wr.configuration;

import android.text.TextUtils;

import java.io.*;

public class ReaderFontFamilies {

  public static final FontFamily POPPINS = new FontFamily("Poppins", "ttf", "Regular", "Bold", "Light");
  public static final FontFamily OPEN_SANS = new FontFamily("OpenSans", "ttf", "Regular", "Bold", "Italic");
  public static final FontFamily LORA = new FontFamily("Lora", "ttf", "Regular", "Bold", "Italic");

  public static FontFamily fromName(String name) {
    if (TextUtils.isEmpty(name)) {
      throw new IllegalArgumentException("Font name is not valid!");
    }

    final String font = name.toUpperCase();

    switch (font) {
      case "POPPINS":
        return POPPINS;
      case "OPEN_SANS":
      case "OPENSANS":
        return OPEN_SANS;
      case "LORA":
        return LORA;
      default:
        throw new IllegalArgumentException("Font " + name + " is not valid!");
    }
  }

  public static class FontFamily implements Serializable {

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
      return name + "-" + regular + "." + extension;
    }

    public String getBoldFont() {
      return name + "-" + bold + "." + extension;
    }

    public String getItalicFont() {
      return name + "-" + italic + "." + extension;
    }
  }

  private ReaderFontFamilies() {
    throw new AssertionError("ReaderFontFamilies not intended fo instantiation!");
  }
}
