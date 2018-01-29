package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration;

public class FontFamilies {

  public enum FontExtension {
    OTF("otf"),
    TTF("ttf");

    private final String extension;

    FontExtension(String extension) {
      this.extension = extension;
    }

    public String getExtension() {
      return extension;
    }
  }

  public static final class FAMILIES {

    public static final String SANS = "sans";
    public static final String SERIF = "serif";
    public static final String MONO = "mono";
    public static final String DEFAULT = "default";

    private FAMILIES() {
      throw new AssertionError("Not intended for instantiation!");
    }
  }

  public static final class LORA {

    public static final FontFamily DEFAULT =
        new FontFamily("Lora", FontExtension.TTF, "Bold", "Italic", "BoldItalic");

    public LORA() {
      throw new AssertionError("Not intended for instantiation!");
    }
  }

  public static final class OPEN_SANS {

    public static final FontFamily DEFAULT =
        new FontFamily("OpenSans", FontExtension.TTF, "Bold", "Italic", null);

    public OPEN_SANS() {
      throw new AssertionError("Not intended for instantiation!");
    }
  }

  public static final class POPPINS {

    public static final FontFamily DEFAULT =
        new FontFamily("Poppins", FontExtension.TTF, "Bold", "Light", null);

    public POPPINS() {
      throw new AssertionError("Not intended for instantiation!");
    }
  }

  public static class FontFamily {

    public final String fontName;
    public FontExtension fontExtension;

    public String bold;
    public String italic;
    public String boldItalic;

    public FontFamily(String fontName, FontExtension fontExtension, String bold, String italic,
        String boldItalic) {
      this.fontName = fontName;
      this.fontExtension = fontExtension;
      this.bold = bold;
      this.italic = italic;
      this.boldItalic = boldItalic;
    }

    public String getFont() {
      return fontName + "." + fontExtension.getExtension();
    }

    public String getBoldFont() {
      return fontName + "-" + bold + "." + fontExtension.getExtension();
    }

    public String getItalicFont() {
      return fontName + "-" + italic + "." + fontExtension.getExtension();
    }

    public String getBoldItalicFont() {
      return fontName + "-" + boldItalic + "." + fontExtension.getExtension();
    }
  }

  private FontFamilies() {
    throw new AssertionError("FontFamilies not intended fo instantiation!");
  }
}
