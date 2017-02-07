package com.worldreader.core.common.intents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.*;
import java.util.*;

public class Intents {

  public static IntentBuilder create() {
    return new IntentBuilder();
  }

  public static IntentBuilder with(Context packageContext, Class<?> cls) {
    return new IntentBuilder(packageContext, cls);
  }

  public static IntentBuilder with(String action) {
    return new IntentBuilder(action);
  }

  public static IntentBuilder with(Intent intent) {
    return new IntentBuilder(intent);
  }

  public static class IntentBuilder {

    private Intent intent;

    public IntentBuilder() {
      intent = new Intent();
    }

    public IntentBuilder(Context packageContext, Class<?> cls) {
      intent = new Intent(packageContext, cls);
    }

    public IntentBuilder(String action) {
      intent = new Intent(action);
    }

    public IntentBuilder(Intent intent) {
      intent = new Intent(intent);
    }

    public IntentBuilder putExtra(String key, Serializable s) {
      intent.putExtra(key, s);
      return this;
    }

    public IntentBuilder putExtra(String key, Parcelable p) {
      intent.putExtra(key, p);
      return this;
    }

    public IntentBuilder putExtra(String key, boolean b) {
      intent.putExtra(key, b);
      return this;
    }

    public IntentBuilder putExtra(String key, Boolean b) {
      intent.putExtra(key, b);
      return this;
    }

    public IntentBuilder putExtra(String key, double d) {
      intent.putExtra(key, d);
      return this;
    }

    public IntentBuilder putExtra(String key, Double d) {
      intent.putExtra(key, d);
      return this;
    }

    public IntentBuilder putExtra(String key, long l) {
      intent.putExtra(key, l);
      return this;
    }

    public IntentBuilder putExtra(String key, Long l) {
      intent.putExtra(key, l);
      return this;
    }

    public IntentBuilder putExtra(String key, int i) {
      intent.putExtra(key, i);
      return this;
    }

    public IntentBuilder putExtra(String key, Integer i) {
      intent.putExtra(key, i);
      return this;
    }

    public IntentBuilder putExtra(String key, String s) {
      intent.putExtra(key, s);
      return this;
    }

    public IntentBuilder putExtra(String key, String[] s) {
      intent.putExtra(key, s);
      return this;
    }

    public IntentBuilder putExtra(String key, CharSequence c) {
      intent.putExtra(key, c);
      return this;
    }

    public IntentBuilder putExtras(String key, Intent i) {
      intent.putExtra(key, i);
      return this;
    }

    public IntentBuilder putExtras(String key, Bundle b) {
      intent.putExtra(key, b);
      return this;
    }

    public IntentBuilder putCharSequenceArrayListExtra(String key, ArrayList<CharSequence> s) {
      intent.putCharSequenceArrayListExtra(key, s);
      return this;
    }

    public IntentBuilder putIntegerArrayListExtra(String key, ArrayList<Integer> s) {
      intent.putIntegerArrayListExtra(key, s);
      return this;
    }

    public IntentBuilder putParcelableArrayListExtra(String key, ArrayList<Parcelable> s) {
      intent.putParcelableArrayListExtra(key, s);
      return this;
    }

    public IntentBuilder putStringArrayListExtra(String key, ArrayList<String> s) {
      intent.putStringArrayListExtra(key, s);
      return this;
    }

    public IntentBuilder setAction(String action) {
      intent.setAction(action);
      return this;
    }

    public IntentBuilder setType(String type) {
      intent.setType(type);
      return this;
    }

    public IntentBuilder addCategory(String category) {
      intent.addCategory(category);
      return this;
    }

    public IntentBuilder setData(Uri uri) {
      intent.setData(uri);
      return this;
    }

    public IntentBuilder setFlags(int flags) {
      intent.setFlags(flags);
      return this;
    }

    public Intent build() {
      return intent;
    }
  }
}
