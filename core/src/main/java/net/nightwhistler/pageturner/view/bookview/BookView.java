package net.nightwhistler.pageturner.view.bookview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.R;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.wr.configuration.ReaderConfig;
import com.worldreader.reader.wr.helper.WasabiManager;
import com.worldreader.reader.wr.models.PageTurnerSpine;
import com.worldreader.reader.wr.models.TocEntry;
import jedi.functional.Command;
import jedi.functional.Command0;
import jedi.functional.Filter;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SystemFontResolver;
import net.nightwhistler.htmlspanner.handlers.TableHandler;
import net.nightwhistler.htmlspanner.spans.CenterSpan;
import net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import net.nightwhistler.pageturner.scheduling.TaskQueue;
import net.nightwhistler.pageturner.view.bookview.changestrategy.FixedPagesStrategy;
import net.nightwhistler.pageturner.view.bookview.helper.TocUtils;
import net.nightwhistler.pageturner.view.bookview.nodehandler.CSSLinkHandler;
import net.nightwhistler.pageturner.view.bookview.nodehandler.ImageTagHandler;
import net.nightwhistler.pageturner.view.bookview.nodehandler.LinkTagHandler;
import net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import net.nightwhistler.pageturner.view.bookview.span.ClickableImageSpan;
import net.nightwhistler.pageturner.view.bookview.tasks.PreLoadNextResourceTask;
import net.nightwhistler.pageturner.view.bookview.tasks.TasksFactory;
import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.util.StringUtil;
import org.javatuples.Pair;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;

import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.*;

public class BookView extends ScrollView implements TextSelectionActions.SelectedTextProvider {

  private static final String TAG = BookView.class.getSimpleName();

  private final Handler scrollHandler = new Handler(Looper.getMainLooper());
  private final TaskQueue taskQueue = new TaskQueue();

  private int storedIndex;
  private String storedAnchor;

  private BookTextView childView;

  private DICompanion di;
  private BookMetadata bookMetadata;
  private Book book;
  private PageTurnerSpine spine;
  private FixedPagesStrategy strategy;

  private int horizontalMargin = 0;
  private int verticalMargin = 0;
  private int lineSpacing = 0;

  private TextLoader textLoader;
  private ResourcesLoader resourcesLoader;

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

