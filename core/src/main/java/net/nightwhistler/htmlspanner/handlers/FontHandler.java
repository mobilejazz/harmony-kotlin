package net.nightwhistler.htmlspanner.handlers;

import android.text.SpannableStringBuilder;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.css.CSSCompiler;
import net.nightwhistler.htmlspanner.style.Style;
import org.htmlcleaner.TagNode;

/**
 * Handler for font-tags
 */
public class FontHandler extends StyledTextHandler {

  public FontHandler() {
    super(new Style());
  }

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, Style style, SpanStack spanStack) {
    if (getSpanner().isCSSStylingAllowed()) {
      final String face = node.getAttributeByName("face");
      final String size = node.getAttributeByName("size");
      final String color = node.getAttributeByName("color");

      final FontFamily family = getSpanner().getFont(face);
      style = style.setFontFamily(family);

      if (size != null) {
        final CSSCompiler.StyleUpdater updater = CSSCompiler.getStyleUpdater("font-size", size);
        if (updater != null) {
          style = updater.updateStyle(style, getSpanner());
        }
      }

      if (color != null && getSpanner().isUseColoursFromStyle()) {
        final CSSCompiler.StyleUpdater updater = CSSCompiler.getStyleUpdater("color", color);
        if (updater != null) {
          style = updater.updateStyle(style, getSpanner());
        }
      }
    }

    super.handleTagNode(node, builder, start, end, style, spanStack);
  }

}
