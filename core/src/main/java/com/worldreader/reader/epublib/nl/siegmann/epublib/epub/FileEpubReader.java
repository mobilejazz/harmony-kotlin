package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.datasource.model.ContentOpfLocationEntity;
import com.worldreader.core.datasource.model.NCXEntity;
import com.worldreader.reader.epublib.net.sf.jazzlib.ZipEntry;
import com.worldreader.reader.epublib.net.sf.jazzlib.ZipFile;
import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Author;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Guide;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.MediaType;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Metadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resources;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Spine;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.SpineReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TOCReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TableOfContents;
import com.worldreader.reader.epublib.nl.siegmann.epublib.service.MediatypeService;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import com.worldreader.reader.epublib.org.apache.commons.io.FilenameUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.TreeStrategy;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class FileEpubReader {

  private static Strategy XML_STRATEGY = new TreeStrategy("clazz", "l"); // Ignore class attribute in SimpleXML (https://stackoverflow.com/a/16563238)
  private static Serializer XML_PARSER = new Persister(XML_STRATEGY);

  public static Book readFileEpub(final File file) throws Exception {
    final ZipFile zip = new ZipFile(file);

    Resources resources = toEpubRawResources(file, zip);
    final String packageResourceHref = findEpubPackageResourceHref(resources);

    final Resource contentOpfResource = findContentOpfResource(packageResourceHref, resources);
    final ContentOpfEntity contentOpf = XML_PARSER.read(ContentOpfEntity.class, contentOpfResource.getInputStream(), false);

    resources = fixHrefs(packageResourceHref, resources);
    resources = fixStreamingResourcesIds(contentOpf, resources);

    final Metadata metadata = toBookMetadata(contentOpf);
    final Spine spine = toSpine(contentOpf, resources);
    final TableOfContents tableOfContents = toTableOfContents(contentOpf, spine, resources);
    final Resource ncxResource = toNcxResource(contentOpf, spine, resources);

    return Book.builder()
        .withOpfResource(contentOpfResource)
        .withResources(resources)
        .withMetadata(metadata)
        .withSpine(spine)
        .withTableOfContents(tableOfContents)
        .withNcxResource(ncxResource)
        .withGuide(new Guide())
        .build();
  }

  @NonNull private static Resources toEpubRawResources(File file, ZipFile zip) throws IOException {
    final Resources resources = new Resources();

    final Enumeration<? extends ZipEntry> entries = zip.entries();
    while (entries.hasMoreElements()) {
      final ZipEntry zipEntry = entries.nextElement();

      if (zipEntry.isDirectory()) {
        continue;
      }

      final String filename = zipEntry.getName();
      final Resource resource;

      if (Constants.OEBPS_CONTENT_OPF.toLowerCase().contains(filename.toLowerCase()) || Constants.META_INF_CONTAINER.toLowerCase().contains(filename.toLowerCase())) {
        resource = new Resource(zip.getInputStream(zipEntry), file.getPath(), (int) zipEntry.getSize(), filename);
      } else {
        resource = new Resource(file.getPath(), (int) zipEntry.getSize(), filename);
      }

      final MediaType mediaType = MediatypeService.determineMediaType(filename);
      if (mediaType == MediatypeService.XHTML) {
        resource.setInputEncoding(Constants.CHARACTER_ENCODING);
      }

      // TODO: 03/10/2017 Fix this to support extra characteristics
      // If the source supports width and height, let's add it
      //if (MediatypeService.isBitmapImage(mediaType)) {
      //  if (!TextUtils.isEmpty(resource.width) && !TextUtils.isEmpty(entry.height)) {
      //    resource.setWidth(entry.width);
      //    resource.setHeight(entry.height);
      //  }
      //}

      resources.add(resource);
    }

    resources.remove("mimetype");

    return resources;
  }

  private static String findEpubPackageResourceHref(final Resources resources) throws Exception {
    final Resource containerResource = resources.remove(Constants.META_INF_CONTAINER);
    if (containerResource == null) {
      return Constants.OEBPS_CONTENT_OPF;
    }

    final ContentOpfLocationEntity contentOpfLocation = XML_PARSER.read(ContentOpfLocationEntity.class, containerResource.getInputStream(), false);

    return TextUtils.isEmpty(contentOpfLocation.getRawContentOpfFullPath()) ? Constants.OEBPS_CONTENT_OPF : contentOpfLocation.getRawContentOpfFullPath();
  }

  private static Resource findContentOpfResource(String resourceHref, Resources resources) throws IOException {
    final Resource opfResource = resources.getByIdOrHref(resourceHref);
    if (opfResource != null) {
      return opfResource;
    } else {
      throw new IOException("Package resource not found");
    }
  }

  private static Resources fixHrefs(String packageHref, Resources resources) {
    final int lastSlashPos = packageHref.lastIndexOf('/');

    if (lastSlashPos < 0) {
      return resources;
    }

    final Resources result = new Resources();
    for (Resource resource : resources.getAll()) {
      if (!TextUtils.isEmpty(resource.getHref()) || resource.getHref().length() > lastSlashPos) {
        resource.setHref(resource.getHref().substring(lastSlashPos + 1));
      }
      result.add(resource);
    }

    return result;
  }

  private static Resources fixStreamingResourcesIds(final ContentOpfEntity contentOpf, final Resources resources) {
    final List<ContentOpfEntity.Item> manifestEntries = contentOpf.getManifest();

    for (ContentOpfEntity.Item entry : manifestEntries) {
      final Resource resource = resources.getByHref(entry.href);
      resource.setId(entry.id);
    }

    return resources;
  }

  private static Metadata toBookMetadata(ContentOpfEntity opf) {
    final ContentOpfEntity.Metadata metadata = opf.getMetadata();

    final Metadata toReturn = new Metadata();
    toReturn.setTitles(Collections.singletonList(metadata.getTitle()));
    toReturn.setAuthors(Collections.singletonList(new Author(metadata.getCreator())));
    toReturn.setDescriptions(Collections.singletonList(metadata.getDescription()));
    toReturn.setPublishers(Collections.singletonList(metadata.getPublisher()));

    return toReturn;
  }

  private static Spine toSpine(ContentOpfEntity contentOpf, Resources resources) {
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

  private static TableOfContents toTableOfContents(ContentOpfEntity contentOpf, final Spine spine, final Resources resources) throws Exception {
    final String tocId = contentOpf.spine.toc;
    final Resource tocResource = resources.getById(tocId);

    if (tocResource == null) {
      throw new IOException("TOC resource not found!");
    }

    // Load toc resource
    tocResource.initialize();

    // Transform to NCXEntity
    final NCXEntity ncxEntity = XML_PARSER.read(NCXEntity.class, tocResource.getInputStream(), false);

    return toTableOfContents(contentOpf, ncxEntity, resources);
  }

  private static TableOfContents toTableOfContents(final ContentOpfEntity contentOpfEntity, final NCXEntity ncxEntity, final Resources resources)
      throws Exception {
    final List<TOCReference> references = getTocReferences(contentOpfEntity, ncxEntity.navPoints, resources);
    return new TableOfContents(references);
  }

  @NonNull private static List<TOCReference> getTocReferences(final ContentOpfEntity contentOpfEntity, final List<NCXEntity.NavPoint> navPoints,
      final Resources resources) throws Exception {
    if (navPoints == null) {
      return new ArrayList<>();
    }

    final List<TOCReference> references = new ArrayList<>(navPoints.size());

    // Order this list from min to max by playOrder
    Collections.sort(navPoints, Ordering.natural().onResultOf(new Function<NCXEntity.NavPoint, Comparable>() {
      @Nullable @Override public Comparable apply(@Nullable NCXEntity.NavPoint input) {
        return input.playOrder;
      }
    }));

    // Navigate through ordered navPoints
    for (NCXEntity.NavPoint navPoint : navPoints) {
      final TOCReference tocReference = toTOCReference(contentOpfEntity, navPoint, resources);
      if (tocReference != null) {
        references.add(tocReference);
      }
    }

    return references;
  }

  @Nullable private static TOCReference toTOCReference(final ContentOpfEntity contentOpfEntity, final NCXEntity.NavPoint navPoint, final Resources resources)
      throws Exception {
    // Retrieve values from XML
    final String label = navPoint.navLabel.text;
    final String rawSrc = navPoint.content.src;

    // Transform to url
    final String src = URLDecoder.decode(rawSrc, Constants.CHARACTER_ENCODING);

    // Create reference
    final String tocResourceId = contentOpfEntity.spine.toc;
    final Resource tocResource = resources.getById(tocResourceId);
    final String tocResourceHref = tocResource.getHref();

    final String reference = FilenameUtils.getPath(tocResourceHref) + src;

    // Obtain just the fragmentId
    final String fragmentId = StringUtil.substringAfter(reference, Constants.FRAGMENT_SEPARATOR_CHAR);

    // Retrieve from resources
    final String href = StringUtil.substringBefore(reference, Constants.FRAGMENT_SEPARATOR_CHAR);
    final Resource resource = resources.getByHref(href);

    final TOCReference result;
    if (resource != null) {
      result = new TOCReference(label, resource, fragmentId);
      final List<NCXEntity.NavPoint> newNavPoints = navPoint.navPoints;

      if (newNavPoints != null) {
        // Order this list from min to max by playOrder
        Collections.sort(newNavPoints, Ordering.natural().onResultOf(new Function<NCXEntity.NavPoint, Comparable>() {
          @Nullable @Override public Comparable apply(@Nullable NCXEntity.NavPoint input) {
            return input.playOrder;
          }
        }));

        // Call recursively until last element inside has been consumed
        result.setChildren(getTocReferences(contentOpfEntity, newNavPoints, resources));
      }
    } else {
      result = null;
    }

    return result;
  }

  @Nullable private static Resource toNcxResource(final ContentOpfEntity contentOpfEntity, final Spine spine, final Resources resources) throws IOException {
    if (spine == null) {
      return null; // Epub doesn't contain what we are looking for
    }

    // Content already loaded previously
    final String tocId = contentOpfEntity.spine.toc;
    final Resource tocResource = resources.getById(tocId);

    spine.setTocResource(tocResource);

    return tocResource;
  }

}