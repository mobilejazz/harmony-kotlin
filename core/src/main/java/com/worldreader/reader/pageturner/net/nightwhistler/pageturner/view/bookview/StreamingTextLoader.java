package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import com.osbcp.cssparser.CSSParser;
import com.osbcp.cssparser.PropertyValue;
import com.osbcp.cssparser.Rule;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.css.CSSCompiler;
import net.nightwhistler.htmlspanner.css.CompiledRule;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.FastBitmapDrawable;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.StreamingResource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.epub.EpubReader;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.none;
import static jedi.option.Options.option;

public class StreamingTextLoader implements TextLoader {

  public static final String TAG = StreamingTextLoader.class.getSimpleName();

  /**
   * We start clearing the cache if memory usage exceeds 75%.
   */
  private static final double CACHE_CLEAR_THRESHOLD = 0.75;

  private static StreamingTextLoader INSTANCE;

  private Book currentBook;
  private Map<String, Spannable> renderedText = new HashMap<>();
  private Map<String, List<CompiledRule>> cssRules = new HashMap<>();

  private Map<String, FastBitmapDrawable> imageCache = new HashMap<>();

  private Map<String, Map<String, Integer>> anchors = new HashMap<>();
  private List<AnchorHandler> anchorHandlers = new ArrayList<>();

  private HtmlSpanner htmlSpanner;
  private EpubFontResolver fontResolver;

  private LinkTagHandler.LinkCallBack linkCallBack;
  private ResourcesLoader resourcesLoader;

