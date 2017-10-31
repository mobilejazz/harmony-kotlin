package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.spanner;

import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.AnchorHandler;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.CSSLinkHandler;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.LinkTagHandler;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SystemFontResolver;
import org.htmlcleaner.HtmlCleaner;

// Creates a configured HtmlSpanner tailored to Book reading
public class HtmlSpannerFactory {

  public static HtmlSpanner create(final Configuration config) {
    final HtmlSpanner htmlSpanner = new HtmlSpanner(new HtmlCleaner(), new SystemFontResolver());

    // Register special Html handlers (wraps common tags with AnchorHandler to capture anchors)
    htmlSpanner.registerHandler("h1", new AnchorHandler(htmlSpanner.getHandlerFor("h1")));
    htmlSpanner.registerHandler("h2", new AnchorHandler(htmlSpanner.getHandlerFor("h2")));
    htmlSpanner.registerHandler("h3", new AnchorHandler(htmlSpanner.getHandlerFor("h3")));
    htmlSpanner.registerHandler("h4", new AnchorHandler(htmlSpanner.getHandlerFor("h4")));
    htmlSpanner.registerHandler("h5", new AnchorHandler(htmlSpanner.getHandlerFor("h5")));
    htmlSpanner.registerHandler("h6", new AnchorHandler(htmlSpanner.getHandlerFor("h6")));
    htmlSpanner.registerHandler("p", new AnchorHandler(htmlSpanner.getHandlerFor("p")));

    // Add special Html handlers (those that requires registering a callback in TextLoader)
    htmlSpanner.registerHandler("a", new AnchorHandler(new LinkTagHandler()));
    htmlSpanner.registerHandler("link", new CSSLinkHandler());

    // Configure HtmlSpanner with config

    return htmlSpanner;
  }

}
