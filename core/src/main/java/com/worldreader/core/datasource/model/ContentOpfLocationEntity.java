package com.worldreader.core.datasource.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.*;
import java.util.regex.*;

@Root(strict = false, name = "container") public class ContentOpfLocationEntity {

  private static final Pattern CONTENT_OPF_NAME_PATTERN = Pattern.compile("([-\\w\\._]+\\.opf)$");

  private static final Pattern CONTENT_OPF_PATH_PATTERN =
      Pattern.compile("([-\\w/\\._]+/).*\\.opf$");

  @ElementList(name = "rootfiles") public List<RootFile> rootFiles;

  public static class RootFile {

    @Attribute(name = "full-path") public String fullPath;
    @Attribute(name = "media-type") public String mediaType;
  }

  @Nullable public String getRawContentOpfFullPath() {
    return hasRootFile() ? rootFiles.get(0).fullPath : null;
  }

  @Nullable public String getContentOpfName() {
    final String rawContentPath = getRawContentOpfFullPath();
    if (TextUtils.isEmpty(rawContentPath)) {
      return null;
    } else {
      final Matcher matcher = CONTENT_OPF_NAME_PATTERN.matcher(rawContentPath);
      if (matcher.find()) {
        return matcher.group(1);
      } else {
        return null;
      }
    }
  }

  @Nullable public String getContentOpfPath() {
    final String rawContentPath = getRawContentOpfFullPath();
    if (TextUtils.isEmpty(rawContentPath)) {
      return "";
    } else {
      final Matcher matcher = CONTENT_OPF_PATH_PATTERN.matcher(rawContentPath);
      if (matcher.find()) {
        return matcher.group(1);
      } else {
        return "";
      }
    }
  }

  public boolean hasRootFile() {
    return rootFiles != null && rootFiles.size() > 0;
  }
}
