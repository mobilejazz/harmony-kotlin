package net.nightwhistler.htmlspanner.handlers.attributes;

import android.text.SpannableStringBuilder;
import android.util.Log;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.handlers.StyledTextHandler;
import net.nightwhistler.htmlspanner.spans.BorderSpan;
import net.nightwhistler.htmlspanner.style.Style;
import org.htmlcleaner.TagNode;

public class BorderAttributeHandler extends WrappingStyleHandler {

  public BorderAttributeHandler(StyledTextHandler handler) {
    super(handler);
  }

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, Style useStyle, SpanStack spanStack) {
    if (node.getAttributeByName("border") != null) {
      Log.d("BorderAttributeHandler", "Adding BorderSpan from " + start + " to " + end);
      spanStack.pushSpan(new BorderSpan(useStyle, start, end, getSpanner().isUseColoursFromStyle()), start, end);
    }

    super.handleTagNode(node, builder, start, end, useStyle, spanStack);
  }

}
