package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler;

import android.text.SpannableStringBuilder;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import org.htmlcleaner.TagNode;

public class ImageTagHandler extends TagNodeHandler {

  private final ResourcesLoader resourcesLoader;
  private final PageTurnerSpine spine;

  public ImageTagHandler(final ResourcesLoader resourcesLoader, final PageTurnerSpine spine) {
    this.resourcesLoader = resourcesLoader;
    this.spine = spine;
  }

  @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack span) {
    final String src = obtainImageAttribute(node);

    //if (src == null) {
    //  return;
    //}
    //
    //if (src.startsWith("data:image")) {
    //  try {
    //    final String dataString = src.substring(src.indexOf(',') + 1);
    //    final byte[] binData = Base64.decode(dataString, Base64.DEFAULT);
    //    final Resources resources = getContext().getResources();
    //    setImageSpan(builder, new BitmapDrawable(resources, BitmapFactory.decodeByteArray(binData, 0, binData.length)), start, builder.length());
    //  } catch (OutOfMemoryError | IllegalArgumentException ia) {
    //    //Out of memory or invalid Base64, ignore
    //  }
    //} else if (spine != null) {
    //  final String resolvedHref = spine.resolveHref(src);
    //  final BookView.StreamingResourceCallback callback = new BookView.StreamingResourceCallback(builder, start, builder.length());
    //  resourcesLoader.registerImageCallback(resolvedHref, callback);
    //}
  }

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