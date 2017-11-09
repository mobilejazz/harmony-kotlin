/*
 * Copyright (C) 2011 Alex Kuiper
 * 
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.R;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TOCReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.dto.TocEntry;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.TaskQueue;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.changestrategy.FixedPagesStrategy;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.changestrategy.PageChangeStrategy;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.CSSLinkHandler;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler.LinkTagHandler;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ImageResourceCallback;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.span.ClickableImageSpan;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks.OpenFileEpubBookTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks.PreLoadNextResourceTask;
import jedi.functional.Command;
import jedi.functional.Command0;
import jedi.functional.Filter;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.SystemFontResolver;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.handlers.TableHandler;
import net.nightwhistler.htmlspanner.spans.CenterSpan;
import org.htmlcleaner.TagNode;
import org.javatuples.Pair;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;

import static java.util.Arrays.*;
import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.*;

public class BookView extends ScrollView implements TextSelectionActions.SelectedTextProvider {

  private static final String TAG = BookView.class.getSimpleName();

  private final Handler scrollHandler = new Handler(Looper.getMainLooper());

  private int storedIndex;
  private String storedAnchor;

  private BookTextView childView;

  private BookMetadata bookMetadata;
  private Book book;
  private String fileName;
  private PageTurnerSpine spine;
  private PageChangeStrategy strategy;

  private int prevIndex = -1;
  private int prevPos = -1;

  private int horizontalMargin = 0;
  private int verticalMargin = 0;
  private int lineSpacing = 0;

  private Configuration configuration;

  private TextLoader textLoader;
  private ResourcesLoader resourcesLoader;
  private TaskQueue taskQueue;

  private StreamingBookRepository repository;

  private BookViewListener listener;

  private Logger logger;

  private final Drawable.Callback callback = new SimpleDrawableCallback() {
    @Override public void invalidateDrawable(@NonNull final Drawable who) {
      childView.setShadowLayer(0, 0, 0, 0); // this will trigger an invalidation of the text
      childView.setVisibility(View.VISIBLE);
    }
  };

  public BookView(Context context, AttributeSet attributes) {
    super(context, attributes);
  }

  public void init(BookMetadata bookMetadata, ResourcesLoader resourcesLoader, TextLoader textLoader, StreamingBookRepository repository, Logger logger) {
    final Context context = getContext();

    this.resourcesLoader = resourcesLoader;
    this.textLoader = textLoader;
    this.configuration = new Configuration(context);
    this.taskQueue = new TaskQueue();

    this.childView = (BookTextView) this.findViewById(R.id.bookview_inner);
    this.childView.setBookView(this);
    this.childView.setMovementMethod(new BookViewMovementMethod());

    this.setVerticalFadingEdgeEnabled(false);
    this.setSmoothScrollingEnabled(false);

    this.textLoader.registerTagNodeHandler("table", new TableHandler());

    final StreamingImageTagHandler imgHandler = new StreamingImageTagHandler();
    this.textLoader.registerTagNodeHandler("img", imgHandler);
    this.textLoader.registerTagNodeHandler("image", imgHandler);
    this.textLoader.setLinkTagCallBack(new LinkTagHandler.LinkTagCallBack() {
      @Override public void onLinkClicked(String href) {
        navigateTo(spine.resolveHref(href));
      }
    });

    this.bookMetadata = bookMetadata;
    this.repository = repository;

    this.logger = logger;
  }

  void onInnerViewResize() {
    restorePosition();
    final TableHandler tableNodeHandler = textLoader.getHtmlTagHandler("table");
    if (tableNodeHandler != null) {
      final int tableWidth = (int) (childView.getWidth() * 0.9);
      tableNodeHandler.setTableWidth(tableWidth);
    }
  }

  public int getSpineSize() {
    return spine != null ? spine.size() : 0;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public PageChangeStrategy getStrategy() {
    return this.strategy;
  }

  public boolean isAtStart() {
    return spine == null || spine.getPosition() == 0 && strategy.isAtStart();
  }

  public boolean isAtEnd() {
    return spine != null && spine.getPosition() >= spine.size() - 1 && strategy.isAtEnd();
  }

  @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    progressUpdate();
  }

  public List<ClickableSpan> getLinkAt(float x, float y) {
    return getSpansAt(x, y, ClickableSpan.class);
  }

  /**
   * Returns all the spans of a specific class at a specific location.
   *
   * @param x the X coordinate
   * @param y the Y coordinate
   * @param spanClass the class of span to filter for
   *
   * @return a List of spans of type A, may be empty.
   */
  private <A> List<A> getSpansAt(float x, float y, Class<A> spanClass) {
    final Option<Integer> offsetOption = findOffsetForPosition(x, y);
    final CharSequence text = childView.getText();

    if (isEmpty(offsetOption) || !(text instanceof Spanned)) {
      return Collections.emptyList();
    }

    final int offset = offsetOption.getOrElse(0);

    return asList(((Spanned) text).getSpans(offset, offset, spanClass));
  }

  public void blockInnerViewFor(long time) {
    this.childView.setBlockUntil(System.currentTimeMillis() + time);
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    if (strategy.isScrolling()) {
      return super.onTouchEvent(ev);
    } else {
      return childView.onTouchEvent(ev);
    }
  }

  @Override public void fling(int velocityY) {
    strategy.clearStoredPosition();
    super.fling(velocityY);
  }

  @Override public void setOnTouchListener(OnTouchListener l) {
    super.setOnTouchListener(l);
    this.childView.setOnTouchListener(l);
  }

  public void setTextSelectionCallback(TextSelectionCallback callback, ActionModeListener listener) {
    this.childView.setCustomSelectionActionModeCallback(new TextSelectionActions(listener, callback, this));
  }

  public int getLineSpacing() {
    return lineSpacing;
  }

  public void setLineSpacing(int lineSpacing) {
    if (lineSpacing != this.lineSpacing) {
      this.lineSpacing = lineSpacing;
      this.childView.setLineSpacing(lineSpacing, 1);

      if (strategy != null) {
        strategy.updatePosition();
      }
    }
  }

  public void setHorizontalMargin(int horizontalMargin) {
    if (horizontalMargin != this.horizontalMargin) {
      this.horizontalMargin = horizontalMargin;
      setPadding(this.horizontalMargin, this.verticalMargin, this.horizontalMargin, this.verticalMargin);
      if (strategy != null) {
        strategy.updatePosition();
      }
    }
  }

  public void releaseResources() {
    this.strategy.clearText();
    this.textLoader.closeCurrentBook();
    this.taskQueue.clear();
  }

  public void setLinkColor(int color) {
    this.childView.setLinkTextColor(color);
  }

  public int getVerticalMargin() {
    return verticalMargin;
  }

  public void setVerticalMargin(int verticalMargin) {
    if (verticalMargin != this.verticalMargin) {
      this.verticalMargin = verticalMargin;
      setPadding(this.horizontalMargin, this.verticalMargin, this.horizontalMargin, this.verticalMargin);
      if (strategy != null) {
        strategy.updatePosition();
      }
    }
  }

  public Option<String> getSelectedText() {
    final int start = getSelectionStart();
    final int end = getSelectionEnd();

    if (start > 0 && end > 0 && end > start) {
      final String text = childView.getText().subSequence(start, end).toString();
      return some(text);
    } else {
      return none();
    }
  }

  public int getSelectionStart() {
    return childView.getSelectionStart();
  }

  public int getSelectionEnd() {
    return childView.getSelectionEnd();
  }

  public void clear() {
    this.childView.setText("");
    this.storedAnchor = null;
    this.storedIndex = -1;
    this.book = null;
    this.fileName = null;
    this.strategy.reset();
  }

  /**
   * Loads the text and saves the restored position.
   */
  public void restore() {
    strategy.clearText();
    loadText();
  }

  public void loadText() {
    if (spine == null) {
      final Context context = getContext();

      final OpenFileEpubBookTask task = new OpenFileEpubBookTask(context, textLoader, resourcesLoader, storedIndex, logger);
      task.setOnCompletedCallback(new QueueableAsyncTask.QueueCallback() {
        @Override public void onTaskCompleted(QueueableAsyncTask<?, ?, ?> task, boolean canceled, Option<?> result) {
          result.match(new Command<Object>() {
            @Override public void execute(Object value) {
              @SuppressWarnings("unchecked") final Pair<Book, PageTurnerSpine> result = (Pair<Book, PageTurnerSpine>) value;
              final Book book = result.getValue0();
              final PageTurnerSpine pageTurnerSpine = result.getValue1();

              BookView.this.book = book;
              BookView.this.spine = pageTurnerSpine;

              notifyOnBookOpened(book);

              // Once initialization is done, let's proceed to load the text properly
              taskQueue.executeTask(new LoadStreamingTextTask());
            }
          }, new Command0() {
            @Override public void execute() {
              errorOnBookOpening();
            }
          });
        }
      });

      //final OpenStreamingBookTask task = new OpenStreamingBookTask(textLoader, resourcesLoader, storedIndex, contentOpf, tocResourcePath, logger);
      //task.setOnCompletedCallback(new QueueableAsyncTask.QueueCallback() {
      //  @Override public void onTaskCompleted(QueueableAsyncTask<?, ?, ?> task, boolean canceled, Option<?> result) {
      //    result.match(new Command<Object>() {
      //      @Override public void execute(Object value) {
      //        @SuppressWarnings("unchecked") final Pair<Book, PageTurnerSpine> result = (Pair<Book, PageTurnerSpine>) value;
      //        final Book book = result.getValue0();
      //        final PageTurnerSpine pageTurnerSpine = result.getValue1();
      //
      //        BookView.this.book = book;
      //        BookView.this.spine = pageTurnerSpine;
      //
      //        notifyOnBookOpened(book);
      //
      //        // Once initialization is done, let's proceed to load the text properly
      //        taskQueue.executeTask(new LoadStreamingTextTask());
      //      }
      //    }, new Command0() {
      //      @Override public void execute() {
      //        errorOnBookOpening();
      //      }
      //    });
      //  }
      //});

      taskQueue.executeTask(task);
    } else {
      spine.getCurrentResource().forEach(new Command<Resource>() {
        @Override public void execute(Resource resource) {
          loadText(resource);
        }
      });
    }
  }

  private void loadText(Resource resource) {
    Log.d(TAG, "Trying to load text for resource " + resource);

    final Option<Spannable> cachedText = textLoader.getCachedTextForResource(resource);

    //Start by clearing the queue
    taskQueue.clear();

    if (!isEmpty(cachedText) && getInnerView().getWidth() > 0) {
      Log.d(TAG, "Text is cached, loading on UI Thread.");
      loadText(cachedText.unsafeGet());
    } else {
      Log.d(TAG, "Text is NOT cached, loading in background.");
      taskQueue.executeTask(new LoadStreamingTextTask(), resource);
    }

    if (needsPageNumberCalculation()) {
      taskQueue.executeTask(new CalculatePageNumbersTask());
    }
  }

  private void loadText(Spanned text) {
    strategy.loadText(text);
    restorePosition();
    strategy.updateGUI();
    progressUpdate();
    final Resource resource = spine.getCurrentResource().unsafeGet();
    parseEntryComplete(resource);
  }

  public void setFontFamily(FontFamily family) {
    this.childView.setTypeface(family.getDefaultTypeface());
    final TableHandler tableNodeHandler = textLoader.getHtmlTagHandler("table");
    if (tableNodeHandler != null) {
      tableNodeHandler.setTypeFace(family.getDefaultTypeface());
    }
  }

  public void pageDown() {
    boolean isAtStart = isAtStart();
    if (isAtStart) {
      notifyListenersPageDownFirstPageEvent();
    }

    strategy.pageDown();
    progressUpdate();
    notifyListenersPageDownEvent();
  }

  public void lastPageDown() {
    notifyListenersLastPageDownEvent();
  }

  public void pageUp() {
    strategy.pageUp();
    progressUpdate();
  }

  private void notifyListenersPageDownFirstPageEvent() {
    if (listener != null) {
      listener.onPageDownFirstPage();
    }
  }

  private void notifyListenersPageDownEvent() {
    if (listener != null) {
      listener.onPageDown();
    }
  }

  private void notifyListenersLastPageDownEvent() {
    if (listener != null) {
      listener.onLastScreenPageDown();
    }
  }

  public TextView getInnerView() {
    return childView;
  }

  public PageTurnerSpine getSpine() {
    return this.spine;
  }

  private Option<Integer> findOffsetForPosition(float x, float y) {
    if (childView == null || childView.getLayout() == null) {
      return none();
    }

    final Layout layout = this.childView.getLayout();
    final int line = layout.getLineForVertical((int) y);
    final int horizontalOffset = layout.getOffsetForHorizontal(line, x);

    return option(horizontalOffset);
  }

  public void navigateTo(TocEntry tocEntry) {
    navigateTo(tocEntry.getHref());
  }

  public void navigateTo(String rawHref) {
    this.prevIndex = this.getIndex();
    this.prevPos = this.getProgressPosition();

    // Default Charset for android is UTF-8
    // http://developer.android.com/reference/java/nio/charset/Charset.html#defaultCharset()
    String charsetName = Charset.defaultCharset().name();

    if (!Charset.isSupported(charsetName)) {
      Log.w(TAG, "{} is not a supported Charset. Will fall back to UTF-8: " + charsetName);
      charsetName = "UTF-8";
    }

    // URLDecode the href, so it does not contain %20 etc.
    String href;
    try {
      href = URLDecoder.decode(StringUtil.substringBefore(rawHref, Constants.FRAGMENT_SEPARATOR_CHAR), charsetName);
    } catch (UnsupportedEncodingException e) {
      // Won't ever be reached
      throw new AssertionError(e);
    }

    // Don't decode the anchor.
    String anchor = StringUtil.substringAfterLast(rawHref, Constants.FRAGMENT_SEPARATOR_CHAR);

    if (!"".equals(anchor)) {
      this.storedAnchor = anchor;
    }

    // Just an anchor and no href; resolve it on this page
    if (href.length() == 0) {
      restorePosition();
    } else {
      this.strategy.clearText();
      this.strategy.setPosition(0);

      if (this.spine.navigateByHref(href)) {
        loadText();
      } else {
        final Resource resource = book.getResources().getByHref(href);
        if (resource != null) {
          loadText(resource);
        }
      }
    }
  }

  // This percentage value is assuming that is ranging from 0 to 100
  public void navigateToPercentageInChapter(int percentage) {
    if (spine == null) {
      return;
    }

    // We navigate to next chapter
    if (percentage >= 100) {
      Resource resource = this.spine.getNextResource().unsafeGet();
      if (resource != null) {
        navigateTo(resource.getHref());
      }
      return;
    }

    // First we check if we have a size for a resource
    Long relativeSizeForChapter = this.spine.getSizeForCurrentResource();

    // If is null, do nothing
    if (relativeSizeForChapter == null) {
      return;
    }

    double progressPercentage = (double) percentage / 100;

    this.strategy.setRelativePosition(progressPercentage);

    doNavigation(getIndex());
  }

  private void doNavigation(int index) {
    // Check if we're already in the right part of the book
    if (index == this.getIndex()) {
      restorePosition();
      progressUpdate();
      return;
    }

    this.prevIndex = this.getIndex();

    this.storedIndex = index;
    this.strategy.clearText();
    this.spine.navigateByIndex(index);

    loadText();
  }

  public void navigateTo(int index, int position) {
    this.prevPos = this.getProgressPosition();
    this.strategy.setPosition(position);

    doNavigation(index);
  }

  public Option<List<TocEntry>> getTableOfContents() {
    if (book != null) {
      final List<TocEntry> result = new ArrayList<>();
      flatten(book.getTableOfContents().getTocReferences(), result, 0);
      return some(result);
    } else {
      return none();
    }
  }

  private void flatten(List<TOCReference> refs, List<TocEntry> entries, int level) {
    if (spine == null || refs == null || refs.isEmpty()) {
      return;
    }

    for (TOCReference ref : refs) {

      StringBuilder title = new StringBuilder();

      for (int i = 0; i < level; i++) {
        title.append("  ");
      }

      title.append(ref.getTitle());

      if (ref.getResource() != null) {
        entries.add(new TocEntry(title.toString(), spine.resolveTocHref(ref.getCompleteHref())));
      }

      flatten(ref.getChildren(), entries, level + 1);
    }
  }

  public int getIndex() {
    if (this.spine == null) {
      return storedIndex;
    }

    return this.spine.getPosition();
  }

  public void setIndex(int index) {
    this.storedIndex = index;
  }

  public int getStartOfCurrentPage() {
    return strategy.getTopLeftPosition();
  }

  public int getProgressPosition() {
    return strategy.getProgressPosition();
  }

  public void setPosition(int pos) {
    this.strategy.setPosition(pos);
  }

  /**
   * Scrolls to a previously stored point.
   * <p>
   * Call this after setPosition() to actually go there.
   */
  private void restorePosition() {
    if (this.storedAnchor != null) {
      spine.getCurrentHref().forEach(new Command<String>() {
        @Override public void execute(String href) {
          final Option<Integer> anchorValue = textLoader.getAnchor(href, storedAnchor);
          if (!isEmpty(anchorValue)) {
            strategy.setPosition(anchorValue.getOrElse(0));
            storedAnchor = null;
          }
        }
      });
    }
    strategy.updatePosition();
  }

  private void setImageSpan(final SpannableStringBuilder builder, final Drawable drawable, final int start, final int end) {
    drawable.setCallback(callback);
    final ClickableImageSpan imageSpan = new ClickableImageSpan(drawable);
    imageSpan.setOnClickListener(new ClickableImageSpan.ClickableImageSpanListener() {
      @Override public void onImageClick(final View v, final Drawable drawable) {
        if (listener != null) {
          listener.onBookImageClicked(drawable);
        }
      }
    });
    builder.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    builder.setSpan(new CenterSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  private class StreamingImageTagHandler extends TagNodeHandler {

    public StreamingImageTagHandler() {
    }

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack span) {
      final String data = obtainImageAttribute(node);

      // Ignore invalid images
      if (data == null) {
        return;
      }

      // Register callback with retrieved data
      // TODO: Notify to this view that the Drawable has been created
      final ImageResourceCallback callback = new ImageResourceCallback.Builder()
          .withMetadata(bookMetadata)
          .withData(data)
          .withSpine(spine)
          .withStart(start)
          .withEnd(builder.length())
          .withHeight(getHeight())
          .withWidth(getWidth())
          .withVerticalMargin(verticalMargin)
          .withHorizontal(horizontalMargin)
          .withLogger(logger)
          .create();
      resourcesLoader.registerImageCallback(callback);

      //if (src.startsWith("data:image")) {
      //  try {
      //    final String dataString = src.substring(src.indexOf(',') + 1);
      //    final byte[] binData = Base64.decode(dataString, Base64.DEFAULT);
      //    final Resources resources = getContext().getResources();
      //    setImageSpan(builder, new BitmapDrawable(resources, BitmapFactory.decodeByteArray(binData, 0, binData.length)), start, builder.length());
      //  } catch (OutOfMemoryError | IllegalArgumentException ia) {
      //    //Out of memory or invalid Base64, ignore
      //  }
      //} else if (spine != null) {
      //  final String resolvedHref = spine.resolveHref(src);
      //  final ImageResourceCallback callback =
      //      new ImageResourceCallback(bookMetadata, repository, builder, start, builder.length(), getWidth(), getHeight(), verticalMargin, horizontalMargin, logger) {
      //        @Override protected void onFastBitmapDrawableCreated(Drawable drawable, SpannableStringBuilder builder, int start, int end) {
      //          drawable.setCallback(BookView.this.callback);
      //          setImageSpan(builder, drawable, start, end);
      //        }
      //      };
      //  resourcesLoader.registerImageCallback(callback);
      //}
    }

    private String obtainImageAttribute(TagNode node) {
      String src = node.getAttributeByName("src");

      if (src == null) {
        src = node.getAttributeByName("href");
      }

      if (src == null) {
        src = node.getAttributeByName("xlink:href");
      }

      return src;
    }
  }

  public Book getBook() {
    return book;
  }

  public void setTextColor(int color) {
    if (this.childView != null) {
      this.childView.setTextColor(color);
    }

    final TableHandler tableNodeHandler = textLoader.getHtmlTagHandler("table");
    if (tableNodeHandler != null) {
      tableNodeHandler.setTextColor(color);
    }
  }

  public void setTextSize(float textSize) {
    this.childView.setTextSize(textSize);

    final TableHandler tableNodeHandler = textLoader.getHtmlTagHandler("table");
    if (tableNodeHandler != null) {
      tableNodeHandler.setTextSize(textSize);
    }
  }

  public void setListener(BookViewListener listener) {
    this.listener = listener;
  }

  private void notifyOnBookOpened(Book book) {
    if (listener != null) {
      listener.onBookOpened(book);
    }
  }

  private void errorOnBookOpening() {
    if (listener != null) {
      listener.onErrorOnBookOpening();
    }
  }

  private void notifyOnParseEntryStart(int entry) {
    if (listener != null) {
      listener.onParseEntryStart(entry);
    }
  }

  private void parseEntryComplete(Resource resource) {
    if (listener != null) {
      listener.onParseEntryComplete(resource);
    }
  }

  @Override public void setBackgroundColor(int color) {
    super.setBackgroundColor(color);

    if (childView != null) {
      this.childView.setBackgroundColor(color);
    }
  }

  private void notifyOnStartRenderingText() {
    if (listener != null) {
      listener.onStartRenderingText();
    }
  }

  private void progressUpdate() {
    if (this.spine == null) {
      return;
    }

    this.strategy.getText().filter(new Filter<Spanned>() {
      @Override public Boolean execute(Spanned t) {
        return t.length() > 0;
      }
    }).forEach(new Command<Spanned>() {
      @Override public void execute(Spanned text) {
        final FixedPagesStrategy strategy = (FixedPagesStrategy) getStrategy();
        int pagesOffset = strategy.getPageOffsets().size();
        int currentPage = strategy.getCurrentPage();
        int progressPercentage = (int) Math.floor(((double) currentPage / pagesOffset) * 100);

        if (listener != null) {
          listener.onProgressUpdate(progressPercentage);
        }
      }
    });
  }

  public int getPagesForResource() {
    if (spine != null) {
      return ((FixedPagesStrategy) BookView.this.getStrategy()).getPageOffsets().size();
    }

    return -1;
  }

  public int getCurrentPage() {
    if (spine != null) {
      return ((FixedPagesStrategy) BookView.this.getStrategy()).getCurrentPage() + 1;
    }

    return -1;
  }

  private boolean needsPageNumberCalculation() {
    final Option<List<List<Integer>>> offsets = configuration.getPageOffsets(fileName);
    return isEmpty(offsets) || offsets.unsafeGet().size() == 0;
  }

  public void setEnableScrolling(boolean enableScrolling) {
    if (this.strategy == null || this.strategy.isScrolling() != enableScrolling) {
      int pos = -1;
      boolean wasNull = true;

      Spanned text = null;

      if (this.strategy != null) {
        pos = this.strategy.getTopLeftPosition();
        text = this.strategy.getText().unsafeGet();
        this.strategy.clearText();
        wasNull = false;
      }

      this.strategy = new FixedPagesStrategy();
      strategy.setBookView(this);

      if (!wasNull) {
        this.strategy.setPosition(pos);
      }

      if (text != null && text.length() > 0) {
        this.strategy.loadText(text);
      }
    }
  }

  private class LoadStreamingTextTask extends QueueableAsyncTask<Resource, Void, Spanned> {

    @Override protected void onPreExecute() {
      notifyOnParseEntryStart(getIndex());
      notifyOnStartRenderingText();
    }

    @Override public Option<Spanned> doInBackground(Resource... resources) {
      try {
        final Resource resource = resources != null && resources.length > 0 ? resources[0] : spine.getCurrentResource().getOrElse(new Resource(""));

        // Clear previous images references (if any)
        resourcesLoader.clearImageResources();

        final Spannable result = textLoader.getText(resource, new HtmlSpanner.CancellationCallback() {
          @Override public boolean isCancelled() {
            return LoadStreamingTextTask.this.isCancelled();
          }
        });

        // Load all image resources (not true anymore as AbstractFastBitmapDrawable is in charge to do it per image).
        resourcesLoader.onPrepareBitmapDrawables();

        //If the view isn't ready yet, wait a bit.
        while (getInnerView().getWidth() == 0) {
          Thread.sleep(100);
        }

        strategy.loadText(result);

        return option((Spanned) result);
      } catch (Exception | OutOfMemoryError io) {
        logger.sendIssue(TAG, "Exception loading streaming text with book. Current exception: " + Throwables.getStackTraceAsString(io));
      }

      return none();
    }

    @Override public void doOnPostExecute(Option<Spanned> result) {
      restorePosition();
      strategy.updateGUI();
      progressUpdate();

      if (spine != null) { // Spine should not be null (check properly with refactor)
        final Resource resource = spine.getCurrentResource().unsafeGet();
        parseEntryComplete(resource);
      }

      // This is a hack for scrolling not updating to the right position on Android 4+
      if (strategy.isScrolling()) {
        scrollHandler.postDelayed(new Runnable() {
          @Override public void run() {
            BookView.this.restorePosition();
          }
        }, 100);
      }

      taskQueue.executeTask(new PreLoadNextResourceTask(spine, resourcesLoader));
    }
  }

  private class CalculatePageNumbersTask extends QueueableAsyncTask<Object, Void, List<List<Integer>>> {

    @Override public Option<List<List<Integer>>> doInBackground(Object... params) {
      try {
        final Option<List<List<Integer>>> offsets = getOffsets();
        offsets.forEach(new Command<List<List<Integer>>>() {
          @Override public void execute(List<List<Integer>> o) {
            configuration.setPageOffsets(fileName, o);
          }
        });
        Log.d(TAG, "Calculated offsets: " + offsets);
        return offsets;
      } catch (OutOfMemoryError | Exception e) {
        logger.sendIssue(TAG, "Exception while trying to calculate page. Current exception: " + Throwables.getStackTraceAsString(e));
      }

      return none();
    }

    @Override public void doOnPostExecute(Option<List<List<Integer>>> result) {
      Log.d(TAG, "Page number calculation completed.");
      result.filter(new Filter<List<List<Integer>>>() {
        @Override public Boolean execute(List<List<Integer>> r) {
          return r.size() > 0;
        }
      }).forEach(new Command<List<List<Integer>>>() {
        @Override public void execute(List<List<Integer>> r) {
          progressUpdate();
        }
      });
    }

    // Loads the text offsets for the whole book, with minimal use of resources.
    private Option<List<List<Integer>>> getOffsets() throws IOException {
      final List<List<Integer>> result = new ArrayList<>();

      final HtmlSpanner mySpanner = new HtmlSpanner();
      mySpanner.setFontResolver(new SystemFontResolver());
      final TableHandler handler = new TableHandler();
      final int tableWidth = (int) (childView.getWidth() * 0.9);
      handler.setTableWidth(tableWidth);
      mySpanner.registerHandler("table", handler);
      mySpanner.registerHandler("link", new CSSLinkHandler(textLoader));

      final FixedPagesStrategy fixedPagesStrategy = new FixedPagesStrategy();
      fixedPagesStrategy.setBookView(BookView.this);

      final Resource currentResource = spine.getCurrentResource().unsafeGet();

      if (currentResource != null) {
        final Spannable cachedText = textLoader.getCachedTextForResource(currentResource).unsafeGet();
        if (cachedText != null) {
          result.add(fixedPagesStrategy.getPageOffsets(cachedText));
        }
      }

      return some(result);
    }
  }

}
