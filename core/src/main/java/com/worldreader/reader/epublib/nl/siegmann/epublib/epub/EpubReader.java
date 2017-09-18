package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import android.util.Log;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resources;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.ResourceUtil;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;

/**
 * Reads an epub file.
 *
 * @author paul
 */
public class EpubReader {

  public Book readEpubStreaming(InputStream packageResourceInputStream) {
    final Resource packageResource = createFakePackageResource(packageResourceInputStream);

    final Resources resources = new Resources();

    try {
      ResourceUtil.generateStreamingResourcesFromPackageResource(resources, packageResource);
    } catch (Exception e) {
      e.printStackTrace();
    }

    resources.add(packageResource);

    final String packageResourceHref = getPackageResourceHref(resources);

    final Book b = new Book();
    b.setOpfResource(processPackageResource(packageResourceHref, b, resources));
    b.setNcxResource(processNcxResource(b));
    return b;
  }

  public static Resource processNcxResource(Book book) {
    return NCXDocument.read(book);
  }

  private Resource processPackageResource(String packageResourceHref, Book book, Resources resources) {
    Resource packageResource = resources.remove(packageResourceHref);
    try {
      PackageDocumentReader.read(packageResource, this, book, resources);
    } catch (Exception e) {
      Log.e("epublib", e.getMessage(), e);
    }
    return packageResource;
  }

  private String getPackageResourceHref(Resources resources) {
    final String defaultResult = "OEBPS/content.opf";
    String result = defaultResult;

    final Resource containerResource = resources.remove("META-INF/container.xml");
    if (containerResource == null) {
      return result;
    }

    try {
      final Document document = ResourceUtil.getAsDocument(containerResource);
      final Element rootFileElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("rootfiles").item(0)).getElementsByTagName("rootfile").item(0);
      result = rootFileElement.getAttribute("full-path");
    } catch (Exception e) {
      Log.e("epublib", e.getMessage(), e);
    }

    if (StringUtil.isBlank(result)) {
      result = defaultResult;
    }

    return result;
  }

  private Resource createFakePackageResource(InputStream inputStream) throws IllegalArgumentException {
    try {
      return ResourceUtil.createFakePackageResource(inputStream);
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid content opf file!", e);
    }
  }
}
