package net.nightwhistler.pageturner.view.bookview.spanner;

import com.worldreader.reader.wr.configuration.ReaderConfig;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SystemFontResolver;
import net.nightwhistler.pageturner.view.bookview.nodehandler.AnchorHandler;
import net.nightwhistler.pageturner.view.bookview.nodehandler.CSSLinkHandler;
import net.nightwhistler.pageturner.view.bookview.nodehandler.LinkTagHandler;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;

// Creates a configured HtmlSpanner tailored to Book reading
public class HtmlSpannerFactory {

  public static HtmlSpanner create(final ReaderConfig config) {
    final HtmlSpanner htmlSpanner = new HtmlSpanner(createHtmlCleaner(), new SystemFontResolver());

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

  private static HtmlCleaner createHtmlCleaner() {
    HtmlCleaner result = new HtmlCleaner();
    CleanerProperties cleanerProperties = result.getProperties();
    cleanerProperties.setAdvancedXmlEscape(true);
    cleanerProperties.setOmitXmlDeclaration(true);
    cleanerProperties.setOmitDoctypeDeclaration(false);
    cleanerProperties.setTranslateSpecialEntities(true);
    cleanerProperties.setTransResCharsToNCR(true);
    cleanerProperties.setRecognizeUnicodeChars(true);
    cleanerProperties.setIgnoreQuestAndExclam(true);
    cleanerProperties.setUseEmptyElementTags(false);
    cleanerProperties.setPruneTags("script,title");
    return result;
  }

}
