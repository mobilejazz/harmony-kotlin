package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import com.osbcp.cssparser.CSSParser;
import com.osbcp.cssparser.Rule;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.StreamingResource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.epub.FileEpubReader;
import com.worldreader.reader.epublib.nl.siegmann.epublib.epub.StreamingEpubReader;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.IOUtil;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.AnchorHandler;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.CSSLinkHandler;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.LinkTagHandler;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SystemFontResolver;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.css.CSSCompiler;
import net.nightwhistler.htmlspanner.css.CompiledRule;

import java.io.*;
import java.util.*;

import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.none;
import static jedi.option.Options.option;

public class TextLoader implements LinkTagHandler.LinkTagCallBack {

  public static final String TAG = TextLoader.class.getSimpleName();

  private static final double CACHE_CLEAR_THRESHOLD = 0.75;

  private Book currentBook;
  private Map<String, Spannable> renderedText = new HashMap<>();
  private Map<String, List<CompiledRule>> cssRules = new HashMap<>();

  private Map<String, Map<String, Integer>> anchors = new HashMap<>();
  private List<AnchorHandler> anchorHandlers = new ArrayList<>();

  private final HtmlSpanner htmlSpanner;
  private final ResourcesLoader resourcesLoader;

  private LinkTagHandler.LinkTagCallBack linkTagCallBack;

  public TextLoader(final HtmlSpanner spanner, final ResourcesLoader resourcesLoader) {
    this.htmlSpanner = spanner;
    this.resourcesLoader = resourcesLoader;
    registerCallbacksSpannerHandlers();
  }

  private void registerCallbacksSpannerHandlers() {
    ((LinkTagHandler) getHtmlTagHandler("a")).setCallBack(this);
    ((CSSLinkHandler) getHtmlTagHandler("link")).setTextLoader(this);
  }

