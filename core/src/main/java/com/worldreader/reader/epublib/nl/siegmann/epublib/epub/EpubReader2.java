package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.datasource.model.NCXEntity;
import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Author;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.MediaType;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Metadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resources;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Spine;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.SpineReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.StreamingResource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TOCReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TableOfContents;
import com.worldreader.reader.epublib.nl.siegmann.epublib.service.MediatypeService;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.ResourceUtil;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import com.worldreader.reader.epublib.org.apache.commons.io.FilenameUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

// TODO: 25/09/2017 Check that get book cover works properly
public class EpubReader2 {

  private static Serializer XML_PARSER = new Persister();

  public static Book readStreamingEpub(final InputStream contentOpfIs, final InputStream tocResourceIs) throws Exception {
    final ContentOpfEntity contentOpf = XML_PARSER.read(ContentOpfEntity.class, contentOpfIs, false);
    final NCXEntity NCXEntity = XML_PARSER.read(NCXEntity.class, tocResourceIs, false);

    final Book book = new Book();

    book.setOpfResource(toOpfResource(contentOpfIs));
    book.setResources(toBookResources(contentOpf));
    book.setMetadata(toBookMetadata(contentOpf));
    book.setSpine(toSpine(contentOpf, book.getResources()));
    book.setTableOfContents(toTableOfContents(contentOpf, NCXEntity, book));
    book.setNcxResource(toNcxResource(contentOpf, book, tocResourceIs));

    return book;
  }

  private static Resource toOpfResource(InputStream contentOpfIs) throws IOException {
    contentOpfIs.reset();
    return ResourceUtil.createFakePackageResource(contentOpfIs);
  }

  private static Resources toBookResources(ContentOpfEntity contentOpf) {
    final List<ContentOpfEntity.Item> entries = contentOpf.getManifest();
    final Resources resources = new Resources();

    for (ContentOpfEntity.Item entry : entries) {
      final MediaType mediaType = MediatypeService.getMediaTypeByName(entry.mediaType);
      final StreamingResource resource = new StreamingResource(entry.id, entry.href, mediaType);

      // Set proper encoding if source is HTML
      if (mediaType == MediatypeService.XHTML) {
        resource.setInputEncoding(Constants.CHARACTER_ENCODING);
      }

      // If the source supports width and height, let's add it
      if (MediatypeService.isBitmapImage(mediaType)) {
        if (!TextUtils.isEmpty(entry.width) && !TextUtils.isEmpty(entry.height)) {
          resource.setWidth(entry.width);
          resource.setHeight(entry.height);
        }
      }

      resources.add(resource);
    }

    return resources;
  }

  private static Metadata toBookMetadata(final ContentOpfEntity contentOpf) {
    final ContentOpfEntity.Metadata metadata = contentOpf.getMetadata();

    final Metadata toReturn = new Metadata();
    toReturn.setTitles(Collections.singletonList(metadata.getTitle()));
    toReturn.setAuthors(Collections.singletonList(new Author(metadata.getCreator())));
    toReturn.setDescriptions(Collections.singletonList(metadata.getDescription()));
    toReturn.setPublishers(Collections.singletonList(metadata.getPublisher()));

    return toReturn;
  }

  private static Spine toSpine(final ContentOpfEntity contentOpf, final Resources resources) {
    final List<SpineReference> spineReferences = new ArrayList<>();
    final List<ContentOpfEntity.itemref> itemrefs = contentOpf.getSpineItemRefs();

    for (ContentOpfEntity.itemref itemref : itemrefs) {
      final String idRef = itemref.idRef;
      final Resource resource = resources.getByIdOrHref(idRef);
      if (resource != null) {
        final SpineReference reference = new SpineReference(resource);
        spineReferences.add(reference);
      }
    }

    return new Spine(spineReferences);
  }

  @Nullable private static Resource toNcxResource(final ContentOpfEntity contentOpfEntity, final Book book, final InputStream ncx) throws IOException {
    final Spine spine = book.getSpine();

    if (spine == null) {
      return null; // Epub doesn't contain what we are looking for
    }

    ncx.reset();

    final String tocId = contentOpfEntity.spine.toc;
    final Resource tocResource = book.getResources().getById(tocId);
    tocResource.setData(ncx);

    spine.setTocResource(tocResource);

    return tocResource;
  }

  private static TableOfContents toTableOfContents(final ContentOpfEntity contentOpfEntity, final NCXEntity ncxEntity, final Book book) throws Exception {
    final List<TOCReference> references = getTocReferences(contentOpfEntity, ncxEntity.navPoints, book);
    return new TableOfContents(references);
  }

  @NonNull
  private static List<TOCReference> getTocReferences(final ContentOpfEntity contentOpfEntity, final List<NCXEntity.NavPoint> navPoints, final Book book)
      throws Exception {
    final List<TOCReference> references = new ArrayList<>();

    for (NCXEntity.NavPoint navPoint : navPoints) {
      final TOCReference tocReference = toTOCReference(contentOpfEntity, navPoint, book);
      if (tocReference != null) {
        references.add(tocReference);
      }
    }

    return references;
  }

  @Nullable private static TOCReference toTOCReference(final ContentOpfEntity contentOpfEntity, final NCXEntity.NavPoint navPoint, final Book book)
      throws Exception {
    // Retrieve values from XML
    final String label = navPoint.navLabel.text;
    final String rawSrc = navPoint.content.src;

    // Transform to url
    final String src = URLDecoder.decode(rawSrc, Constants.CHARACTER_ENCODING);

    // Create reference
    final String tocResourceId = contentOpfEntity.spine.toc;
    final Resource tocResource = book.getResources().getById(tocResourceId);
    final String tocResourceHref = tocResource.getHref();

    final String reference = FilenameUtils.getPath(tocResourceHref) + src;

    // Obtain just the fragmentId
    final String fragmentId = StringUtil.substringAfter(reference, Constants.FRAGMENT_SEPARATOR_CHAR);

    // Retrieve from resources
    final String href = StringUtil.substringBefore(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
    final Resource resource = book.getResources().getByHref(href);

    final TOCReference result;
    if (resource != null) {
      result = new TOCReference(label, resource, fragmentId);
      final List<NCXEntity.NavPoint> newNavPoint = navPoint.navPoints;
      if (newNavPoint != null) {
        // Call recursively until last element inside has been consumed
        result.setChildren(getTocReferences(contentOpfEntity, newNavPoint, book));
      }
    } else {
      result = null;
    }

    return result;
  }

}
