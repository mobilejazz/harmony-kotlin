package net.nightwhistler.htmlspanner.style;

import android.util.Log;

public class StyleValue {

  private Integer intValue;
  private Float floatValue;
  private Unit unit;

  public StyleValue(int intValue) {
    this.unit = Unit.PX;
    this.intValue = intValue;
  }

  public StyleValue(float floatValue, Unit unit) {
    this.floatValue = floatValue;
    this.unit = unit;
  }

  public enum Unit {PX, EM, PERCENTAGE}

  public static StyleValue parse(String value) {
    if (value.equals("0")) {
      return new StyleValue(0f, Unit.EM);
    }

    if (value.endsWith("px")) {
      try {
        final Integer intValue = Integer.parseInt(value.substring(0, value.length() - 2));
        return new StyleValue(intValue);
      } catch (NumberFormatException nfe) {
        Log.e("StyleValue", "Can't parse value: " + value);
        return null;
      }
    }

    if (value.endsWith("%")) {
      Log.d("StyleValue", "translating percentage " + value);
      try {
        final int percentage = Integer.parseInt(value.substring(0, value.length() - 1));
        final float floatValue = percentage / 100f;

        return new StyleValue(floatValue, Unit.PERCENTAGE);
      } catch (NumberFormatException nfe) {
        Log.e("StyleValue", "Can't parse font-size: " + value);
        return null;
      }
    }

    if (value.endsWith("em")) {
      try {
        final Float number = Float.parseFloat(value.substring(0, value.length() - 2));
        return new StyleValue(number, Unit.EM);
      } catch (NumberFormatException nfe) {
        Log.e("CSSCompiler", "Can't parse value: " + value);
        return null;
      }
    }

    return null;
  }

  public int getIntValue() {
    return this.intValue;
  }

  public float getFloatValue() {
    return this.floatValue;
  }

  public Unit getUnit() {
    return this.unit;
  }

  @Override
  public String toString() {
    if (intValue != null) {
      return "" + intValue + this.unit;
    } else {
      return "" + floatValue + this.unit;
    }
  }
}
