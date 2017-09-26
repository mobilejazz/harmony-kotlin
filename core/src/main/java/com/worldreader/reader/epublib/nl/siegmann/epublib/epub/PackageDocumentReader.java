package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import android.text.TextUtils;
import android.util.Log;
import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Guide;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.GuideReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.MediaType;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resources;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Spine;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.SpineReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.StreamingResource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.service.MediatypeService;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.ResourceUtil;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentMetadataReader.NAMESPACE_OPF;
import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentMetadataReader.OPFAttributes;
import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentMetadataReader.OPFTags;
import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentMetadataReader.OPFValues;
import static com.worldreader.reader.epublib.nl.siegmann.epublib.epub.PackageDocumentMetadataReader.readMetadata;

/**
 * Reads the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *
 * @author paul
 */
public class PackageDocumentReader {

  private static final String[] POSSIBLE_NCX_ITEM_IDS = new String[] { "toc", "ncx" };

  public static void read(Resource packageResource, Book book, Resources resources)
      throws SAXException, IOException, ParserConfigurationException {
    Document packageDocument = ResourceUtil.getAsDocument(packageResource);
    //String packageHref = packageResource.getHref();
    //resources = fixHrefs(packageHref, resources);
    readGuide(packageDocument, book, resources);

    // Books sometimes use non-identifier ids. We map these here to legal ones
    Map<String, String> idMapping = new HashMap<>();

    resources = readManifest(packageDocument, resources, idMapping);
    book.setResources(resources);
    readCover(packageDocument, book);
    book.setMetadata(readMetadata(packageDocument));
    book.setSpine(readSpine(packageDocument, book.getResources(), idMapping));

    // if we did not find a cover page then we make the first page of the book the cover page
    if (book.getCoverPage() == null && book.getSpine().size() > 0) {
      book.setCoverPage(book.getSpine().getResource(0));
    }
  }

  //	private static Resource readCoverImage(Element metadataElement, Resources resources) {
  //		String coverResourceId = DOMUtil.getFindAttributeValue(metadataElement.getOwnerDocument(), NAMESPACE_OPF, OPFTags.meta, OPFAttributes.name, OPFValues.meta_cover, OPFAttributes.content);
  //		if (StringUtil.isBlank(coverResourceId)) {
  //			return null;
  //		}
  //		Resource coverResource = resources.getByIdOrHref(coverResourceId);
  //		return coverResource;
  //	}

  public static void processStreamingManifest(Resources resources, Document packageDocument) {
    final Element manifestElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.manifest);
    if (manifestElement == null) {
      Log.e("epublib", "Package document does not contain element " + OPFTags.manifest);
      return;
    }

