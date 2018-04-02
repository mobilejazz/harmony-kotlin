package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub;

import com.worldreader.reader.wr.configuration.ReaderConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * This class allows page-offsets to be read from and stored as JSON.
 * <p>
 * Page-offsets are only valid under the circumstances they were
 * calculated with: if text-size, page-margins, etc. change, they
 * must be re-calculated. This class allows checks for this.
 *
 */
public class PageOffsets {

  private static int ALGORITHM_VERSION = 3;

  private int fontSize;
  private String fontFamily;

  private int vMargin;
  private int hMargin;

  private int lineSpacing;

  private boolean fullScreen;

  private boolean allowStyling;

  private int algorithmVersion;

  private List<List<Integer>> offsets;

  private enum Fields {
    fontSize, fontFamily, vMargin, hMargin, lineSpacing, fullScreen, offsets, allowStyling, algorithmVersion
  }

  private PageOffsets() {
  }

  public boolean isValid(ReaderConfig config) {
    return this.fontFamily.equals(config.getDefaultFontFamily().getName())
        && this.fontSize == config.getTextSize()
        && this.vMargin == config.getVerticalMargin()
        && this.hMargin == config.getHorizontalMargin()
        && this.lineSpacing == config.getLineSpacing()
        && this.algorithmVersion == ALGORITHM_VERSION;
  }

  public List<List<Integer>> getOffsets() {
    return offsets;
  }

  public static PageOffsets fromValues(ReaderConfig config, List<List<Integer>> offsets) {
    final PageOffsets result = new PageOffsets();
    result.fontFamily = config.getDefaultFontFamily().getName();
    result.fontSize = config.getTextSize();
    result.hMargin = config.getHorizontalMargin();
    result.vMargin = config.getVerticalMargin();
    result.lineSpacing = config.getLineSpacing();
    result.allowStyling = false;
    result.algorithmVersion = ALGORITHM_VERSION;
    result.offsets = offsets;
    return result;
  }

  public static PageOffsets fromJSON(String json) throws JSONException {
    final JSONObject offsetsObject = new JSONObject(json);
    PageOffsets result = new PageOffsets();
    result.fontFamily = offsetsObject.getString(Fields.fontFamily.name());
    result.fontSize = offsetsObject.getInt(Fields.fontSize.name());
    result.vMargin = offsetsObject.getInt(Fields.vMargin.name());
    result.hMargin = offsetsObject.getInt(Fields.hMargin.name());
    result.lineSpacing = offsetsObject.getInt(Fields.lineSpacing.name());
    result.fullScreen = offsetsObject.getBoolean(Fields.fullScreen.name());
    result.algorithmVersion = offsetsObject.optInt(Fields.algorithmVersion.name(), -1);
    result.allowStyling = offsetsObject.optBoolean(Fields.allowStyling.name(), true);
    result.offsets = readOffsets(offsetsObject.getJSONArray(Fields.offsets.name()));
    return result;
  }

  public String toJSON() throws JSONException {
    final JSONObject jsonObject = new JSONObject();
    jsonObject.put(Fields.fontFamily.name(), fontFamily);
    jsonObject.put(Fields.fontSize.name(), fontSize);
    jsonObject.put(Fields.vMargin.name(), vMargin);
    jsonObject.put(Fields.hMargin.name(), hMargin);
    jsonObject.put(Fields.lineSpacing.name(), lineSpacing);
    jsonObject.put(Fields.fullScreen.name(), fullScreen);
    jsonObject.put(Fields.allowStyling.name(), allowStyling);
    jsonObject.put(Fields.algorithmVersion.name(), algorithmVersion);
    jsonObject.put(Fields.offsets.name(), new JSONArray(offsets));
    return jsonObject.toString();
  }

  private static List<List<Integer>> readOffsets(JSONArray jsonArray) throws JSONException {
    final List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      final List<Integer> sublist = new ArrayList<>();
      final JSONArray subArray = new JSONArray(jsonArray.getString(i));
      for (int j = 0; j < subArray.length(); j++) {
        int val = subArray.getInt(j);
        sublist.add(val);
      }
      result.add(sublist);
    }

    return result;
  }
}
