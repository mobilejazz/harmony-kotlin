package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;

/**
 * Various low-level support methods for reading/writing epubs.
 *
 * @author paul.siegmann
 */
public class EpubProcessorSupport {

  private static DocumentBuilderFactory documentBuilderFactory;

  static {
    EpubProcessorSupport.documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    documentBuilderFactory.setValidating(false);
  }

  static class EntityResolverImpl implements EntityResolver {

    private String previousLocation;

    @Override public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
      final String resourcePath;

      if (systemId.startsWith("http:")) {
        URL url = new URL(systemId);
        resourcePath = "dtd/" + url.getHost() + url.getPath();
        previousLocation = resourcePath.substring(0, resourcePath.lastIndexOf('/'));
      } else {
        resourcePath = previousLocation + systemId.substring(systemId.lastIndexOf('/'));
      }

      if (this.getClass().getClassLoader().getResource(resourcePath) == null) {
        throw new RuntimeException("remote resource is not cached : [" + systemId + "] cannot continue");
      }

      final InputStream in = EpubProcessorSupport.class.getClassLoader().getResourceAsStream(resourcePath);

      return new InputSource(in);
    }
  }

  /**
   * Gets an EntityResolver that loads dtd's and such from the epublib classpath.
   * In order to enable the loading of relative urls the given EntityResolver contains the
   * previousLocation.
   * Because of a new EntityResolver is created every time this method is called.
   * Fortunately the EntityResolver created uses up very little memory per instance.
   *
   * @return an EntityResolver that loads dtd's and such from the epublib classpath.
   */
  private static EntityResolver getEntityResolver() {
    return new EntityResolverImpl();
  }

  /**
   * Creates a DocumentBuilder that looks up dtd's and schema's from epublib's classpath.
   */
  public static DocumentBuilder createDocumentBuilder() {
    try {
      final DocumentBuilder result = documentBuilderFactory.newDocumentBuilder();
      result.setEntityResolver(getEntityResolver());
      return result;
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      return null;
    }
  }
}
