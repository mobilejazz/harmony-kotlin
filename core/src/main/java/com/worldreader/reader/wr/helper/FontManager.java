package com.worldreader.reader.wr.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import com.worldreader.reader.wr.configuration.ReaderFontFamilies;
import net.nightwhistler.htmlspanner.FontFamily;

import java.io.*;
import java.util.*;

public class FontManager {

  private static FontManager INSTANCE;

  private static final String FOLDER = "fonts";

  private static final HashMap<String, FontFamily> FONT_CACHE = new HashMap<>();

  private final AssetManager manager;

  public static FontManager getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new FontManager(context);
    }
    return INSTANCE;
  }

  private FontManager(Context context) {
    this.manager = context.getAssets();
  }

  public FontFamily getFontFamily(String f) {
    final ReaderFontFamilies.FontFamily family = ReaderFontFamilies.fromName(f);
    return getFontFamily(family);
  }

  public FontFamily getFontFamily(ReaderFontFamilies.FontFamily f) {
    final String key = f.getName();

    if (!FONT_CACHE.containsKey(key)) {
      final FontFamily family = loadFontFamilyFromAssets(key, f);
      FONT_CACHE.put(key, family);
    }

    return FONT_CACHE.get(key);
  }

  private FontFamily loadFontFamilyFromAssets(String name, ReaderFontFamilies.FontFamily f) {
    final Typeface regular = Typeface.createFromAsset(manager, FOLDER + File.separator + f.getRegularFont());
    final Typeface bold = Typeface.createFromAsset(manager, FOLDER + File.separator + f.getBoldFont());
    final Typeface italic = Typeface.createFromAsset(manager, FOLDER + File.separator + f.getItalicFont());

    final FontFamily fm = new FontFamily(name, null);
    fm.setDefaultTypeface(regular);
    fm.setBoldTypeface(bold);
    fm.setItalicTypeface(italic);

    return fm;
  }
}
