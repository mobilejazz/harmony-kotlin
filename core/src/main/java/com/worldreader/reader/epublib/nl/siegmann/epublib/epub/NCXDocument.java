package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import android.util.Log;
import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TOCReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TableOfContents;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.ResourceUtil;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import com.worldreader.reader.epublib.org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * Writes the ncx document as defined by namespace http://www.daisy.org/z3986/2005/ncx/
 *
 * @author paul
 */
public class NCXDocument {

  public static final String NAMESPACE_NCX = "http://www.daisy.org/z3986/2005/ncx/";

  private interface NCXTags {

    String ncx = "ncx";
    String meta = "meta";
    String navPoint = "navPoint";
    String navMap = "navMap";
    String navLabel = "navLabel";
    String content = "content";
    String text = "text";
    String docTitle = "docTitle";
    String docAuthor = "docAuthor";
    String head = "head";
  }

  private interface NCXAttributes {

    String src = "src";
    String name = "name";
    String content = "content";
    String id = "id";
    String playOrder = "playOrder";
    String clazz = "class";
    String version = "version";
  }

  public static Resource read(Book book) {
    if (book.getSpine().getTocResource() == null) {
      Log.e("epublib", "Book does not contain a table of contents file");
      return null;
    }

    Resource ncxResource = null;
    try {
      ncxResource = book.getSpine().getTocResource();
      Document ncxDocument = ResourceUtil.getAsDocument(ncxResource);
      Element navMapElement = DOMUtil.getFirstElementByTagNameNS(ncxDocument.getDocumentElement(), NAMESPACE_NCX, NCXTags.navMap);
      TableOfContents tableOfContents = new TableOfContents(readTOCReferences(navMapElement.getChildNodes(), book));
      book.setTableOfContents(tableOfContents);
    } catch (Exception e) {
      Log.e("epublib", e.getMessage(), e);
    }
    return ncxResource;
  }

  private static List<TOCReference> readTOCReferences(NodeList navpoints, Book book) {
    if (navpoints == null) {
      return new ArrayList<>();
    }

    List<TOCReference> result = new ArrayList<>(navpoints.getLength());
    for (int i = 0; i < navpoints.getLength(); i++) {
      Node node = navpoints.item(i);
      if (node.getNodeType() != Document.ELEMENT_NODE) {
        continue;
      }
      if (!(node.getLocalName().equals(NCXTags.navPoint))) {
        continue;
      }
      TOCReference tocReference = readTOCReference((Element) node, book);
      result.add(tocReference);
    }

    return result;
  }

  private static TOCReference readTOCReference(Element navpointElement, Book book) {
    String label = readNavLabel(navpointElement);
    String reference = FilenameUtils.getPath(book.getSpine().getTocResource().getHref()) + readNavReference(navpointElement);
    String href = StringUtil.substringBefore(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
    String fragmentId = StringUtil.substringAfter(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
    Resource resource = book.getResources().getByHref(href);
    if (resource == null) {
      Log.e("epublib", "Resource with href " + href + " in NCX document not found");
    }
    TOCReference result = new TOCReference(label, resource, fragmentId);
    readTOCReferences(navpointElement.getChildNodes(), book);
    result.setChildren(readTOCReferences(navpointElement.getChildNodes(), book));
    return result;
  }

  private static String readNavReference(Element navpointElement) {
    Element contentElement = DOMUtil.getFirstElementByTagNameNS(navpointElement, NAMESPACE_NCX, NCXTags.content);
    String result = DOMUtil.getAttribute(contentElement, NAMESPACE_NCX, NCXAttributes.src);
    try {
      result = URLDecoder.decode(result, Constants.CHARACTER_ENCODING);
    } catch (UnsupportedEncodingException e) {
      Log.e("epublib", e.getMessage());
    }
    return result;
  }

  private static String readNavLabel(Element navpointElement) {
    Element navLabel = DOMUtil.getFirstElementByTagNameNS(navpointElement, NAMESPACE_NCX, NCXTags.navLabel);
    return DOMUtil.getTextChildrenContent(DOMUtil.getFirstElementByTagNameNS(navLabel, NAMESPACE_NCX, NCXTags.text));
  }
}
