package com.worldreader.reader.wr.models;

import com.worldreader.reader.wr.configuration.ReaderConfig;

import java.util.*;

/**
 * This class stores meta information needed by the reader to allow continue reading where the user left.
 * */
public class ReaderBookMetadata {

  // Reader attributes snapshot
  private int fontSize;
  private String fontFamily;
  private int vMargin;
  private int hMargin;
  private int lineSpacing;

  // Offsets
  public List<List<Integer>> offsets = new ArrayList<>();

  // Positioning and index
  public int position = -1;
  public int index = -1;

  public static ReaderBookMetadata fromValues(ReaderConfig config) {
    return fromValues(config, null);
  }

  public static ReaderBookMetadata fromValues(ReaderConfig config, List<List<Integer>> offsets) {
    final ReaderBookMetadata result = new ReaderBookMetadata();
    result.fontFamily = config.getDefaultFontFamily().getName();
    result.fontSize = config.getTextSize();
    result.hMargin = config.getHorizontalMargin();
    result.vMargin = config.getVerticalMargin();
    result.lineSpacing = config.getLineSpacing();
    if (offsets != null) {
      result.offsets = offsets;
    }
    return result;
  }

  private ReaderBookMetadata() {
  }

  public boolean isValid(ReaderConfig config) {
    return this.fontFamily.equals(config.getDefaultFontFamily().getName())
        && this.fontSize == config.getTextSize()
        && this.vMargin == config.getVerticalMargin()
        && this.hMargin == config.getHorizontalMargin()
        && this.lineSpacing == config.getLineSpacing();
  }

}
