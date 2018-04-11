package net.nightwhistler.htmlspanner;

import android.graphics.Typeface;

public class FontFamily {

  private Typeface defaultTypeface;

  private Typeface boldTypeface;

  private Typeface italicTypeface;

  private Typeface boldItalicTypeface;

  private String name;

  public FontFamily(String name, Typeface defaultTypeFace) {
    this.name = name;
    this.defaultTypeface = defaultTypeFace;
  }

  public String getName() {
    return name;
  }

  public Typeface getBoldItalicTypeface() {
    return boldItalicTypeface;
  }

  public void setBoldItalicTypeface(Typeface boldItalicTypeface) {
    this.boldItalicTypeface = boldItalicTypeface;
  }

  public Typeface getBoldTypeface() {
    return boldTypeface;
  }

  public void setBoldTypeface(Typeface boldTypeface) {
    this.boldTypeface = boldTypeface;
  }

  public Typeface getDefaultTypeface() {
    return defaultTypeface;
  }

  public void setDefaultTypeface(Typeface defaultTypeface) {
    this.defaultTypeface = defaultTypeface;
  }

  public Typeface getItalicTypeface() {
    return italicTypeface;
  }

  public void setItalicTypeface(Typeface italicTypeface) {
    this.italicTypeface = italicTypeface;
  }

  public boolean isFakeBold() {
    return boldTypeface == null;
  }

  public boolean isFakeItalic() {
    return italicTypeface == null;
  }

  public String toString() {
    return name;
  }

}
