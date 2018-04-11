package net.nightwhistler.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.TextUtil;
import net.nightwhistler.htmlspanner.spans.FontFamilySpan;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

/**
 * Handles pre tags, setting the style to monospace and preserving the
 * formatting.
 *
 * @author Alex Kuiper
 *
 */
public class PreHandler extends TagNodeHandler {

  private void getPlainText(StringBuffer buffer, Object node) {
    if (node instanceof ContentNode) {

      ContentNode contentNode = (ContentNode) node;
      String text = TextUtil.replaceHtmlEntities(contentNode.getContent().toString(), true);

      buffer.append(text);

    } else if (node instanceof TagNode) {
      TagNode tagNode = (TagNode) node;
      for (Object child : tagNode.getAllChildren()) {
        getPlainText(buffer, child);
      }
    }
  }

  @Override
  public boolean rendersContent() {
    return true;
  }

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder,
      int start, int end, SpanStack spanStack) {

    StringBuffer buffer = new StringBuffer();
    getPlainText(buffer, node);

    builder.append(buffer.toString());

    FontFamily monoSpace = getSpanner().getFontResolver().getMonoSpaceFont();
    spanStack.pushSpan(new FontFamilySpan(monoSpace), start, builder.length());
    appendNewLine(builder);
    appendNewLine(builder);
  }

}