  public static StreamingTextLoader getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new StreamingTextLoader();
    }
    return INSTANCE;
  }

  public void setHtmlSpanner(HtmlSpanner spanner) {
    this.htmlSpanner = spanner;
    this.htmlSpanner.setFontResolver(fontResolver);

    spanner.registerHandler("a", registerAnchorHandler(new LinkTagHandler(this)));

    spanner.registerHandler("h1", registerAnchorHandler(spanner.getHandlerFor("h1")));
    spanner.registerHandler("h2", registerAnchorHandler(spanner.getHandlerFor("h2")));
    spanner.registerHandler("h3", registerAnchorHandler(spanner.getHandlerFor("h3")));
    spanner.registerHandler("h4", registerAnchorHandler(spanner.getHandlerFor("h4")));
    spanner.registerHandler("h5", registerAnchorHandler(spanner.getHandlerFor("h5")));
    spanner.registerHandler("h6", registerAnchorHandler(spanner.getHandlerFor("h6")));

    spanner.registerHandler("p", registerAnchorHandler(spanner.getHandlerFor("p")));

    spanner.registerHandler("link", new CSSLinkHandler(this));
  }

  public void setFontResolver(EpubFontResolver resolver) {
    this.fontResolver = resolver;
    this.htmlSpanner.setFontResolver(fontResolver);
  }

  public void registerCustomFont(String name, String href) {
    Log.d(TAG, "Registering custom font " + name + " with href " + href);
    this.fontResolver.loadEmbeddedFont(name, href);
  }

  public List<CompiledRule> getCSSRules(String href) {
    if (this.cssRules.containsKey(href)) {
      return Collections.unmodifiableList(cssRules.get(href));
    }

    List<CompiledRule> result = new ArrayList<>();

    if (currentBook == null) {
      return result;
    }

    String strippedHref = href.substring(href.lastIndexOf('/') + 1);

    Resource res = null;

    for (Resource resource : this.currentBook.getResources().getAll()) {
      if (resource.getHref().endsWith(strippedHref)) {
        res = resource;
        break;
      }
    }

    if (res == null) {
      Log.e(TAG, "Could not find CSS resource " + strippedHref);
      return new ArrayList<>();
    }

    StringWriter writer = new StringWriter();
    try {
      if (res instanceof StreamingResource && !res.isInitialized()) {
        res.setData(resourcesLoader.loadResource(res));
      }
      IOUtil.copy(res.getReader(), writer);

      List<Rule> rules = CSSParser.parse(writer.toString());
      Log.d(TAG, "Parsed " + rules.size() + " raw rules.");

      for (Rule rule : rules) {

        if (rule.getSelectors().size() == 1 && rule.getSelectors()
            .get(0)
            .toString()
            .equals("@font-face")) {
          handleFontLoadingRule(rule);
        } else {
          result.add(CSSCompiler.compile(rule, htmlSpanner));
        }
      }
    } catch (IOException io) {
      Log.e(TAG, "Error while reading resource", io);
      return new ArrayList<>();
    } catch (Exception e) {
      Log.e(TAG, "Error reading CSS file", e);
    } finally {
      res.close();
    }

    cssRules.put(href, result);

    Log.d(TAG, "Compiled " + result.size() + " CSS rules.");

    return result;
  }

  public void invalidateCachedText() {
    this.renderedText.clear();
  }

  private void handleFontLoadingRule(Rule rule) {
    String href = null;
    String fontName = null;

    for (PropertyValue prop : rule.getPropertyValues()) {
      if (prop.getProperty().equals("font-family")) {
        fontName = prop.getValue();
      }

      if (prop.getProperty().equals("src")) {
        href = prop.getValue();
      }
    }

    if (fontName.startsWith("\"") && fontName.endsWith("\"")) {
      fontName = fontName.substring(1, fontName.length() - 1);
    }

    if (fontName.startsWith("\'") && fontName.endsWith("\'")) {
      fontName = fontName.substring(1, fontName.length() - 1);
    }

    if (href.startsWith("url(")) {
      href = href.substring(4, href.length() - 1);
    }

    registerCustomFont(fontName, href);
  }

  private AnchorHandler registerAnchorHandler(TagNodeHandler wrapThis) {
    final AnchorHandler handler = new AnchorHandler(wrapThis);
    anchorHandlers.add(handler);
    return handler;
  }

  @Override public void linkClicked(String href) {
    if (linkCallBack != null) {
      linkCallBack.linkClicked(href);
    }
  }

  public void setLinkCallBack(LinkTagHandler.LinkCallBack callBack) {
    this.linkCallBack = callBack;
  }

  @Override public void registerTagNodeHandler(String tag, TagNodeHandler handler) {
    this.htmlSpanner.registerHandler(tag, handler);
  }

  public boolean hasCachedBook(String fileName) {
    return false;
  }

  public Book initBook(String fileName) throws IOException {
    throw new UnsupportedOperationException("This operation is not supported by this loader!");
  }

  public Book getCurrentBook() {
    return this.currentBook;
  }

  @Override public void setResourcesLoader(ResourcesLoader resourcesLoader) {
    this.resourcesLoader = resourcesLoader;
  }

  public void setFontFamily(FontFamily family) {
    this.fontResolver.setDefaultFont(family);
  }

  public void setSerifFontFamily(FontFamily family) {
    this.fontResolver.setSerifFont(family);
  }

  public void setSansSerifFontFamily(FontFamily family) {
    this.fontResolver.setSansSerifFont(family);
  }

  public void setStripWhiteSpace(boolean stripWhiteSpace) {
    this.htmlSpanner.setStripExtraWhiteSpace(stripWhiteSpace);
  }

  public void setAllowStyling(boolean allowStyling) {
    this.htmlSpanner.setAllowStyling(allowStyling);
  }

  public void setUseColoursFromCSS(boolean useColours) {
    this.htmlSpanner.setUseColoursFromStyle(useColours);
  }

  public FastBitmapDrawable getCachedImage(String href) {
    return imageCache.get(href);
  }

  public boolean hasCachedImage(String href) {
    return imageCache.containsKey(href);
  }

  public void storeImageInCache(String href, FastBitmapDrawable drawable) {
    this.imageCache.put(href, drawable);
  }

  private void registerNewAnchor(String href, String anchor, int position) {
    if (!anchors.containsKey(href)) {
      anchors.put(href, new HashMap<String, Integer>());
    }

    anchors.get(href).put(anchor, position);
  }

  public Option<Spannable> getCachedTextForResource(Resource resource) {

    Log.d(TAG, "Checking for cached resource: " + resource);

    return option(renderedText.get(resource.getHref()));
  }

  public Spannable getText(final Resource resource, HtmlSpanner.CancellationCallback cancellationCallback) throws IOException {
    final Option<Spannable> cached = getCachedTextForResource(resource);

    if (!isEmpty(cached)) {
      return cached.unsafeGet();
    }

    for (AnchorHandler handler : this.anchorHandlers) {
      handler.setCallback(new AnchorHandler.AnchorCallback() {
        @Override public void registerAnchor(String anchor, int position) {
          StreamingTextLoader.this.registerNewAnchor(resource.getHref(), anchor, position);
        }
      });
    }

    final double memoryUsage = Configuration.getMemoryUsage();
    final double bitmapUsage = Configuration.getBitmapMemoryUsage();

    Log.d(TAG, "Current memory usage is " + (int) (memoryUsage * 100) + "%");
    Log.d(TAG, "Current bitmap memory usage is " + (int) (bitmapUsage * 100) + "%");

    //If memory usage gets over the threshold, try to free up memory
    if (memoryUsage > CACHE_CLEAR_THRESHOLD || bitmapUsage > CACHE_CLEAR_THRESHOLD) {
      Log.d(TAG, "Clearing cached resources.");
      clearCachedText();
      closeLazyLoadedResources();
    }

    //If it's already in memory, use that. If not we need to use resources loader to load a resource
    if (resource instanceof StreamingResource && !resource.isInitialized()) {
      resource.setData(resourcesLoader.loadResource(resource));
    }

    Spannable result;

    try {
      result = htmlSpanner.fromHtml(resource.getReader(), cancellationCallback);
      renderedText.put(resource.getHref(), result);
    } catch (Exception e) {
      Log.e(TAG, "Caught exception while rendering text", e);
      result = new SpannableString(e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    return result;
  }

  private void closeLazyLoadedResources() {
    if (currentBook != null) {
      for (Resource res : currentBook.getResources().getAll()) {
        res.close();
      }
    }
  }

  public void clearCachedText() {
    clearImageCache();
    anchors.clear();
    renderedText.clear();
    cssRules.clear();
  }

  @Override public void setCurrentBook(Book book) {
    this.currentBook = book;
  }

  @Override public Book initBook(InputStream is) {
    closeCurrentBook();

    this.anchors = new HashMap<>();

    final EpubReader epubReader = new EpubReader();
    final Book newBook = epubReader.readEpubStreaming(is);

    this.currentBook = newBook;

    return newBook;
  }

  @Override public Option<Integer> getAnchor(String href, String anchor) {
    if (this.anchors.containsKey(href)) {
      Map<String, Integer> nestedMap = this.anchors.get(href);
      return option(nestedMap.get(anchor));
    }

    return none();
  }

  public void closeCurrentBook() {
    if (currentBook != null) {
      for (Resource res : currentBook.getResources().getAll()) {
        res.setData((byte[]) null); //Release the byte[] data.
      }
    }

    currentBook = null;
    renderedText.clear();
    clearImageCache();
    anchors.clear();
  }

  public void clearImageCache() {
    for (Map.Entry<String, FastBitmapDrawable> draw : imageCache.entrySet()) {
      draw.getValue().destroy();
    }

    imageCache.clear();
  }
}