  public void init(DICompanion di, BookMetadata bookMetadata, ResourcesLoader resourcesLoader, TextLoader textLoader) {
    this.di = di;
    this.bookMetadata = bookMetadata;
    this.resourcesLoader = resourcesLoader;
    this.textLoader = textLoader;
    logger = di.logger;

    childView = findViewById(R.id.book_view_inner);
    childView.setBookView(this);
    childView.setMovementMethod(new BookViewMovementMethod());

    setVerticalFadingEdgeEnabled(false);
    setSmoothScrollingEnabled(false);

    textLoader.registerTagNodeHandler("table", new TableHandler());

    final ImageTagHandler imgHandler = new BookViewImageTagHandler(getContext(), bookMetadata, resourcesLoader, di, logger);
    textLoader.registerTagNodeHandler("img", imgHandler);
    textLoader.registerTagNodeHandler("image", imgHandler);

    textLoader.setLinkTagCallBack(new LinkTagHandler.LinkTagCallBack() {
      @Override public void onLinkClicked(String href) {
        navigateTo(spine.resolveHref(href));
      }
    });
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

  public FixedPagesStrategy getStrategy() {
    return this.strategy;
  }

  public PageTurnerSpine getSpinner() {
    return this.spine;
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

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  public void blockInnerViewFor(long time) {
    this.childView.setBlockUntil(System.currentTimeMillis() + time);
  }

  @Override public boolean onTouchEvent(MotionEvent ev) {
    return strategy.isScrolling() ? super.onTouchEvent(ev) : childView.onTouchEvent(ev);
  }

  @Override public void fling(int velocityY) {
    strategy.clearStoredPosition();
    super.fling(velocityY);
  }

  @Override public void setOnTouchListener(OnTouchListener l) {
    super.setOnTouchListener(l);
    this.childView.setOnTouchListener(l);
  }

  public void setTextSelectionCallback(TextSelectionCallback callback, ActionModeListener listener, ReaderConfig c) {
    this.childView.setCustomSelectionActionModeCallback(new TextSelectionActions(listener, callback, this, c));
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

  public void setHorizontalMargin(int margin) {
    if (margin != horizontalMargin) {
      horizontalMargin = margin;
      setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
      if (strategy != null) {
        strategy.updatePosition();
      }
    }
  }

  public void releaseResources() {
    strategy.clearText();
    textLoader.closeCurrentBook();
    taskQueue.clear();
  }

  public void setLinkColor(int color) {
    childView.setLinkTextColor(color);
  }

  public int getVerticalMargin() {
    return verticalMargin;
  }

  public void setVerticalMargin(int margin) {
    if (verticalMargin != margin) {
      verticalMargin = margin;
      setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
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
    this.strategy.reset();
    this.spine = null;
  }

  public void startLoadingText() {
    strategy.clearText();
    loadText();
  }

  public void loadText() {
    if (spine == null) {
      final Context c = getContext();
      final WasabiManager w = di.wasabiManager;
      final QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> task = TasksFactory.createOpenBookTask(c, w, bookMetadata, textLoader, storedIndex, logger);
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
              taskQueue.executeTask(new LoadTextTask());
            }
          }, new Command0() {
            @Override public void execute() {
              errorOnBookOpening();
            }
          });
        }
      });

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
    logger.d(TAG, "Trying to load text for resource " + resource);

    final Option<Spannable> cachedText = textLoader.getCachedTextForResource(resource);

    //Start by clearing the queue
    taskQueue.clear();

    if (!isEmpty(cachedText) && getInnerView().getWidth() > 0) {
      logger.d(TAG, "Text is cached, loading on UI Thread.");
      loadText(cachedText.unsafeGet());
    } else {
      logger.d(TAG, "Text is NOT cached, loading in background.");
      taskQueue.executeTask(new LoadTextTask(), resource);
    }

