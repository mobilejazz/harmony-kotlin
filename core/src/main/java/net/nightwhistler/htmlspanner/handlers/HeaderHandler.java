package net.nightwhistler.htmlspanner.handlers;

import net.nightwhistler.htmlspanner.style.Style;
import net.nightwhistler.htmlspanner.style.StyleValue;

/**
 * Handles Headers, by assigning a relative text-size.
 *
 * Note that which header is handled (h1, h2, etc) is determined by the tag this
 * handler is registered for.
 *
 * Example:
 *
 * spanner.registerHandler("h1", new HeaderHandler(1.5f));
 * spanner.registerHandler("h2", new HeaderHandler(1.4f));
 *
 * @author Alex Kuiper
 *
 */
public class HeaderHandler extends StyledTextHandler {

  private final StyleValue size;
  private final StyleValue margin;

  /**
   * Creates a HeaderHandler which gives
   *
   * @param size
   */
  public HeaderHandler(float size, float margin) {
    this.size = new StyleValue(size, StyleValue.Unit.EM);
    this.margin = new StyleValue(margin, StyleValue.Unit.EM);
  }

  @Override
  public Style getStyle() {
    return super.getStyle().setFontSize(size)
        .setFontWeight(Style.FontWeight.BOLD)
        .setDisplayStyle(Style.DisplayStyle.BLOCK)
        .setMarginBottom(margin)
        .setMarginTop(margin);
  }

}
