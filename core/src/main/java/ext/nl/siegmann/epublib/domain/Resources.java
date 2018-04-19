package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.StringUtil;

import java.io.*;
import java.util.*;

/**
 * All the resources that make up the book.
 * XHTML files, images and epub xml documents must be here.
 */
public class Resources implements Serializable {

  private static final long serialVersionUID = 2450876953383871451L;

  private static final String IMAGE_PREFIX = "image_";
  private static final String ITEM_PREFIX = "item_";
  private int lastId = 1;

  private Map<String, Resource> resources = new HashMap<>();

  /**
   * Adds a resource to the resources.
   * <p>
   * Fixes the resources id and href if necessary.
   */
  public Resource add(Resource resource) {
    fixResourceHref(resource);
    fixResourceId(resource);
    this.resources.put(resource.getHref(), resource);
    return resource;
  }

  /**
   * Checks the id of the given resource and changes to a unique identifier if it isn't one
   * already.
   */
  private void fixResourceId(Resource resource) {
    String resourceId = resource.getId();

    // first try and create a unique id based on the resource's href
    if (StringUtil.isBlank(resource.getId())) {
      resourceId = StringUtil.substringBeforeLast(resource.getHref(), '.');
      resourceId = StringUtil.substringAfterLast(resourceId, '/');
    }

    resourceId = makeValidId(resourceId, resource);

    // check if the id is unique. if not: create one from scratch
    if (StringUtil.isBlank(resourceId) || containsId(resourceId)) {
      resourceId = createUniqueResourceId(resource);
    }
    resource.setId(resourceId);
  }

  /**
   * Check if the id is a valid identifier. if not: prepend with valid identifier
   */
  private String makeValidId(String resourceId, Resource resource) {
    if (StringUtil.isNotBlank(resourceId) && !Character.isJavaIdentifierStart(resourceId.charAt(0))) {
      resourceId = getResourceItemPrefix(resource) + resourceId;
    }
    return resourceId;
  }

  private String getResourceItemPrefix(Resource resource) {
    String result;
    if (MediatypeService.isBitmapImage(resource.getMediaType())) {
      result = IMAGE_PREFIX;
    } else {
      result = ITEM_PREFIX;
    }
    return result;
  }

  /**
   * Creates a new resource id that is guaranteed to be unique for this set of Resources
   */
  private String createUniqueResourceId(Resource resource) {
    int counter = lastId;
    if (counter == Integer.MAX_VALUE) {
      if (resources.size() == Integer.MAX_VALUE) {
        throw new IllegalArgumentException("Resources contains " + Integer.MAX_VALUE + " elements: no new elements can be added");
      } else {
        counter = 1;
      }
    }
    String prefix = getResourceItemPrefix(resource);
    String result = prefix + counter;
    while (containsId(result)) {
      result = prefix + (++counter);
    }
    lastId = counter;
    return result;
  }

