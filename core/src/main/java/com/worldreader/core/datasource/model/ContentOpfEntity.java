package com.worldreader.core.datasource.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.io.*;
import java.util.*;

import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentMetadataReader.NAMESPACE_DUBLIN_CORE;
import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentMetadataReader.NAMESPACE_OPF;

@Root(strict = false, name = "package") @Namespace(reference = NAMESPACE_OPF) public class ContentOpfEntity {

  public static final String TOC_ID = "ncx";
  public static final String DEFAULT_TOC_NAME = "toc.ncx";

  @Attribute(name = "cover", required = false) public String cover;
  @Attribute(name = "COVER", required = false) public String cover2;

  @Attribute(name = "image", required = false) public String image;

  @Element(name = "metadata") public Metadata metadata;

  @Element(name = "spine") public Spine spine;

  @ElementList(name = "manifest") public List<Item> manifest;

  public static class Item implements Serializable {

    @Attribute(name = "href") public String href;
    @Attribute(name = "id") public String id;
    @Attribute(name = "media-type") public String mediaType;
    @Attribute(name = "width", required = false) public String width;
    @Attribute(name = "height", required = false) public String height;
  }

  @NamespaceList({
      @Namespace(reference = ""), @Namespace(reference = NAMESPACE_DUBLIN_CORE),
  }) public static class Metadata implements Serializable {

    @Element(name = "dc:title", required = false) public String title;
    @Element(name = "title", required = false) public String title2;

    @Element(name = "dc:creator", required = false) public String creator;
    @Element(name = "creator", required = false) public String creator2;

    @Element(name = "dc:publisher", required = false) public String publisher;
    @Element(name = "publisher", required = false) public String publisher2;

    @Element(name = "dc:language", required = false) public String language;
    @Element(name = "language", required = false) public String language2;

    @Element(name = "dc:description", required = false) public String description;
    @Element(name = "description", required = false) public String description2;

    @Element(name = "date", required = false) public String date;

    public String getTitle() {
      return !TextUtils.isEmpty(title) ? title : title2;
    }

    public String getCreator() {
      return !TextUtils.isEmpty(creator) ? creator : creator2;
    }

    public String getPublisher() {
      return !TextUtils.isEmpty(publisher) ? publisher : publisher2;
    }

    public String getLanguage() {
      return !TextUtils.isEmpty(language) ? language : language2;
    }

    public String getDescription() {
      return !TextUtils.isEmpty(description) ? description : description2;
    }

  }

  public static class Meta implements Serializable {

    @Attribute(name = "name") public String name;
    @Attribute(name = "content") public String content;
  }

  public static class Spine implements Serializable {
    @Attribute(name = "toc", required = false) public String toc;
    @ElementList(inline = true) public List<itemref> itemrefs;
  }

  // Do NOT rename to ItemRef or anything similar or @ElementList(inline=true) does NOT work properly!
  public static class itemref implements Serializable {

    @Attribute(name = "idref") public String idRef;
    @Attribute(name = "properties", required = false) String properties;
  }

  public List<Item> getManifest() {
    return manifest;
  }

  public List<String> getManifestEntriesHref() {
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

  public List<itemref> getSpineItemRefs() {
    return new ArrayList<>(spine.itemrefs);
  }

  public Metadata getMetadata() {
    return metadata;
  }
}