  public List<CompiledRule> getCSSRules(String href) {
    if (cssRules.containsKey(href)) {
      return Collections.unmodifiableList(cssRules.get(href));
    }

    final List<CompiledRule> result = new ArrayList<>();

    if (currentBook == null) {
      return result;
    }

    final String strippedHref = href.substring(href.lastIndexOf('/') + 1);

    Resource res = null;
    for (Resource resource : currentBook.getResources().getAll()) {
      if (resource.getHref().endsWith(strippedHref)) {
        res = resource;
        break;
      }
    }

    if (res == null) {
      Log.e(TAG, "Could not find CSS resource " + strippedHref);
      return new ArrayList<>();
    }

    final StringWriter writer = new StringWriter();
    try {
      if (res instanceof StreamingResource && !res.isInitialized()) {
        res.setData(resourcesLoader.loadResource(res));
      }

      final InputStreamReader inputStreamReader = new InputStreamReader(res.getInputStream());

      IOUtil.copy(inputStreamReader, writer);

      List<Rule> rules = CSSParser.parse(writer.toString());
      Log.d(TAG, "Parsed " + rules.size() + " raw rules.");

      for (Rule rule : rules) {
        if (rule.getSelectors().size() == 1 && rule.getSelectors().get(0).toString().equals("@font-face")) {
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
    //String href = null;
    //String fontName = null;
    //
    //for (PropertyValue prop : rule.getPropertyValues()) {
    //  if (prop.getProperty().equals("font-family")) {
    //    fontName = prop.getValue();
    //  }
    //
    //  if (prop.getProperty().equals("src")) {
    //    href = prop.getValue();
    //  }
    //}
    //
    //if (fontName.startsWith("\"") && fontName.endsWith("\"")) {
    //  fontName = fontName.substring(1, fontName.length() - 1);
    //}
    //
    //if (fontName.startsWith("\'") && fontName.endsWith("\'")) {
    //  fontName = fontName.substring(1, fontName.length() - 1);
    //}
    //
    //if (href.startsWith("url(")) {
    //  href = href.substring(4, href.length() - 1);
    //}

    //fontResolver.loadEmbeddedFont(name, href);
  }

  @Override public void onLinkClicked(String href) {
    if (linkTagCallBack != null) {
      linkTagCallBack.onLinkClicked(href);
    }
  }

  public void setLinkTagCallBack(LinkTagHandler.LinkTagCallBack callBack) {
    this.linkTagCallBack = callBack;
  }

  public void registerTagNodeHandler(String tag, TagNodeHandler handler) {
    this.htmlSpanner.registerHandler(tag, handler);
  }

  private void setFontFamily(FontFamily family) {
    ((SystemFontResolver) this.htmlSpanner.getFontResolver()).setDefaultFont(family);
  }

  private void setSerifFontFamily(FontFamily family) {
    ((SystemFontResolver) this.htmlSpanner.getFontResolver()).setSerifFont(family);
  }

  private void setSansSerifFontFamily(FontFamily family) {
    ((SystemFontResolver) this.htmlSpanner.getFontResolver()).setSansSerifFont(family);
  }

  private void setStripWhiteSpace(boolean stripWhiteSpace) {
    this.htmlSpanner.setStripExtraWhiteSpace(stripWhiteSpace);
  }

  private void setUseColoursFromCSS(boolean useColours) {
    this.htmlSpanner.setUseColoursFromStyle(useColours);
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

    for (AnchorHandler handler : anchorHandlers) {
      handler.setCallback(new AnchorHandler.AnchorCallback() {
        @Override public void registerAnchor(String anchor, int position) {
          registerNewAnchor(resource.getHref(), anchor, position);
        }
      });
    }

    final double memoryUsage = Configuration.getMemoryUsage();

    Log.d(TAG, "Current memory usage is " + (int) (memoryUsage * 100) + "%");

    //If memory usage gets over the threshold, try to free up memory
    if (memoryUsage > CACHE_CLEAR_THRESHOLD) {
      Log.d(TAG, "Clearing cached resources.");
      clearCachedText();
      closeLazyLoadedResources();
    }

    //If it's already in memory, use that. If not we need to use resources loader to load a resource
    if (resource instanceof StreamingResource && !resource.isInitialized()) {
      resource.setData(resourcesLoader.loadResource(resource));
    }

    try {
      final Spannable result = htmlSpanner.fromHtml(new InputStreamReader(resource.getInputStream()), cancellationCallback);
      renderedText.put(resource.getHref(), result);
      return result;
    } catch (Exception e) {
      Log.e(TAG, "Caught exception while rendering text", e);
      return new SpannableString(e.getClass().getSimpleName() + ": " + e.getMessage());
    }
  }

  private void closeLazyLoadedResources() {
    if (currentBook != null) {
      for (Resource res : currentBook.getResources().getAll()) {
        res.close();
      }
    }
  }

  public void clearCachedText() {
    anchors.clear();
    renderedText.clear();
    cssRules.clear();
  }

  public Book initBook(final File file) throws Exception {
    closeCurrentBook();
    clearAnchors();
    final Book newBook = FileEpubReader.readFileEpub(file);
    this.currentBook = newBook;
    return newBook;
  }

  public Book initBook(final String contentOpf, final String tocResourcePath) throws Exception {
    closeCurrentBook();
    clearAnchors();

    final InputStream contentOpfIs = resourcesLoader.loadResource(new Resource(contentOpf));
    final InputStream tocResourcesIs = resourcesLoader.loadResource(new Resource(tocResourcePath));

    final Book newBook = StreamingEpubReader.readStreamingEpub(contentOpfIs, tocResourcesIs);
    this.currentBook = newBook;

    return newBook;
  }

  private void clearAnchors() {
    this.anchors = new HashMap<>();
  }

  public Option<Integer> getAnchor(String href, String anchor) {
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
    anchors.clear();
  }

  @Nullable public <T extends TagNodeHandler> T getHtmlTagHandler(final String tag) {
    final TagNodeHandler handlerFor = htmlSpanner.getHandlerFor(tag);
    return handlerFor instanceof AnchorHandler ? (T) ((AnchorHandler) handlerFor).getWrappedHandler() : (T) handlerFor;
  }

  public void fromConfiguration(Configuration config) {
    setFontFamily(config.getSerifFontFamily());
    setSansSerifFontFamily(config.getSansSerifFontFamily());
    setSerifFontFamily(config.getSerifFontFamily());
    setStripWhiteSpace(config.isStripWhiteSpaceEnabled());
    setUseColoursFromCSS(config.isUseColoursFromCSS());
  }
}

