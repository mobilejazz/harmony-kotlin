package com.worldreader.reader.epublib.nl.siegmann.epublib.epub;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.datasource.model.ContentOpfLocationEntity;
import com.worldreader.reader.epublib.net.sf.jazzlib.ZipEntry;
import com.worldreader.reader.epublib.net.sf.jazzlib.ZipFile;
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
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TableOfContents;
import com.worldreader.reader.epublib.nl.siegmann.epublib.service.MediatypeService;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.util.*;

public class FileEpubReader {

  private static Serializer XML_PARSER = new Persister();

  public static Book readFileEpub(final File file) throws Exception {
    final ZipFile zip = new ZipFile(file);

    Resources resources = toEpubRawResources(file, zip);
    final String packageResourceHref = findEpubPackageResourceHref(resources);

    final Book book = new Book();

    final Resource opfResource = toOpfResource(packageResourceHref, resources);
    final ContentOpfEntity contentOpf = XML_PARSER.read(ContentOpfEntity.class, opfResource.getInputStream(), false);

    resources = fixEpubHrefs(packageResourceHref, resources);
    resources = fixResourcesIds(packageResourceHref, contentOpf, resources);

    book.setOpfResource(opfResource);
    book.setResources(resources);
    book.setMetadata(toBookMetadata(contentOpf));
    book.setSpine(toSpine(contentOpf, resources));
    book.setTableOfContents(toTableOfContents(contentOpf, book));
    //book.setNcxResource(toNcxResource(contentOpf, book, tocResourceIs));

    return book;
  }

  private static Resource toOpfResource(String resourceHref, Resources resources) throws IOException {
    final Resource opfResource = resources.getByIdOrHref(resourceHref);
    if (opfResource != null) {
      return opfResource;
    } else {
      throw new IOException("Package resource not found");
    }
  }

  private static String findEpubPackageResourceHref(final Resources resources) throws Exception {
    final Resource containerResource = resources.remove(Constants.META_INF_CONTAINER);
    if (containerResource == null) {
      return Constants.OEBPS_CONTENT_OPF;
    }

    final ContentOpfLocationEntity contentOpfLocation = XML_PARSER.read(ContentOpfLocationEntity.class, containerResource.getInputStream(), false);

    return TextUtils.isEmpty(contentOpfLocation.getRawContentOpfFullPath()) ? Constants.OEBPS_CONTENT_OPF : contentOpfLocation.getRawContentOpfFullPath();
  }

  private static Resources fixEpubHrefs(String packageHref, Resources resources) {
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

  private static Resources fixResourcesIds(String packageHref, ContentOpfEntity contentOpf, Resources resources) {
    return resources;
  }

  @NonNull private static Resources toEpubRawResources(File file, ZipFile zip) throws IOException {
    final Resources resources = new Resources();
    final Enumeration<? extends ZipEntry> entries = zip.entries();

    while (entries.hasMoreElements()) {
      final ZipEntry zipEntry = entries.nextElement();

      if (zipEntry.isDirectory()) {
        continue;
      }

      final String href = zipEntry.getName();
      final String hrefLowerCase = href.toLowerCase();
      final MediaType mediaType = MediatypeService.determineMediaType(href);

      // We load the whole in a streaming way, but with the exception of ContentOpf (the default one) or the MetaInf
      final Resource resource;
      if (Constants.OEBPS_CONTENT_OPF.toLowerCase().contains(hrefLowerCase) || Constants.META_INF_CONTAINER.toLowerCase().contains(hrefLowerCase)) {
        resource = new Resource(zip.getInputStream(zipEntry), file.getName(), (int) zipEntry.getSize(), href);
      } else {
        resource = new StreamingResource(file.getName(), href, mediaType);
      }

      if (resource.getMediaType() == MediatypeService.XHTML) {
        resource.setInputEncoding(Constants.CHARACTER_ENCODING);
      }

      // TODO: 03/10/2017 Fix this to support extra characteristics
      // If the source supports width and height, let's add it
      //if (MediatypeService.isBitmapImage(mediaType)) {
      //  if (!TextUtils.isEmpty(entry.width) && !TextUtils.isEmpty(entry.height)) {
      //    resource.setWidth(entry.width);
      //    resource.setHeight(entry.height);
      //  }
      //}

      resources.add(resource);
    }

    resources.remove("mimetype");

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

  private static TableOfContents toTableOfContents(ContentOpfEntity contentOpf, Book book) {
    final Spine spine = book.getSpine();
    final String tocId = contentOpf.spine.toc;
    final Resource tocResource = book.getResources().getById(tocId);

    // Load this resource

    return null;
  }

}