package com.worldreader.reader.epublib.nl.siegmann.epublib.util;

import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resources;
import com.worldreader.reader.epublib.nl.siegmann.epublib.epub.EpubProcessorSupport;
import com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Various resource utility methods
 *
 * @author paul
 */
public class ResourceUtil {

  public static Resource createFakePackageResource(InputStream inputStream) throws IOException {
    return new Resource(inputStream, "OEBPS/content.opf");
  }

  /**
   * Gets the contents of the Resource as an InputSource in a null-safe manner.
   */
  private static InputSource getInputSource(Resource resource) throws IOException {
    if (resource == null) {
      return null;
    }

    final Reader reader = resource.getReader();
    if (reader == null) {
      return null;
    }

    return new InputSource(reader);
  }

  /**
   * Reads parses the xml therein and returns the result as a Document
   */
  public static Document getAsDocument(Resource resource) throws SAXException, IOException, ParserConfigurationException {
    return getAsDocument(resource, EpubProcessorSupport.createDocumentBuilder());
  }

  /**
   * Reads the given resources inputstream, parses the xml therein and returns the result as a
   * Document
   *
   * @throws UnsupportedEncodingException
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public static Document getAsDocument(Resource resource, DocumentBuilder documentBuilder)
      throws SAXException, IOException, ParserConfigurationException {
    final InputSource inputSource = getInputSource(resource);
    if (inputSource == null) {
      return null;
    }
    return documentBuilder.parse(inputSource);
  }

  public static void generateStreamingResourcesFromPackageResource(Resources resources, Resource packageResource) throws SAXException, IOException, ParserConfigurationException {
    final Document packageDocument = getAsDocument(packageResource, EpubProcessorSupport.createDocumentBuilder());
    PackageDocumentReader.processStreamingManifest(resources, packageDocument);
  }
}
