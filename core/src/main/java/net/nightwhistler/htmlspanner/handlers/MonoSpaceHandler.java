package net.nightwhistler.htmlspanner.handlers;

import net.nightwhistler.htmlspanner.style.Style;

/**
 * Sets monotype font.
 *
 * @author Alex Kuiper
 *
 */
public class MonoSpaceHandler extends StyledTextHandler {

  @Override
  public Style getStyle() {
    return new Style().setFontFamily(
        getSpanner().getFontResolver().getMonoSpaceFont());
  }
}
