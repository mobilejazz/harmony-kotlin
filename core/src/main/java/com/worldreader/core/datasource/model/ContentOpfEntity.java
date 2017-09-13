package com.worldreader.core.datasource.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.Serializable;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.*;
import java.util.*;

@Root(strict = false, name = "package") public class ContentOpfEntity {

  public static final String TOC_ID = "ncx";
  public static final String DEFAULT_TOC_NAME = "toc.ncx";

  @ElementList(name = "manifest") public List<Item> manifest;

  public static class Item implements Serializable {

    @Attribute(name = "href") public String href;
    @Attribute(name = "id") public String id;
    @Attribute(name = "width", required = false) public String width;
    @Attribute(name = "height", required = false) public String height;
  }

  public List<String> getManifestEntries() {
    final List<String> entries = new ArrayList<>();

    if (manifest != null && manifest.size() > 0) {
      for (Item item : manifest) {
        entries.add(item.href);
      }
    }

    return entries;
  }

  public Map<String, Item> getImagesResourcesEntries() {
    final HashMap<String, Item> entries = new HashMap<>();

    if (manifest != null && manifest.size() > 0) {
      for (final Item item : manifest) {
        if (!TextUtils.isEmpty(item.height) && !TextUtils.isEmpty(item.width)) {
          entries.put(item.href, item);
        }
      }
    }

    return entries;
  }

  @Nullable public String getTocEntry() {
    if (manifest != null && manifest.size() > 0) {
      for (Item item : manifest) {
        if (!TextUtils.isEmpty(item.id) && TOC_ID.equals(item.id)) {
          return item.href;
        }
      }
    }

    return DEFAULT_TOC_NAME;
  }

}
