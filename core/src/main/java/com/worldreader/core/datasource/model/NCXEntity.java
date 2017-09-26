package com.worldreader.core.datasource.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.*;

import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.NCXDocument.NAMESPACE_NCX;

@Root(strict = false, name = "ncx") @Namespace(reference = NAMESPACE_NCX) public class NCXEntity {

  @ElementList(name = "navMap") public List<NavPoint> navPoints;

  public static class NavPoint {

    @Attribute(name = "id", required = false) public String id;
    @Attribute(name = "playOrder", required = false) public String playOrder;
    @Element(name = "navLabel", required = false) public NavLabel navLabel;
    @Element(name = "content", required = false) public Content content;
    @Element(name = "navPoint", required = false) public List<NavPoint> navPoints;
  }

  public static class NavLabel {

    @Attribute(name = "text", required = false) public String text;
  }

  public static class Content {

    @Attribute(name = "src", required = false) public String src;
  }

}
