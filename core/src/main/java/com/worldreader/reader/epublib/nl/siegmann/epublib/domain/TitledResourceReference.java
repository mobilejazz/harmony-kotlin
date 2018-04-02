package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;

import java.io.*;

public class TitledResourceReference extends ResourceReference implements Serializable {

  private String fragmentId;
  private String title;

  TitledResourceReference(Resource resource, String title, String fragmentId) {
    super(resource);
    this.title = title;
    this.fragmentId = fragmentId;
  }

  public String getFragmentId() {
    return fragmentId;
  }

  public void setFragmentId(String fragmentId) {
    this.fragmentId = fragmentId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * If the fragmentId is blank it returns the resource href, otherwise it returns the resource href '#' + the fragmentId.
   */
  public String getCompleteHref() {
    if (StringUtil.isBlank(fragmentId)) {
      return resource.getHref();
    } else {
      return resource.getHref() + Constants.FRAGMENT_SEPARATOR_CHAR + fragmentId;
    }
  }

  public void setResource(Resource resource, String fragmentId) {
    super.setResource(resource);
    this.fragmentId = fragmentId;
  }

  /**
   * Sets the resource to the given resource and sets the fragmentId to null.
   */
  public void setResource(Resource resource) {
    setResource(resource, null);
  }
}
