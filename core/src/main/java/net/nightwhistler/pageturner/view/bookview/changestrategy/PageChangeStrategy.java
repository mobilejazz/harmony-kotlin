package net.nightwhistler.pageturner.view.bookview.changestrategy;

import android.text.Spanned;
import jedi.option.Option;
import net.nightwhistler.pageturner.view.bookview.BookView;

public interface PageChangeStrategy {

  /**
   * Loads the given section of text.
   * <p>
   * This will be a whole "file" from an epub.
   */
  void loadText(Spanned text);

  /**
   * Returns the text-offset of the top-left character on the screen.
   */
  int getTopLeftPosition();

  /**
   * Gets the current reading progress in the chapter.
   */
  int getProgressPosition();

  /**
   * Returns if we're at the start of the current section
   */
  boolean isAtStart();

  /**
   * Returns if we're at the end of the current section
   */
  boolean isAtEnd();

  /**
   * Tells this strategy to move the window so the specified
   * position ends up on the top line of the windows.
   */
  void setPosition(int pos);

  /**
   * Sets a position relative to the text length:
   * 0 means the start of the text, 1 means the end of
   * the text.
   *
   * @param position a value between 0 and 1
   */
  void setRelativePosition(double position);

  /**
   * Move the view one page up.
   */
  void pageUp();

  /**
   * Move the view one page down.
   */
  void pageDown();

  /**
   * Simple way to differentiate without instanceof
   **/
  boolean isScrolling();

  /**
   * Clears all text held in this strategy's buffer.
   */
  void clearText();

  /**
   * Clears the stored position in this strategy.
   */
  void clearStoredPosition();

  /**
   * Updates all fields to reflect a new configuration.
   */
  void updatePosition();

  /**
   * Clears both the buffer and stored position.
   */
  void reset();

  /**
   * Gets the text held in this strategy's buffer.
   *
   * @return the text
   */
  Option<Spanned> getText();

  void setBookView(BookView bookView);

  int getSizeChartDisplayed();

}
