package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.text.Spannable;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.css.CompiledRule;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.FastBitmapDrawable;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface TextLoader extends LinkTagHandler.LinkCallBack {

  List<CompiledRule> getCSSRules(String href);

  void invalidateCachedText();

  boolean hasCachedBook(String fileName);

  void setHtmlSpanner(HtmlSpanner spanner);

  void setFontResolver(EpubFontResolver resolver);

  void setFontFamily(FontFamily family);

  void setSerifFontFamily(FontFamily family);

  void setSansSerifFontFamily(FontFamily family);

  void setStripWhiteSpace(boolean stripWhiteSpace);

  void setAllowStyling(boolean allowStyling);

  void setUseColoursFromCSS(boolean useColours);

  FastBitmapDrawable getCachedImage(String href);

  boolean hasCachedImage(String href);

  void storeImageInCache(String href, FastBitmapDrawable drawable);

  Option<Spannable> getCachedTextForResource(Resource resource);

  Spannable getText(final Resource resource, HtmlSpanner.CancellationCallback cancellationCallback)
      throws IOException;

  void clearCachedText();

  void setCurrentBook(Book book);

  Book initBook(InputStream is);

  Book initBook(String filename) throws IOException;

  void closeCurrentBook();

  void clearImageCache();

  void setLinkCallBack(LinkTagHandler.LinkCallBack callBack);

  Book getCurrentBook();

  void setResourcesLoader(ResourcesLoader resourcesLoader);

  Option<Integer> getAnchor(String href, String anchor);

  void registerTagNodeHandler(String tag, TagNodeHandler handler);

}
