package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration;

public class FontFamilies {

  public enum Extension {
    OTF("otf"),
    TTF("ttf");

    private final String extension;

    Extension(String extension) {
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
        new FontFamily("Lora", Extension.TTF, "Bold", "Italic", "BoldItalic");

    public LORA() {
      throw new AssertionError("Not intended for instantiation!");
    }
  }

  public static final class OPEN_SANS {

    public static final FontFamily DEFAULT =
        new FontFamily("OpenSans", Extension.TTF, "Bold", "Italic", null);

    public OPEN_SANS() {
      throw new AssertionError("Not intended for instantiation!");
    }
  }

  public static final class POPPINS {

    public static final FontFamily DEFAULT =
        new FontFamily("Poppins", Extension.TTF, "Bold", "Light", null);

    public POPPINS() {
      throw new AssertionError("Not intended for instantiation!");
    }
  }

  public static class FontFamily {

    public final String fontName;
    public Extension extension;

    public String bold;
    public String italic;
    public String boldItalic;

    public FontFamily(String fontName, Extension extension, String bold, String italic, String boldItalic) {
      this.fontName = fontName;
      this.extension = extension;
      this.bold = bold;
      this.italic = italic;
      this.boldItalic = boldItalic;
    }

    public String getFont() {
      return fontName + "." + extension.getExtension();
    }

    public String getBoldFont() {
      return fontName + "-" + bold + "." + extension.getExtension();
    }

    public String getItalicFont() {
      return fontName + "-" + italic + "." + extension.getExtension();
    }

    public String getBoldItalicFont() {
      return fontName + "-" + boldItalic + "." + extension.getExtension();
    }
  }

  private FontFamilies() {
    throw new AssertionError("FontFamilies not intended fo instantiation!");
  }
}
