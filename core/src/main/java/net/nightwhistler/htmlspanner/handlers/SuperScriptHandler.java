package net.nightwhistler.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import android.text.style.SuperscriptSpan;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import org.htmlcleaner.TagNode;

/**
 * Applies superscript.
 *
 * @author Alex Kuiper
 *
 */
public class SuperScriptHandler extends TagNodeHandler {

  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
    spanStack.pushSpan(new SuperscriptSpan(), start, end);
  }

}
