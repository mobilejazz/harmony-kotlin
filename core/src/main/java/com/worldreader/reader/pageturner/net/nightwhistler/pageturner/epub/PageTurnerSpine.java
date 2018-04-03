package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.worldreader.core.R;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Spine;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.SpineReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TOCReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TableOfContents;
import jedi.option.Option;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.none;
import static jedi.option.Options.option;

public class PageTurnerSpine implements Iterable<PageTurnerSpine.SpineEntry> {

  private final Book book;
  private final List<SpineEntry> entries;

  private int position;

  public PageTurnerSpine(Context c, Book book) {
    this.book = book;
    entries = new ArrayList<>();
    position = 0;

    final Map<String, String> blacklist = createBlackList(c);
    final Set<String> blacklistKeys = blacklist.keySet();

    final TableOfContents toc = book.getTableOfContents();

    final Spine spine = book.getSpine();
    final List<SpineReference> spineReferences = spine.getSpineReferences();

    for (SpineReference ref : spineReferences) {
      final Resource res = ref.getResource();
      final String resourceId = res.getId();

      final String blacklistKey = isBlackListed(blacklistKeys, resourceId);
      if (TextUtils.isEmpty(blacklistKey)) {
        addResource(res);
        continue;
      }

      final String name = blacklist.get(blacklistKey);
      if (!TextUtils.isEmpty(name)) {
        final TOCReference tocReference = new TOCReference(name, res);
        toc.addTOCReference(tocReference);
      }
    }
  }

  private Map<String, String> createBlackList(Context c) {
    final Resources r = c.getResources();

    return new HashMap<String, String>() {{
      put("toc", r.getString(R.string.ls_toc));
      put("nav", r.getString(R.string.ls_toc));
      put("copy", r.getString(R.string.ls_copy));
      put("copyright", r.getString(R.string.ls_copy));
      put("title", r.getString(R.string.ls_title));
      put("dedi", r.getString(R.string.ls_dedi));
      put("dedication", r.getString(R.string.ls_dedi));
      put("epilogue", r.getString(R.string.ls_epilogue));
      put("ack", r.getString(R.string.ls_ack));
      put("acknowledgements", r.getString(R.string.ls_ack));
      put("backcover", r.getString(R.string.ls_back));
      put("back", r.getString(R.string.ls_back));
      put("bcover", r.getString(R.string.ls_back));
      put("index", r.getString(R.string.ls_index));
      put("contents", r.getString(R.string.ls_toc));
      put("credits", r.getString(R.string.ls_credits));
      put("morebyauthor", r.getString(R.string.ls_moreByAuthor));
      put("morebypublisher", r.getString(R.string.ls_moreByPublisher));
    }};
  }

  private String isBlackListed(Set<String> keySet, String resourceId) {
    if (TextUtils.isEmpty(resourceId)) {
      return null;
    }

    for (String key : keySet) {
      final boolean startsWith = resourceId.startsWith(key);
      if (startsWith) {
        return key;
      }
    }

    return null;
  }

  @NonNull @Override public Iterator<SpineEntry> iterator() {
    return entries.iterator();
  }

  private void addResource(Resource resource) {
    final SpineEntry e = new SpineEntry();
    e.title = resource.getTitle();
    e.resource = resource;
    e.href = resource.getHref();
    e.size = (int) resource.getSize();
    entries.add(e);
  }

  /**
   * Returns the number of entries in this spine.
   * This includes the generated cover.
   */
  public int size() {
    return entries.size();
  }

  /**
   * Navigates one entry forward.
   *
   * @return false if we're already at the end.
   */
  public boolean navigateForward() {
    if (position == size() - 1) {
      return false;
    }

    position++;
    return true;
  }

  /**
   * Navigates one entry back.
   *
   * @return false if we're already at the start
   */
  public boolean navigateBack() {
    if (position == 0) {
      return false;
    }

    position--;
    return true;
  }

  /**
   * Returns the current resource, or null
   * if there is none.
   */
  public Option<Resource> getCurrentResource() {
    return getResourceForIndex(position);
  }

  /**
   * Returns the resource after the current one
   */
  public Option<Resource> getNextResource() {
    return getResourceForIndex(position + 1);
  }

  public Option<Resource> getPreviousResource() {
    return getResourceForIndex(position - 1);
  }

  private Option<Resource> getResourceForIndex(int index) {
    if (entries.isEmpty() || index < 0 || index >= entries.size()) {
      return none();
    }

    return option(entries.get(index).resource);
  }

  /**
   * Resolves a href relative to the current resource.
   */
  public String resolveHref(String href) {
    final Option<Resource> res = getCurrentResource();
    if (!isEmpty(res)) {
      final Resource actualResource = res.unsafeGet();
      if (actualResource.getHref() != null) {
        return resolveHref(href, actualResource.getHref());
      }
    }
    return href;
  }

  private static String resolveHref(String href, String against) {
    try {
      return new URI(encode(against)).resolve(encode(href)).getPath();
    } catch (URISyntaxException u) {
      return href;
    } catch (IllegalArgumentException i) {
      return href;
    }
  }

  private static String encode(String input) {
    final StringBuilder resultStr = new StringBuilder();
    for (char ch : input.toCharArray()) {
      if (ch == '\\') { //Some books use \ as a separator... invalid, but we'll try to fix it
        resultStr.append('/');
      } else if (isUnsafe(ch)) {
        resultStr.append('%');
        resultStr.append(toHex(ch / 16));
        resultStr.append(toHex(ch % 16));
      } else {
        resultStr.append(ch);
      }
    }
    return resultStr.toString();
  }

  private static char toHex(int ch) {
    return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
  }

  /**
   * This is slightly unsafe: it lets / and % pass, making
   * multiple encodes safe.
   */
  private static boolean isUnsafe(char ch) {
    return ch > 128 || ch < 0 || " %$&+,:;=?@<>#[]".indexOf(ch) >= 0;
  }

  /**
   * Returns the href of the current resource.
   */
  public Option<String> getCurrentHref() {
    if (entries.size() > 0) {
      return option(entries.get(position).href);
    } else {
      return none();
    }
  }

  /**
   * Navigates to a specific point in the spine.
   */
  public void navigateByIndex(int index) {
    if (index < 0 || index >= size()) {
      return;
    }
    position = index;
  }

  /**
   * Returns the current position in the spine.
   */
  public int getPosition() {
    return position;
  }

  /**
   * Navigates to the point with the given href.
   *
   * @return false if that point did not exist.
   */
  public boolean navigateByHref(String href) {
    final String encodedHref = encode(href);
    final int size = size();
    for (int i = 0; i < size; i++) {
      final String entry = encode(entries.get(i).href);
      if (entry.equals(encodedHref)) {
        position = i;
        return true;
      }
    }
    return false;
  }

  public Long getSizeForCurrentResource() {
    final Option<Resource> currentResource = getCurrentResource();
    if (currentResource == null) {
      return null;
    }
    return currentResource.unsafeGet().getSize();
  }

  public Book getBook() {
    return book;
  }

  public static class SpineEntry {

    private String title;
    private Resource resource;
    private String href;

    private int size;

    public String getTitle() {
      return title;
    }

    public int getSize() {
      return size;
    }

    public Resource getResource() {
      return resource;
    }

    public String getHref() {
      return href;
    }
  }
}