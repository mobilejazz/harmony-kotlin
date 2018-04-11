package net.nightwhistler.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import android.util.Log;
import com.osbcp.cssparser.CSSParser;
import com.osbcp.cssparser.Rule;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.css.CSSCompiler;
import net.nightwhistler.htmlspanner.css.CompiledRule;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

/**
 * TagNodeHandler that reads <style> blocks and parses the CSS rules within.
 */
public class StyleNodeHandler extends TagNodeHandler {

  @Override
  public boolean rendersContent() {
    return true;
  }

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
    if (getSpanner().isAllowStyling()) {
      if (node.getAllChildren().size() == 1) {
        final Object childNode = node.getAllChildren().get(0);
        if (childNode instanceof ContentNode) {
          parseCSSFromText(((ContentNode) childNode).getContent(), spanStack);
        }
      }
    }
  }

  private void parseCSSFromText(String text, SpanStack spanStack) {
    try {
      for (Rule rule : CSSParser.parse(text)) {
        final CompiledRule compiledRule = CSSCompiler.compile(rule, getSpanner());
        spanStack.registerCompiledRule(compiledRule);
      }
    } catch (Exception e) {
      Log.e("StyleNodeHandler", "Unparseable CSS definition", e);
    }
  }
}
