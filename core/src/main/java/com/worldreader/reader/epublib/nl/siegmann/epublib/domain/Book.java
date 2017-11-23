package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import java.io.*;
import java.util.*;

/**
 * Representation of a Book.
 * <p>
 * All resources of a Book (html, css, xml, fonts, images) are represented as Resources. See
 * getResources() for access to these.<br/>
 * A Book as 3 indexes into these Resources, as per the epub specification.<br/>
 * <dl>
 * <dt>Spine</dt>
 * <dd>these are the Resources to be shown when a user reads the book from start to finish.</dd>
 * <dt>Table of Contents<dt>
 * <dd>The table of contents. Table of Contents references may be in a different order and contain
 * different Resources than the spine, and often do.
 * <dt>Guide</dt>
 * <dd>The Guide has references to a set of special Resources like the cover page, the Glossary, the
 * copyright page, etc.
 * </dl>
 * <p/>
 * The complication is that these 3 indexes may and usually do point to different pages.
 * A chapter may be split up in 2 pieces to fit it in to memory. Then the spine will contain both
 * pieces, but the Table of Contents only the first.
 * The Content page may be in the Table of Contents, the Guide, but not in the Spine.
 * Etc.
 * <p/>
 */
public class Book implements Serializable {

  private static final long serialVersionUID = 2068355170895770100L;

  private Resources resources = new Resources();
  private Metadata metadata = new Metadata();
  private Spine spine = new Spine();
  private TableOfContents tableOfContents = new TableOfContents();
  private Guide guide = new Guide();
  private Resource opfResource;
  private Resource ncxResource;

  public static Builder builder() {
    return new Builder();
  }

  private Book(Builder builder) {
    this.resources = builder.resources;
    this.metadata = builder.metadata;
    this.spine = builder.spine;
    this.tableOfContents = builder.tableOfContents;
    this.guide = builder.guide;
    this.opfResource = builder.opfResource;
    this.ncxResource = builder.ncxResource;
  }

  /**
   * The Book's metadata (titles, authors, etc)
   */
  public Metadata getMetadata() {
    return metadata;
  }

  /**
   * The collection of all images, chapters, sections, xhtml files, stylesheets, etc that make up
   * the book.
   */
  public Resources getResources() {
    return resources;
  }

  /**
   * The sections of the book that should be shown if a user reads the book from start to finish.
   */
  public Spine getSpine() {
    return spine;
  }

  /**
   * The Table of Contents of the book.
   */
  public TableOfContents getTableOfContents() {
    return tableOfContents;
  }

  /**
   * The book's cover page.
   * An XHTML document containing a link to the cover image.
   */
  public Resource getCoverPage() {
    Resource coverPage = guide.getCoverPage();
    if (coverPage == null) {
      coverPage = spine.getResource(0);
    }
    return coverPage;
  }

  /**
   * Gets the first non-blank title from the book's metadata.
   */
  public String getTitle() {
    return getMetadata().getFirstTitle();
  }

  /**
   * The guide; contains references to special sections of the book like colophon, glossary, etc.
   */
  public Guide getGuide() {
    return guide;
  }

  /**
   * All Resources of the Book that can be reached via the Spine, the TableOfContents or the Guide.
   * <p/>
   * Consists of a list of "reachable" resources:
   * <ul>
   * <li>The coverpage</li>
   * <li>The resources of the Spine that are not already in the result</li>
   * <li>The resources of the Table of Contents that are not already in the result</li>
   * <li>The resources of the Guide that are not already in the result</li>
   * </ul>
   * To get all html files that make up the epub file use
   */
  public List<Resource> getContents() {
    Map<String, Resource> result = new LinkedHashMap<>();
    addToContentsResult(getCoverPage(), result);

    for (SpineReference spineReference : getSpine().getSpineReferences()) {
      addToContentsResult(spineReference.getResource(), result);
    }

    for (Resource resource : getTableOfContents().getAllUniqueResources()) {
      addToContentsResult(resource, result);
    }

    for (GuideReference guideReference : getGuide().getReferences()) {
      addToContentsResult(guideReference.getResource(), result);
    }

    return new ArrayList<>(result.values());
  }

  private static void addToContentsResult(Resource resource, Map<String, Resource> allReachableResources) {
    if (resource != null && (!allReachableResources.containsKey(resource.getHref()))) {
      allReachableResources.put(resource.getHref(), resource);
    }
  }

  public Resource getOpfResource() {
    return opfResource;
  }

  public Resource getNcxResource() {
    return ncxResource;
  }

  public static final class Builder {

    private Resources resources;
    private Metadata metadata;
    private Spine spine;
    private TableOfContents tableOfContents;
    private Guide guide;
    private Resource opfResource;
    private Resource ncxResource;

    private Builder() {
    }

    public Builder withResources(Resources val) {
      resources = val;
      return this;
    }

    public Builder withMetadata(Metadata val) {
      metadata = val;
      return this;
    }

    public Builder withSpine(Spine val) {
      spine = val;
      return this;
    }

    public Builder withTableOfContents(TableOfContents val) {
      tableOfContents = val;
      return this;
    }

    public Builder withGuide(Guide val) {
      guide = val;
      return this;
    }

    public Builder withOpfResource(Resource val) {
      opfResource = val;
      return this;
    }

    public Builder withNcxResource(Resource val) {
      ncxResource = val;
      return this;
    }

    public Book build() {
      return new Book(this);
    }
  }

}

