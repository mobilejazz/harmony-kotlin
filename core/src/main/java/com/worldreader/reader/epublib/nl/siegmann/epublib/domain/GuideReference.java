package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;

import java.io.*;

public class GuideReference extends TitledResourceReference implements Serializable {

  private static final long serialVersionUID = -316179702440631834L;

  public static String COVER = "cover";

  private String type;

  public GuideReference(Resource resource, String type, String title, String fragmentId) {
    super(resource, title, fragmentId);
    this.type = StringUtil.isNotBlank(type) ? type.toLowerCase() : null;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
