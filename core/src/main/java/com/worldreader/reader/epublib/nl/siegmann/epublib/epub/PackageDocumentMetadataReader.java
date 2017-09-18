package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import android.util.Log;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Author;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Date;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Identifier;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Metadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * Reads the package document metadata.
 *
 * In its own separate class because the PackageDocumentReader became a bit large and unwieldy.
 *
 * @author paul
 */
// package
public class PackageDocumentMetadataReader {

  public static final String NAMESPACE_OPF = "http://www.idpf.org/2007/opf";
  public static final String NAMESPACE_DUBLIN_CORE = "http://purl.org/dc/elements/1.1/";
  public static final String dateFormat = "yyyy-MM-dd";

  protected interface DCTags {

    String title = "title";
    String creator = "creator";
    String subject = "subject";
    String description = "description";
    String publisher = "publisher";
    String contributor = "contributor";
    String date = "date";
    String type = "type";
    String format = "format";
    String identifier = "identifier";
    String source = "source";
    String language = "language";
    String relation = "relation";
    String coverage = "coverage";
    String rights = "rights";
  }

  protected interface DCAttributes {

    String scheme = "scheme";
    String id = "id";
  }

  protected interface OPFTags {

    String metadata = "metadata";
    String meta = "meta";
    String manifest = "manifest";
    String packageTag = "package";
    String itemref = "itemref";
    String spine = "spine";
    String reference = "reference";
    String guide = "guide";
    String item = "item";
  }

  protected interface OPFAttributes {

    String uniqueIdentifier = "unique-identifier";
    String idref = "idref";
    String name = "name";
    String content = "content";
    String type = "type";
    String href = "href";
    String linear = "linear";
    String event = "event";
    String role = "role";
    String file_as = "file-as";
    String id = "id";
    String media_type = "media-type";
    String title = "title";
    String toc = "toc";
    String version = "version";
    String scheme = "scheme";
    String property = "property";
    String width = "width";
    String height = "height";
  }

  protected interface OPFValues {

    String meta_cover = "cover";
    String reference_cover = "cover";
    String no = "no";
    String generator = "generator";
  }

  public static Metadata readMetadata(Document packageDocument) {
    Metadata result = new Metadata();
    Element metadataElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.metadata);
    if (metadataElement == null) {
      Log.e("epublib", "Package does not contain element " + OPFTags.metadata);
      return result;
    }
    result.setTitles(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.title));
    result.setPublishers(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.publisher));
    result.setDescriptions(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.description));
    result.setRights(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.rights));
    result.setTypes(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.type));
    result.setSubjects(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.subject));
    result.setIdentifiers(readIdentifiers(metadataElement));
    result.setAuthors(readCreators(metadataElement));
    result.setContributors(readContributors(metadataElement));
    result.setDates(readDates(metadataElement));
    result.setOtherProperties(readOtherProperties(metadataElement));

    Element languageTag = DOMUtil.getFirstElementByTagNameNS(metadataElement, NAMESPACE_DUBLIN_CORE, DCTags.language);
    if (languageTag != null) {
      result.setLanguage(DOMUtil.getTextChildrenContent(languageTag));
    }

    return result;
  }

  /**
   * consumes meta tags that have a property attribute as defined in the standard. For example:
   * &lt;meta property="rendition:layout"&gt;pre-paginated&lt;/meta&gt;
   */
  private static Map<QName, String> readOtherProperties(Element metadataElement) {
    Map<QName, String> result = new HashMap<>();

    NodeList metaTags = metadataElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.meta);
    for (int i = 0; i < metaTags.getLength(); i++) {
      Node metaNode = metaTags.item(i);
      Node property = metaNode.getAttributes().getNamedItem(OPFAttributes.property);
      if (property != null) {
        String name = property.getNodeValue();
        String value = metaNode.getTextContent();
        result.put(new QName(name), value);
      }
    }

    return result;
  }

  private static String getBookIdId(Document document) {
    Element packageElement = DOMUtil.getFirstElementByTagNameNS(document.getDocumentElement(), NAMESPACE_OPF, OPFTags.packageTag);
    if (packageElement == null) {
      return null;
    }
    return packageElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.uniqueIdentifier);
  }

  private static List<Author> readCreators(Element metadataElement) {
    return readAuthors(DCTags.creator, metadataElement);
  }

  private static List<Author> readContributors(Element metadataElement) {
    return readAuthors(DCTags.contributor, metadataElement);
  }

  private static List<Author> readAuthors(String authorTag, Element metadataElement) {
    NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, authorTag);
    List<Author> result = new ArrayList<>(elements.getLength());
    for (int i = 0; i < elements.getLength(); i++) {
      Element authorElement = (Element) elements.item(i);
      Author author = createAuthor(authorElement);
      if (author != null) {
        result.add(author);
      }
    }
    return result;
  }

  private static List<Date> readDates(Element metadataElement) {
    NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.date);
    List<Date> result = new ArrayList<>(elements.getLength());
    for (int i = 0; i < elements.getLength(); i++) {
      Element dateElement = (Element) elements.item(i);
      Date date;
      try {
        date = new Date(DOMUtil.getTextChildrenContent(dateElement), dateElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.event));
        result.add(date);
      } catch (IllegalArgumentException e) {
        Log.e("epublib", e.getMessage());
      }
    }
    return result;
  }

  private static Author createAuthor(Element authorElement) {
    String authorString = DOMUtil.getTextChildrenContent(authorElement);
    if (StringUtil.isBlank(authorString)) {
      return null;
    }
    int spacePos = authorString.lastIndexOf(' ');
    Author result;
    if (spacePos < 0) {
      result = new Author(authorString);
    } else {
      result = new Author(authorString.substring(0, spacePos), authorString.substring(spacePos + 1));
    }
    result.setRole(authorElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.role));
    return result;
  }

  private static List<Identifier> readIdentifiers(Element metadataElement) {
    NodeList identifierElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCTags.identifier);
    if (identifierElements.getLength() == 0) {
      Log.e("epublib", "Package does not contain element " + DCTags.identifier);
      return new ArrayList<>();
    }
    String bookIdId = getBookIdId(metadataElement.getOwnerDocument());
    List<Identifier> result = new ArrayList<Identifier>(identifierElements.getLength());
    for (int i = 0; i < identifierElements.getLength(); i++) {
      Element identifierElement = (Element) identifierElements.item(i);
      String schemeName = identifierElement.getAttributeNS(NAMESPACE_OPF, DCAttributes.scheme);
      String identifierValue = DOMUtil.getTextChildrenContent(identifierElement);
      if (StringUtil.isBlank(identifierValue)) {
        continue;
      }
      Identifier identifier = new Identifier(schemeName, identifierValue);
      if (identifierElement.getAttribute("id").equals(bookIdId)) {
        identifier.setBookId(true);
      }
      result.add(identifier);
    }
    return result;
  }
}