  /**
   * Whether the map of resources already contains a resource with the given id.
   */
  private boolean containsId(String id) {
    if (StringUtil.isBlank(id)) {
      return false;
    }
    for (Resource resource : resources.values()) {
      if (id.equals(resource.getId())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the resource with the given id.
   *
   * @return null if not found
   */
  public Resource getById(String id) {
    if (StringUtil.isBlank(id)) {
      return null;
    }
    for (Resource resource : resources.values()) {
      if (id.equals(resource.getId())) {
        return resource;
      }
    }
    return null;
  }

  /**
   * Remove the resource with the given href.
   *
   * @return the removed resource, null if not found
   */
  public Resource remove(String href) {
    return resources.remove(href);
  }

  private void fixResourceHref(Resource resource) {
    if (StringUtil.isNotBlank(resource.getHref()) && !resources.containsKey(resource.getHref())) {
      return;
    }
    if (StringUtil.isBlank(resource.getHref())) {
      if (resource.getMediaType() == null) {
        throw new IllegalArgumentException("Resource must have either a MediaType or a href");
      }
      int i = 1;
      String href = createHref(resource.getMediaType(), i);
      while (resources.containsKey(href)) {
        href = createHref(resource.getMediaType(), (++i));
      }
      resource.setHref(href);
    }
  }

  private String createHref(MediaType mediaType, int counter) {
    if (MediatypeService.isBitmapImage(mediaType)) {
      return "image_" + counter + mediaType.getDefaultExtension();
    } else {
      return "item_" + counter + mediaType.getDefaultExtension();
    }
  }

  public boolean isEmpty() {
    return resources.isEmpty();
  }

  /**
   * The number of resources
   */
  public int size() {
    return resources.size();
  }

  /**
   * The resources that make up this book.
   * Resources can be xhtml pages, images, xml documents, etc.
   */
  public Map<String, Resource> getResourceMap() {
    return resources;
  }

  public Collection<Resource> getAll() {
    return resources.values();
  }

  /**
   * Whether there exists a resource with the given href
   */
  public boolean containsByHref(String href) {
    return !StringUtil.isBlank(href) && resources.containsKey(StringUtil.substringBefore(href, Constants.FRAGMENT_SEPARATOR_CHAR));
  }

  /**
   * Sets the collection of Resources to the given collection of resources
   */
  public void set(Collection<Resource> resources) {
    this.resources.clear();
    addAll(resources);
  }

  /**
   * Adds all resources from the given Collection of resources to the existing collection.
   */
  public void addAll(Collection<Resource> resources) {
    for (Resource resource : resources) {
      fixResourceHref(resource);
      this.resources.put(resource.getHref(), resource);
    }
  }

  /**
   * Sets the collection of Resources to the given collection of resources
   *
   * @param resources A map with as keys the resources href and as values the Resources
   */
  public void set(Map<String, Resource> resources) {
    this.resources = new HashMap<>(resources);
  }

  /**
   * First tries to find a resource with as id the given idOrHref, if that
   * fails it tries to find one with the idOrHref as href.
   */
  public Resource getByIdOrHref(String idOrHref) {
    Resource resource = getById(idOrHref);
    if (resource == null) {
      resource = getByHref(idOrHref);
    }
    return resource;
  }

  /**
   * Gets the resource with the given href.
   * If the given href contains a fragmentId then that fragment id will be ignored.
   *
   * @return null if not found.
   */
  public Resource getByHref(String href) {
    if (StringUtil.isBlank(href)) {
      return null;
    }
    href = StringUtil.substringBefore(href, Constants.FRAGMENT_SEPARATOR_CHAR);
    return resources.get(href);
  }

  /**
   * Matches a Resource by only the last portion of the href (the filename)
   */
  public Resource getByFileName(String href) {
    if (StringUtil.isBlank(href)) {
      return null;
    }

    href = StringUtil.substringBefore(href, Constants.FRAGMENT_SEPARATOR_CHAR);
    href = StringUtil.substringAfter(href, '/');

    for (Resource res : getAll()) {
      if (res.getHref().endsWith(href)) {
        return res;
      }
    }

    return null;
  }

  /**
   * Gets the first resource (random order) with the give mediatype.
   * <p>
   * Useful for looking up the table of contents as it's supposed to be the only resource with NCX
   * mediatype.
   */
  public Resource findFirstResourceByMediaType(MediaType mediaType) {
    return findFirstResourceByMediaType(resources.values(), mediaType);
  }

  /**
   * Gets the first resource (random order) with the give mediatype.
   * <p>
   * Useful for looking up the table of contents as it's supposed to be the only resource with NCX
   * mediatype.
   */
  public static Resource findFirstResourceByMediaType(Collection<Resource> resources, MediaType mediaType) {
    for (Resource resource : resources) {
      if (resource.getMediaType() == mediaType) {
        return resource;
      }
    }
    return null;
  }

  /**
   * All resources that have the given MediaType.
   */
  public List<Resource> getResourcesByMediaType(MediaType mediaType) {
    final List<Resource> result = new ArrayList<>();
    if (mediaType == null) {
      return result;
    }
    for (Resource resource : getAll()) {
      if (resource.getMediaType() == mediaType) {
        result.add(resource);
      }
    }
    return result;
  }

  /**
   * All Resources that match any of the given list of MediaTypes
   */
  public List<Resource> getResourcesByMediaTypes(MediaType[] mediaTypes) {
    List<Resource> result = new ArrayList<Resource>();
    if (mediaTypes == null) {
      return result;
    }

    // this is the fastest way of doing this according to
    // http://stackoverflow.com/questions/1128723/in-java-how-can-i-test-if-an-array-contains-a-certain-value
    List<MediaType> mediaTypesList = Arrays.asList(mediaTypes);
    for (Resource resource : getAll()) {
      if (mediaTypesList.contains(resource.getMediaType())) {
        result.add(resource);
      }
    }
    return result;
  }

  public Collection<String> getAllHrefs() {
    return resources.keySet();
  }
}
