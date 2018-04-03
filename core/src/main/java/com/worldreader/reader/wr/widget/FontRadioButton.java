package com.worldreader.reader.wr.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import com.worldreader.core.R;
import com.worldreader.reader.wr.helper.FontManager;
import net.nightwhistler.htmlspanner.FontFamily;

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

  private boolean applyCustomTypeface(Context context, String font) {
    final FontManager fm = FontManager.getInstance(context);
    final FontFamily family = fm.getFontFamily(font);
    setTypeface(family.getDefaultTypeface());
    return true;
  }
}