    if (needsPageNumberCalculation()) {
      taskQueue.executeTask(new CalculatePageNumbersTask());
    }
  }

  private void loadText(Spanned text) {
    strategy.loadText(text);
    restorePosition();
    strategy.updatePosition();
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
    if (isAtEnd()) {
      notifyListenersLastPageDownEvent();
      return;
    }

    if (isAtStart()) {
      notifyListenersPageDownFirstPageEvent();
    }

    strategy.pageDown();
    progressUpdate();
    notifyListenersPageDownEvent();
  }

  public void pageUp() {
    if (isAtStart()) {
      return;
    }

    strategy.pageUp();
    progressUpdate();
    deselectText();
  }

  // FIXME: Workaround to avoid text selection on some devices when swiping pages
  private void deselectText() {
    getInnerView().post(new Runnable() {
      @Override public void run() {
        getInnerView().clearFocus();
      }
    });
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

  public void navigateTo(TocEntry tocEntry) {
    navigateTo(tocEntry.getHref());
  }

  public void navigateTo(String rawHref) {
    // Default Charset for android is UTF-8
    // http://developer.android.com/reference/java/nio/charset/Charset.html#defaultCharset()
    String charsetName = Charset.defaultCharset().name();

    if (!Charset.isSupported(charsetName)) {
      logger.w(TAG, "{} is not a supported Charset. Will fall back to UTF-8: " + charsetName);
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
      storedAnchor = anchor;
    }

    // Just an anchor and no href; resolve it on this page
    if (href.length() == 0) {
      restorePosition();
    } else {
      strategy.clearText();
      strategy.setPosition(0);

      if (spine.navigateByHref(href)) {
        loadText();
      } else {
        final Resource resource = book.getResources().getByHref(href);
        if (resource != null) {
          loadText(resource);
          //AD-551: We got here because the resource is part of the TOC but not part of the spine. Let's think of "copyright" as example.
          //If we were in chapter 3 and we decided to go to the TOC->Copyright, when finishing reading the copyright we will go to chapter 4...I'm adding the
          // following line so we go back to where wr where before.
          // IDEALLY, we should go to the following element of the TOC after finishing reading the copyright.
          spine.navigateBack();
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
      final Resource resource = spine.getNextResource().unsafeGet();
      if (resource != null) {
        navigateTo(resource.getHref());
      }
      return;
    }

    // First we check if we have a size for a resource
    final Long relativeSizeForChapter = spine.getSizeForCurrentResource();

    // If is null, do nothing
    if (relativeSizeForChapter == null) {
      return;
    }

    double progressPercentage = (double) percentage / 100;
    strategy.setRelativePosition(progressPercentage);
    doNavigation(getIndex());
  }

  private void doNavigation(int index) {
    // Check if we're already in the right part of the book
    if (index == getIndex()) {
      restorePosition();
      progressUpdate();
      return;
    }

    storedIndex = index;
    strategy.clearText();
    spine.navigateByIndex(index);

    loadText();
  }

  public Option<List<TocEntry>> getTableOfContents() {
    if (book != null) {
      return some(TocUtils.flattenTocReferences(spine, book.getTableOfContents()));
    } else {
      return none();
    }
  }

  public int getIndex() {
    if (spine == null) {
      return storedIndex;
    }

    return spine.getPosition();
  }

  public void setIndex(int index) {
    storedIndex = index;
  }

  public int getProgressPosition() {
    return strategy.getProgressPosition();
  }

  public void setPosition(int pos) {
    strategy.setPosition(pos);
  }

  private void restorePosition() {
    if (storedAnchor != null) {
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

  public Book getBook() {
    return book;
  }

  public void setTextColor(int color) {
    if (childView != null) {
      childView.setTextColor(color);
    }

    final TableHandler tableNodeHandler = textLoader.getHtmlTagHandler("table");
    if (tableNodeHandler != null) {
      tableNodeHandler.setTextColor(color);
    }
  }

  public void setTextSize(float textSize) {
    childView.setTextSize(textSize);

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
      listener.onBookParsed(book);
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
      childView.setBackgroundColor(color);
    }
  }

  private void notifyOnStartRenderingText() {
    if (listener != null) {
      listener.onStartRenderingText();
    }
  }

  private void progressUpdate() {
    if (spine == null) {
      return;
    }

    strategy.getText().filter(new Filter<Spanned>() {
      @Override public Boolean execute(Spanned t) {
        return t.length() > 0;
      }
    }).forEach(new Command<Spanned>() {
      @Override public void execute(Spanned text) {
        final FixedPagesStrategy strategy = getStrategy();
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
      return getStrategy().getPageOffsets().size();
    }

    return -1;
  }

  public int getCurrentPage() {
    if (spine != null) {
      return getStrategy().getCurrentPage() + 1;
    }

    return -1;
  }

  private boolean needsPageNumberCalculation() {
    final List<List<Integer>> offsets = di.readerBookMetadataManager.retrievePageOffsets(di.config, bookMetadata.bookId);
    return offsets.isEmpty();
  }

  public void setEnableScrolling(boolean enableScrolling) {
    if (strategy == null || strategy.isScrolling() != enableScrolling) {
      int pos = -1;
      boolean wasNull = true;

      Spanned text = null;

      if (strategy != null) {
        pos = strategy.getTopLeftPosition();
        text = strategy.getText().unsafeGet();
        strategy.clearText();
        wasNull = false;
      }

      strategy = new FixedPagesStrategy();
      strategy.setBookView(this);

      if (!wasNull) {
        strategy.setPosition(pos);
      }

      if (text != null && text.length() > 0) {
        strategy.loadText(text);
      }
    }
  }

  private class BookViewImageTagHandler extends ImageTagHandler {

    BookViewImageTagHandler(Context context, BookMetadata bm, ResourcesLoader resourcesLoader, DICompanion di, Logger logger) {
      super(context, bm, resourcesLoader, di, logger);
    }

    @Override public void onBitmapDrawableCreated(Drawable drawable, SpannableStringBuilder builder, int start, int end, final String data) {
      drawable.setCallback(callback);
      final ClickableImageSpan imageSpan = new ClickableImageSpan(drawable, new ClickableImageSpan.ClickableImageSpanListener() {
        @Override public void onImageClick(View v, Drawable drawable) {
          if (listener != null) {
            listener.onBookImageClicked(drawable, data);
          }
        }
      });
      builder.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      builder.setSpan(new CenterSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override protected int getViewHeight() {
      return getHeight();
    }

    @Override protected int getViewWidth() {
      return getWidth();
    }

    @Override protected int getViewVerticalMargin() {
      return verticalMargin;
    }

    @Override protected int getViewHorizontalMargin() {
      return horizontalMargin;
    }

    @Override protected Resources getResources() {
      return book.getResources();
    }

  }

  private class LoadTextTask extends QueueableAsyncTask<Resource, Void, Spanned> {

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
            return LoadTextTask.this.isCancelled();
          }
        });

        // Create FastBitmapDrawables (but don't allocate memory of the image itself)
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
      strategy.updatePosition();
      progressUpdate();

      if (spine != null) { // Spine should not be null (check properly with refactor)
        final Resource resource = spine.getCurrentResource().unsafeGet();
        parseEntryComplete(resource);
      }

      // This is a hack for scrolling not updating to the right position on Android 4+
      if (strategy.isScrolling()) {
        scrollHandler.postDelayed(new Runnable() {
          @Override public void run() {
            restorePosition();
          }
        }, 100);
      }

      taskQueue.executeTask(new PreLoadNextResourceTask(spine, resourcesLoader));
    }
  }

  private class CalculatePageNumbersTask extends QueueableAsyncTask<Object, Void, List<List<Integer>>> {

    @Override public Option<List<List<Integer>>> doInBackground(Object... params) {
      try {
        final List<List<Integer>> offsets = getOffsets();
        di.readerBookMetadataManager.savePageOffsets(di.config, bookMetadata.bookId, offsets);
        logger.d(TAG, "Calculated offsets: " + offsets);
        return option(offsets);
      } catch (OutOfMemoryError | Exception e) {
        logger.sendIssue(TAG, "Exception while trying to calculate page. Current exception: " + Throwables.getStackTraceAsString(e));
        return none();
      }
    }

    @Override public void doOnPostExecute(Option<List<List<Integer>>> result) {
      logger.d(TAG, "Page number calculation completed.");
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
    private List<List<Integer>> getOffsets() {
      final List<List<Integer>> result = new ArrayList<>();

      final HtmlSpanner spanner = new HtmlSpanner();
      spanner.setFontResolver(new SystemFontResolver());

      final TableHandler handler = new TableHandler();
      final int tableWidth = (int) (childView.getWidth() * 0.9);
      handler.setTableWidth(tableWidth);
      spanner.registerHandler("table", handler);

      final CSSLinkHandler cssLinkHandler = new CSSLinkHandler(textLoader);
      spanner.registerHandler("link", cssLinkHandler);

      final FixedPagesStrategy fixedPagesStrategy = new FixedPagesStrategy();
      fixedPagesStrategy.setBookView(BookView.this);

      final Resource currentResource = spine.getCurrentResource().unsafeGet();

      if (currentResource != null) {
        final Spannable cachedText = textLoader.getCachedTextForResource(currentResource).unsafeGet();
        if (cachedText != null) {
          result.add(fixedPagesStrategy.getPageOffsets(cachedText));
        }
      }

      return result;
    }
  }
}