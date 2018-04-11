package net.nightwhistler.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.spans.ListItemSpan;
import org.htmlcleaner.TagNode;

/**
 * Handles items in both numbered and unordered lists.
 *
 * @author Alex Kuiper
 *
 */
public class ListItemHandler extends TagNodeHandler {

  private int getMyIndex(TagNode node) {
    if (node.getParent() == null) {
      return -1;
    }

    int i = 1;

    for (Object child : node.getParent().getAllChildren()) {
      if (child == node) {
        return i;
      }

      if (child instanceof TagNode) {
        TagNode childNode = (TagNode) child;
        if ("li".equals(childNode.getName())) {
          i++;
        }
      }
    }

    return -1;
  }

  private String getParentName(TagNode node) {
    if (node.getParent() == null) {
      return null;
    }

    return node.getParent().getName();
  }

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder,
      int start, int end, SpanStack spanStack) {

    if (builder.length() > 0
        && builder.charAt(builder.length() - 1) != '\n') {
      builder.append("\n");
    }

    if ("ol".equals(getParentName(node))) {
      ListItemSpan bSpan = new ListItemSpan(getMyIndex(node));
      spanStack.pushSpan(bSpan, start, end);
    } else if ("ul".equals(getParentName(node))) {
      // Unicode bullet character.
      ListItemSpan bSpan = new ListItemSpan();
      spanStack.pushSpan(bSpan, start, end);
    }

  }
}