package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import android.text.Spannable;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.LinkTagHandler;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.css.CompiledRule;

import java.io.*;
import java.util.*;

public interface TextLoader extends LinkTagHandler.LinkCallBack {

  List<CompiledRule> getCSSRules(String href);

  void invalidateCachedText();

  boolean hasCachedBook(String fileName);

  void setFontFamily(FontFamily family);

  void setSerifFontFamily(FontFamily family);

  void setSansSerifFontFamily(FontFamily family);

  void setStripWhiteSpace(boolean stripWhiteSpace);

  void setAllowStyling(boolean allowStyling);

  void setUseColoursFromCSS(boolean useColours);

  Option<Spannable> getCachedTextForResource(Resource resource);

  Spannable getText(final Resource resource, HtmlSpanner.CancellationCallback cancellationCallback) throws IOException;

  void clearCachedText();

  void setCurrentBook(Book book);

  Book initBook(final InputStream contentOpfIs, final InputStream tocResourceIs) throws Exception;

  void closeCurrentBook();

  void setLinkCallBack(LinkTagHandler.LinkCallBack callBack);

  void setResourcesLoader(ResourcesLoader resourcesLoader);

  Option<Integer> getAnchor(String href, String anchor);

  void registerTagNodeHandler(String tag, TagNodeHandler handler);
}
