package com.worldreader.reader.epublib.nl.siegmann.epublib.domain;

import android.util.Log;
import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.service.MediatypeService;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.IOUtil;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;

import java.io.*;
import java.util.zip.*;

/**
 * Represents a resource that is part of the epub.
 * A resource can be a html file, image, xml, etc.
 *
 * @author paul
 */
public class Resource implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1043946707835004037L;

  protected String id;
  protected String title;
  protected String href;
  protected String originalHref;
  protected MediaType mediaType;
  protected String width;
  protected String height;
  protected String inputEncoding = Constants.CHARACTER_ENCODING;
  protected byte[] data;

  protected String fileName;
  protected long cachedSize;

  //private static final Logger LOG = LoggerFactory.getLogger(Resource.class);

  /**
   * Creates an empty Resource with the given href.
   *
   * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
   *
   * @param href The location of the resource within the epub. Example: "chapter1.html".
   */
  public Resource(String href) {
    this(null, new byte[0], href, MediatypeService.determineMediaType(href));
  }

  /**
   * Creates a Resource with the given data and MediaType.
   * The href will be automatically generated.
   *
   * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
   *
   * @param data The Resource's contents
   * @param mediaType The MediaType of the Resource
   */
  public Resource(byte[] data, MediaType mediaType) {
    this(null, data, null, mediaType);
  }

  /**
   * Creates a resource with the given data at the specified href.
   * The MediaType will be determined based on the href extension.
   *
   * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
   *
   * @param data The Resource's contents
   * @param href The location of the resource within the epub. Example: "chapter1.html".
   * @see nl.siegmann.epublib.service.MediatypeService.determineMediaType(String)
   */
  public Resource(byte[] data, String href) {
    this(null, data, href, MediatypeService.determineMediaType(href), Constants.CHARACTER_ENCODING);
  }

  /**
   * Creates a resource with the data from the given Reader at the specified href.
   * The MediaType will be determined based on the href extension.
   *
   * @param in The Resource's contents
   * @param href The location of the resource within the epub. Example: "cover.jpg".
   * @see nl.siegmann.epublib.service.MediatypeService.determineMediaType(String)
   */
  public Resource(Reader in, String href) throws IOException {
    this(null, IOUtil.toByteArray(in, Constants.CHARACTER_ENCODING), href,
        MediatypeService.determineMediaType(href), Constants.CHARACTER_ENCODING);
  }

  /**
   * Creates a resource with the data from the given InputStream at the specified href.
   * The MediaType will be determined based on the href extension.
   *
   * @param in The Resource's contents
   * @param href The location of the resource within the epub. Example: "cover.jpg".
   * @see nl.siegmann.epublib.service.MediatypeService.determineMediaType(String)
   *
   * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
   *
   * It is recommended to us the
   * @see Resource.Resource(Reader, String)
   * method for creating textual (html/css/etc) resources to prevent encoding problems.
   * Use this method only for binary Resources like images, fonts, etc.
   */
  public Resource(InputStream in, String href) throws IOException {
    this(null, IOUtil.toByteArray(in), href, MediatypeService.determineMediaType(href));
  }

  /**
   * Creates a Resource that tries to load the data, but falls back to lazy loading.
   *
   * If the size of the resource is known ahead of time we can use that to allocate
   * a matching byte[]. If this succeeds we can safely load the data.
   *
   * If it fails we leave the data null for now and it will be lazy-loaded when
   * it is accessed.
   *
   * @throws IOException
   */
  public Resource(InputStream in, String fileName, int length, String href) throws IOException {
    this(null, IOUtil.toByteArray(in, length), href, MediatypeService.determineMediaType(href));
    this.fileName = fileName;
    this.cachedSize = length;
  }

  /**
   * Creates a Lazy resource, by not actually loading the data for this entry.
   *
   * The data will be loaded on the first call to getData()
   *
   * @param fileName the fileName for the epub we're created from.
   * @param size the size of this resource.
   * @param href The resource's href within the epub.
   */
  public Resource(String fileName, long size, String href) {
    this(null, null, href, MediatypeService.determineMediaType(href));
    this.fileName = fileName;
    this.cachedSize = size;
  }

  /**
   * Creates a resource with the given id, data, mediatype at the specified href.
   * Assumes that if the data is of a text type (html/css/etc) then the encoding will be UTF-8
   *
   * @param id The id of the Resource. Internal use only. Will be auto-generated if it has a
   * null-value.
   * @param data The Resource's contents
   * @param href The location of the resource within the epub. Example: "chapter1.html".
   * @param mediaType The resources MediaType
   */
  public Resource(String id, byte[] data, String href, MediaType mediaType) {
    this(id, data, href, mediaType, Constants.CHARACTER_ENCODING);
  }

  /**
   * Creates a resource with the given id, data, mediatype at the specified href.
   * If the data is of a text type (html/css/etc) then it will use the given inputEncoding.
   *
   * @param id The id of the Resource. Internal use only. Will be auto-generated if it has a
   * null-value.
   * @param data The Resource's contents
   * @param href The location of the resource within the epub. Example: "chapter1.html".
   * @param mediaType The resources MediaType
   * @param inputEncoding If the data is of a text type (html/css/etc) then it will use the given
   * inputEncoding.
   */
  public Resource(String id, byte[] data, String href, MediaType mediaType, String inputEncoding) {
    this.id = id;
    this.href = href;
    this.originalHref = href;
    this.mediaType = mediaType;
    this.inputEncoding = inputEncoding;
    this.data = data;
  }

  /**
   * Gets the contents of the Resource as an InputStream.
   *
   * @return The contents of the Resource.
   * @throws IOException
   */
  public InputStream getInputStream() throws IOException {
    if (isInitialized()) {
      return new ByteArrayInputStream(getData());
    } else {
      return getResourceStream();
    }
  }

  /**
   * Initializes the resource by loading its data into memory.
   *
   * @throws IOException
   */
  public void initialize() throws IOException {
    getData();
  }

  /**
   * The contents of the resource as a byte[]
   *
   * If this resource was lazy-loaded and the data was not yet loaded,
   * it will be loaded into memory at this point.
   * This included opening the zip file, so expect a first load to be slow.
   *
   * @return The contents of the resource
   */
  public byte[] getData() throws IOException {

    if (data == null) {
      Log.e("epublib", "Initializing lazy resource " + fileName + "#" + this.href);

      InputStream in = getResourceStream();
      byte[] readData = IOUtil.toByteArray(in, (int) this.cachedSize);
      if (readData == null) {
        throw new IOException("Could not lazy-load data.");
      } else {
        this.data = readData;
      }

      in.close();
    }

    return data;
  }

  private InputStream getResourceStream() throws FileNotFoundException, IOException {
    ZipFile zipResource = new ZipFile(fileName);
    ZipEntry zipEntry = zipResource.getEntry(originalHref);
    if (zipEntry == null) {
      zipResource.close();
      throw new IllegalStateException("Cannot find resources href in the epub file");
    }
    return new ResourceInputStream(zipResource.getInputStream(zipEntry), zipResource);
  }

  /**
   * Tells this resource to release its cached data.
   *
   * If this resource was not lazy-loaded, this is a no-op.
   */
  public void close() {
    if (this.fileName != null) {
      this.data = null;
    }
  }

  /**
   * Sets the data of the Resource.
   * If the data is a of a different type then the original data then make sure to change the
   * MediaType.
   */
  public void setData(byte[] data) {
    this.data = data;
  }

  /**
   * Sets the data of the Resource.
   * If the data is a of a different type then the original data then make sure to change the
   * MediaType.
   */
  public void setData(InputStream inputStream) throws IOException {
    this.setData(IOUtil.toByteArray(inputStream));
  }

  /**
   * Returns if the data for this resource has been loaded into memory.
   *
   * @return true if data was loaded.
   */
  public boolean isInitialized() {
    return data != null;
  }

  /**
   * Returns the size of this resource in bytes.
   *
   * @return the size.
   */
  public long getSize() {
    if (data != null) {
      return data.length;
    }

    return cachedSize;
  }

  /**
   * If the title is found by scanning the underlying html document then it is cached here.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the Resource's id: Make sure it is unique and a valid identifier.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * The resources Id.
   *
   * Must be both unique within all the resources of this book and a valid identifier.
   */
  public String getId() {
    return id;
  }

  /**
   * The location of the resource within the contents folder of the epub file.
   *
   * Example:<br/>
   * images/cover.jpg<br/>
   * content/chapter1.xhtml<br/>
   */
  public String getHref() {
    return href;
  }

  /**
   * Returns the full href, including path.
   */
  public String getOriginalHref() {
    return originalHref;
  }

  /**
   * Sets the Resource's href.
   */
  public void setHref(String href) {
    this.href = href;
  }

  /**
   * The character encoding of the resource.
   * Is allowed to be null for non-text resources like images.
   */
  public String getInputEncoding() {
    return inputEncoding;
  }

  /**
   * Sets the Resource's input character encoding.
   */
  public void setInputEncoding(String encoding) {
    this.inputEncoding = encoding;
  }

  /**
   * Gets the hashCode of the Resource's href.
   */
  public int hashCode() {
    return href.hashCode();
  }

  /**
   * Checks to see of the given resourceObject is a resource and whether its href is equal to this
   * one.
   */
  public boolean equals(Object resourceObject) {
    return resourceObject instanceof Resource && href.equals(((Resource) resourceObject).getHref());
  }

  /**
   * This resource's mediaType.
   */
  public MediaType getMediaType() {
    return mediaType;
  }

  public void setMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getWidth() {
    return width;
  }

  public void setWidth(final String width) {
    this.width = width;
  }

  public String getHeight() {
    return height;
  }

  public void setHeight(final String height) {
    this.height = height;
  }

  public String toString() {
    return StringUtil.toString("id", id, "title", title, "encoding", inputEncoding, "mediaType",
        mediaType, "href", href, "size", (data == null ? 0 : data.length));
  }
}