    final NodeList itemElements = manifestElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.item);

    for (int i = 0; i < itemElements.getLength(); i++) {
      final Element itemElement = (Element) itemElements.item(i);
      final String id = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.id);

      String href = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.href);
      try {
        href = URLDecoder.decode(href, Constants.CHARACTER_ENCODING);
      } catch (UnsupportedEncodingException e) {
        Log.e("epublib", "Problem while decoding url: " + e.getMessage());
      }

      final String mediaTypeName = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.media_type);
      final MediaType mediaType = MediatypeService.getMediaTypeByName(mediaTypeName);

      // TODO: Check if is necessary to create special cases for different media types
      final Resource resource = new StreamingResource(id, href, mediaType);

      if (mediaType == MediatypeService.XHTML) {
        resource.setInputEncoding(Constants.CHARACTER_ENCODING);
      }

      if (MediatypeService.isBitmapImage(mediaType)) {
        final String width = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.width);
        final String height = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.height);
        if (!TextUtils.isEmpty(width) && !TextUtils.isEmpty(height)) {
          resource.setWidth(width);
          resource.setHeight(height);
        }
      }

      resources.add(resource);
    }
  }

  /**
   * Reads the manifest containing the resource ids, hrefs and mediatypes.
   *
   * @return a Map with resources, with their id's as key.
   */
  private static Resources readManifest(Document packageDocument, Resources resources, Map<String, String> idMapping) {
    Element manifestElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.manifest);
    Resources result = new Resources();
    if (manifestElement == null) {
      Log.e("epublib", "Package document does not contain element " + OPFTags.manifest);
      return result;
    }

    NodeList itemElements = manifestElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.item);
    for (int i = 0; i < itemElements.getLength(); i++) {
      final Element itemElement = (Element) itemElements.item(i);
      final String id = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.id);

      String href = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.href);
      try {
        href = URLDecoder.decode(href, Constants.CHARACTER_ENCODING);
      } catch (UnsupportedEncodingException e) {
        Log.e("epublib", e.getMessage());
      }

      String mediaTypeName = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.media_type);
      Resource resource = resources.remove(href);

      if (resource == null) {
        Log.e("epublib", "resource with href '" + href + "' not found");
        continue;

      }
      resource.setId(id);

      final MediaType mediaType = MediatypeService.getMediaTypeByName(mediaTypeName);
      if (mediaType != null) {
        resource.setMediaType(mediaType);
        if (MediatypeService.isBitmapImage(mediaType)) {
          final String width = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.width);
          final String height = DOMUtil.getAttribute(itemElement, NAMESPACE_OPF, OPFAttributes.height);

          resource.setWidth(width);
          resource.setHeight(height);
        }
      }

      result.add(resource);

      idMapping.put(id, resource.getId());
    }
    return result;
  }

  /**
   * Reads the book's guide.
   * Here some more attempts are made at finding the cover page.
   */
  private static void readGuide(Document packageDocument, Book book, Resources resources) {
    Element guideElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.guide);
    if (guideElement == null) {
      return;
    }
    Guide guide = book.getGuide();
    NodeList guideReferences = guideElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.reference);
    for (int i = 0; i < guideReferences.getLength(); i++) {
      Element referenceElement = (Element) guideReferences.item(i);
      String resourceHref = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.href);
      if (StringUtil.isBlank(resourceHref)) {
        continue;
      }
      Resource resource = resources.getByHref(StringUtil.substringBefore(resourceHref, Constants.FRAGMENT_SEPARATOR_CHAR));
      if (resource == null) {
        Log.e("epublib", "Guide is referencing resource with href " + resourceHref + " which could not be found");
        continue;
      }
      String type = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.type);
      if (StringUtil.isBlank(type)) {
        Log.e("epublib", "Guide is referencing resource with href " + resourceHref + " which is missing the 'type' attribute");
        continue;
      }
      String title = DOMUtil.getAttribute(referenceElement, NAMESPACE_OPF, OPFAttributes.title);
      if (GuideReference.COVER.equalsIgnoreCase(type)) {
        continue; // cover is handled elsewhere
      }
      GuideReference reference = new GuideReference(resource, type, title, StringUtil.substringAfter(resourceHref, Constants.FRAGMENT_SEPARATOR_CHAR));
      guide.addReference(reference);
    }
  }

  ///**
  // * Strips off the package prefixes up to the href of the packageHref.
  // *
  // * Example:
  // * If the packageHref is "OEBPS/content.opf" then a resource href like "OEBPS/foo/bar.html" will
  // * be turned into "foo/bar.html"
  // */
  //private static Resources fixHrefs(String packageHref, Resources resourcesByHref) {
  //  int lastSlashPos = packageHref.lastIndexOf('/');
  //  if (lastSlashPos < 0) {
  //    return resourcesByHref;
  //  }
  //  Resources result = new Resources();
  //  for (Resource resource : resourcesByHref.getAll()) {
  //    if (StringUtil.isNotBlank(resource.getHref()) || resource.getHref().length() > lastSlashPos) {
  //      resource.setHref(resource.getHref().substring(lastSlashPos + 1));
  //    }
  //    result.add(resource);
  //  }
  //  return result;
  //}

  /**
   * Reads the document's spine, containing all sections in reading order.
   */
  private static Spine readSpine(Document packageDocument, Resources resources, Map<String, String> idMapping) {
    Element spineElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFTags.spine);
    if (spineElement == null) {
      Log.e("epublib", "Element OPFTags.spine not found in package document, generating one automatically");
      return generateSpineFromResources(resources);
    }

    Spine result = new Spine();
    result.setTocResource(findTableOfContentsResource(spineElement, resources));
    NodeList spineNodes = packageDocument.getElementsByTagNameNS(NAMESPACE_OPF, OPFTags.itemref);
    List<SpineReference> spineReferences = new ArrayList<SpineReference>(spineNodes.getLength());
    for (int i = 0; i < spineNodes.getLength(); i++) {
      Element spineItem = (Element) spineNodes.item(i);
      String itemref = DOMUtil.getAttribute(spineItem, NAMESPACE_OPF, OPFAttributes.idref);
      if (StringUtil.isBlank(itemref)) {
        Log.e("epublib", "itemref with missing or empty idref"); // XXX
        continue;
      }
      String id = idMapping.get(itemref);
      if (id == null) {
        id = itemref;
      }
      Resource resource = resources.getByIdOrHref(id);
      if (resource == null) {
        Log.e("epublib", "resource with id \'" + id + "\' not found");
        continue;
      }

      SpineReference spineReference = new SpineReference(resource);
      if (OPFValues.no.equalsIgnoreCase(DOMUtil.getAttribute(spineItem, NAMESPACE_OPF, OPFAttributes.linear))) {
        spineReference.setLinear(false);
      }
      spineReferences.add(spineReference);
    }
    result.setSpineReferences(spineReferences);
    return result;
  }

  /**
   * Creates a spine out of all resources in the resources.
   * The generated spine consists of all XHTML pages in order of their href.
   */
  private static Spine generateSpineFromResources(Resources resources) {
    Spine result = new Spine();
    List<String> resourceHrefs = new ArrayList<String>();
    resourceHrefs.addAll(resources.getAllHrefs());
    Collections.sort(resourceHrefs, String.CASE_INSENSITIVE_ORDER);
    for (String resourceHref : resourceHrefs) {
      Resource resource = resources.getByHref(resourceHref);
      if (resource.getMediaType() == MediatypeService.NCX) {
        result.setTocResource(resource);
      } else if (resource.getMediaType() == MediatypeService.XHTML) {
        result.addSpineReference(new SpineReference(resource));
      }
    }
    return result;
  }

  /**
   * The spine tag should contain a 'toc' attribute with as value the resource id of the table of
   * contents resource.
   *
   * Here we try several ways of finding this table of contents resource.
   * We try the given attribute value, some often-used ones and finally look through all resources
   * for the first resource with the table of contents mimetype.
   */
  private static Resource findTableOfContentsResource(Element spineElement, Resources resources) {
    String tocResourceId = DOMUtil.getAttribute(spineElement, NAMESPACE_OPF, OPFAttributes.toc);
    Resource tocResource = null;
    if (StringUtil.isNotBlank(tocResourceId)) {
      tocResource = resources.getByIdOrHref(tocResourceId);
    }

    if (tocResource != null) {
      return tocResource;
    }

    for (String POSSIBLE_NCX_ITEM_ID : POSSIBLE_NCX_ITEM_IDS) {
      tocResource = resources.getByIdOrHref(POSSIBLE_NCX_ITEM_ID);
      if (tocResource != null) {
        return tocResource;
      }
      tocResource = resources.getByIdOrHref(POSSIBLE_NCX_ITEM_ID.toUpperCase());
      if (tocResource != null) {
        return tocResource;
      }
    }

    // get the first resource with the NCX mediatype
    tocResource = resources.findFirstResourceByMediaType(MediatypeService.NCX);

    if (tocResource == null) {
      Log.e("epublib",
          "Could not find table of contents resource. Tried resource with id '" + tocResourceId + "', " + "toc" + ", " + "TOC" + " and any NCX resource.");
    }
    return tocResource;
  }

  /**
   * Find all resources that have something to do with the coverpage and the cover image.
   * Search the meta tags and the guide references
   */
  // package
  private static Set<String> findCoverHrefs(Document packageDocument) {
    Set<String> result = new HashSet<>();

    // try and find a meta tag with name = 'cover' and a non-blank id
    String coverResourceId =
        DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF, OPFTags.meta, OPFAttributes.name, OPFValues.meta_cover, OPFAttributes.content);

    if (StringUtil.isNotBlank(coverResourceId)) {
      String coverHref = DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF, OPFTags.item, OPFAttributes.id, coverResourceId, OPFAttributes.href);
      if (StringUtil.isNotBlank(coverHref)) {
        result.add(coverHref);
      } else {
        result.add(coverResourceId); // maybe there was a cover href put in the cover id attribute
      }
    }
    // try and find a reference tag with type is 'cover' and reference is not blank
    String coverHref =
        DOMUtil.getFindAttributeValue(packageDocument, NAMESPACE_OPF, OPFTags.reference, OPFAttributes.type, OPFValues.reference_cover, OPFAttributes.href);
    if (StringUtil.isNotBlank(coverHref)) {
      result.add(coverHref);
    }
    return result;
  }

  /**
   * Finds the cover resource in the packageDocument and adds it to the book if found.
   * Keeps the cover resource in the resources map
   */
  private static void readCover(Document packageDocument, Book book) {
    final Collection<String> coverHrefs = findCoverHrefs(packageDocument);
    for (String coverHref : coverHrefs) {
      Resource resource = book.getResources().getByHref(coverHref);
      if (resource == null) {
        Log.e("epublib", "Cover resource " + coverHref + " not found");
        continue;
      }
      if (resource.getMediaType() == MediatypeService.XHTML) {
        book.setCoverPage(resource);
      } else if (MediatypeService.isBitmapImage(resource.getMediaType())) {
        book.setCoverImage(resource);
      }
    }
  }
}