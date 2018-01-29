package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler;

import android.content.Context;
import android.text.SpannableStringBuilder;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resources;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.DICompanion;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ImageResourceCallback;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import org.htmlcleaner.TagNode;

public abstract class ImageTagHandler extends TagNodeHandler implements ImageResourceCallback.Listener {

  private final Context context;
  private final BookMetadata bm;
  private final ResourcesLoader resourcesLoader;
  private final DICompanion di;
  private final Logger logger;

  public ImageTagHandler(final Context context, final BookMetadata bm, final ResourcesLoader resourcesLoader, DICompanion di, final Logger logger) {
    this.context = context.getApplicationContext();
    this.bm = bm;
    this.resourcesLoader = resourcesLoader;
    this.di = di;
    this.logger = logger;
  }

  @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack span) {
    final String data = obtainImageAttribute(node);

    // Ignore invalid images
    if (data == null) {
      return;
    }

    // Append unicode object replacement char '￼' (http://www.fileformat.info/info/unicode/char/fffc/index.htm).
    // This is later used by Android spans.
    builder.append("\uFFFC");

    // Create an ImageResourceCallback to hold all data related to the image
    final ImageResourceCallback callback = new ImageResourceCallback.Builder()
        .withContext(context)
        .withSpannableBuilder(builder)
        .withMetadata(bm)
        .withData(data)
        .withStart(start)
        .withEnd(builder.length())
        .withHeight(getViewHeight())
        .withWidth(getViewWidth())
        .withVerticalMargin(getViewVerticalMargin())
        .withHorizontal(getViewHorizontalMargin())
        .withRepository(di.streamingBookDataSource)
        .withResources(getResources())
        .withListener(this)
        .withLogger(logger)
        .create();

    // Save it to the resources loader to be later processed (when needed)
    resourcesLoader.registerImageCallback(callback);
  }

  protected abstract int getViewHeight();

  protected abstract int getViewWidth();

  protected abstract int getViewVerticalMargin();

  protected abstract int getViewHorizontalMargin();

  protected abstract Resources getResources();

  private String obtainImageAttribute(TagNode node) {
    String src = node.getAttributeByName("src");

    if (src == null) {
      src = node.getAttributeByName("href");
    }

    if (src == null) {
      src = node.getAttributeByName("xlink:href");
    }

    return src;
  }
}