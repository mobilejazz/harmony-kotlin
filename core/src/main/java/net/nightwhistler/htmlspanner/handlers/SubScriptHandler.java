package net.nightwhistler.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import android.text.style.SubscriptSpan;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import org.htmlcleaner.TagNode;

/**
 * Applies subscript style.
 *
 * @author Alex Kuiper
 *
 */
public class SubScriptHandler extends TagNodeHandler {

  public void handleTagNode(TagNode node, SpannableStringBuilder builder,
      int start, int end, SpanStack spanStack) {

    spanStack.pushSpan(new SubscriptSpan(), start, end);
  }
}
