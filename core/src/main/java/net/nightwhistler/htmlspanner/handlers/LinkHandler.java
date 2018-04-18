package net.nightwhistler.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import net.nightwhistler.htmlspanner.SpanStack;
import org.htmlcleaner.TagNode;

/**
 * Creates clickable links.
 *
 * @author Alex Kuiper
 *
 */
public class LinkHandler extends TagNodeHandler {

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
    final String href = node.getAttributeByName("href");
    spanStack.pushSpan(new URLSpan(href), start, end);
  }
}