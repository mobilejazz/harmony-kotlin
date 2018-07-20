package net.nightwhistler.pageturner.view.bookview;

import android.graphics.drawable.Drawable;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

/**
 * Listener interface for updates from a BookView.
 */
public interface BookViewListener {

  /**
   * Called after the Bookview has successfully parsed the book.
   */
  void onBookParsed(Book book);

  /**
   * Event indicating text rendering has started
   */
  void onStartRenderingText();

  /**
   * Called if the book could not be opened for some reason.
   */
  void onErrorOnBookOpening();

  /**
   * Called when the BookView starts parsing a new entry
   * of the book. Usually after a pageUp or pageDown event.
   */
  void onParseEntryStart(int entry);

  /**
   * Called after parsing is complete.
   */
  void onParseEntryComplete(Resource resource);

  /**
   * Indicates how far we've progressed in the book
   **/
  void onProgressUpdate(int progressPercentage);

  /**
   * Generated when the user from right to left.
   *
   * @return true if the event was handled.
   */
  boolean onSwipeLeft();

  /**
   * Generated when the user swipes from left to right.
   *
   * @return true if the event was handled.
   */
  boolean onSwipeRight();

  /**
   * Generated when the user taps left edge of the screen.
   *
   * @return true if the event was handled.
   */
  boolean onTapLeftEdge();

  /**
   * Generated when the user taps the right edge of the screen.
   *
   * @return true if the event was handled.
   */
  boolean onTapRightEdge();

  /**
   * Called before any sliding the detection is performed.
   *
   * @return true if the event is handled and consumed, otherwise false.
   */
  boolean onPreSlide();

  /**
   * Generated when the user slides a finger along the screen's left edge.
   *
   * @param value how far the user has slid.
   */
  boolean onLeftEdgeSlide(int value);

  /**
   * Generated when the user slides a finger along the screen's right edge.
   *
   * @param value how far the user has slid.
   */
  boolean onRightEdgeSlide(int value);

  /**
   * Called when the user touches the screen and before any detection of the event has been
   * performed.
   * <p>
   * This will always be called when the user taps the screen in every situation.
   * <p>
   * Returns true if the event has been captured and does not want to process further event
   * detection. False otherwise.
   */
  boolean onPreScreenTap();

  /**
   * Called when the user touches the screen.
   * <p>
   * This will always be called when the user taps the screen, even
   * when an edge is tapped.
   */
  void onScreenTap();

  /**
   * Called when the user navigates one page in the book.
   */
  void onPageDown();

  /**
   * Called when the user navigates to the second page in the book
   */
  void onPageDownFirstPage();

  /**
   * Called when the user reaches the last page of the book and when tries to scroll page to the
   * next page.
   */
  void onLastScreenPageDown();

  /**
   * Called when the user clicks on an image.
   */
  void onBookImageClicked(final Drawable drawable, String data);
}
