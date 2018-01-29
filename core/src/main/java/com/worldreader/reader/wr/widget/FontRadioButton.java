package com.worldreader.reader.wr.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import com.worldreader.core.R;

import java.util.*;

public class FontRadioButton extends AppCompatRadioButton {

  public FontRadioButton(Context context) {
    super(context);
  }

  public FontRadioButton(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    applyCustomTypeface(context, attributeSet);
  }

  public FontRadioButton(Context context, AttributeSet attributeSet, int defStyle) {
    super(context, attributeSet, defStyle);
    applyCustomTypeface(context, attributeSet);
  }

  private void applyCustomTypeface(Context context, AttributeSet attributeSet) {
    final TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomTypeface);
    final String font = typedArray.getString(R.styleable.CustomTypeface_custom_typeface);
    applyCustomTypeface(context, font);
    typedArray.recycle();
  }

  private boolean applyCustomTypeface(Context context, String asset) {
    setTypeface(FontCache.getTypeface(context, asset));
    return true;
  }

  public static class FontCache {

    private static final HashMap<String, Typeface> FONT_CACHE = new HashMap<>();

    public static Typeface getTypeface(Context context, String customFont) {
      Typeface typeface = FONT_CACHE.get(customFont);
      if (typeface == null) {
        try {
          typeface = Typeface.createFromAsset(context.getAssets(), customFont);
        } catch (Exception e) {
          return null;
        }
        FONT_CACHE.put(customFont, typeface);
      }
      return typeface;
    }
  }
}