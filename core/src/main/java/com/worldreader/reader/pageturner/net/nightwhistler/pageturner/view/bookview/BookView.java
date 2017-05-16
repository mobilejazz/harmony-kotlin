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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.reader.epublib.nl.siegmann.epublib.Constants;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.TOCReference;
import com.worldreader.reader.epublib.nl.siegmann.epublib.epub.EpubReader;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.StringUtil;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.dto.TocEntry;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.ResourceLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.ResourceLoader.ResourceCallback;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.TaskQueue;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.FastBitmapDrawable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jedi.functional.Command;
import jedi.functional.Command0;
import jedi.functional.Filter;
import jedi.option.None;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.handlers.TableHandler;
import net.nightwhistler.htmlspanner.spans.CenterSpan;
import org.htmlcleaner.TagNode;
import org.javatuples.Triplet;

import static java.util.Arrays.asList;

import static jedi.functional.FunctionalPrimitives.forEach;
import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.*;

public class BookView extends ScrollView implements TextSelectionActions.SelectedTextProvider {

  private static final String TAG = BookView.class.getSimpleName();

  private int storedIndex;
  private String storedAnchor;

  private InnerView childView;

  private Set<BookViewListener> listeners;

  private TableHandler tableHandler;

  private PageTurnerSpine spine;

  private String fileName;
  private Book book;
  private String contentOpf;
  private String tocResourcePath;

  private int prevIndex = -1;
  private int prevPos = -1;

  private PageChangeStrategy strategy;
  private ResourceLoader loader;

  private int horizontalMargin = 0;
  private int verticalMargin = 0;
  private int lineSpacing = 0;

  private Handler scrollHandler;

  private Configuration configuration;

  private TextLoader textLoader;

  private EpubFontResolver fontResolver;

  private TaskQueue taskQueue;

  private ResourcesLoader resourcesLoader;

  public BookView(Context context, AttributeSet attributes) {
    super(context, attributes);
    this.scrollHandler = new Handler();

    this.textLoader = StreamingTextLoader.getInstance();
    this.fontResolver = new EpubFontResolver(this.textLoader, context);

    this.configuration = new Configuration(context);

    this.taskQueue = new TaskQueue();
  }

  @TargetApi(Configuration.TEXT_SELECTION_PLATFORM_VERSION)
  public void init(String contentOpf, String tocResourcePath, ResourcesLoader resourcesLoader) {
    this.contentOpf = contentOpf;
    this.tocResourcePath = tocResourcePath;

    this.listeners = new HashSet<>();

    this.childView = (InnerView) this.findViewById(R.id.bookview_inner);
    this.childView.setBookView(this);
    this.childView.setCursorVisible(false);
    this.childView.setLongClickable(true);
    this.childView.setFocusable(true);
    this.childView.setLinksClickable(true);
    if (Build.VERSION.SDK_INT >= Configuration.TEXT_SELECTION_PLATFORM_VERSION) {
      this.childView.setTextIsSelectable(true);
    }

    this.setVerticalFadingEdgeEnabled(false);

    this.setSmoothScrollingEnabled(false);
    this.tableHandler = new TableHandler();
    this.textLoader.registerTagNodeHandler("table", tableHandler);

    StreamingImageTagHandler imgHandler = new StreamingImageTagHandler();
    this.textLoader.registerTagNodeHandler("img", imgHandler);
    this.textLoader.registerTagNodeHandler("image", imgHandler);

    this.textLoader.setLinkCallBack(new LinkTagHandler.LinkCallBack() {
      @Override public void linkClicked(String href) {
        BookView.this.onLinkClicked(href);
      }
    });
    this.resourcesLoader = resourcesLoader;
    this.textLoader.setResourcesLoader(resourcesLoader);
  }

  private void onInnerViewResize() {
    restorePosition();

    if (this.tableHandler != null) {
      int tableWidth = (int) (childView.getWidth() * 0.9);
      tableHandler.setTableWidth(tableWidth);
    }
  }

  public String getFileName() {
    return fileName;
  }

  public int getSpineSize(){ return spine != null ? spine.size() : 0;}

  public void setFileName(String fileName) {
    this.fileName = fileName;
    //this.loader = new ResourceLoader(fileName);
  }

  public void onLinkClicked(String href) {
    navigateTo(spine.resolveHref(href));
  }

  public PageChangeStrategy getStrategy() {
    return this.strategy;
  }

  /**
   * Returns if we're at the start of the book, i.e. displaying the title
   * page.
   */
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
   * @return a List of spans of type A, may be empty.
   */
  private <A> List<A> getSpansAt(float x, float y, Class<A> spanClass) {

    Option<Integer> offsetOption = findOffsetForPosition(x, y);

    CharSequence text = childView.getText();

    if (isEmpty(offsetOption) || !(text instanceof Spanned)) {
      return new ArrayList<>();
    }

    int offset = offsetOption.getOrElse(0);

    return asList(((Spanned) text).getSpans(offset, offset, spanClass));
  }

  /**
   * Blocks the inner-view from creating action-modes for a given amount of time.
   */
  public void blockFor(long time) {
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

  public boolean hasPrevPosition() {
    return this.prevIndex != -1 && this.prevPos != -1;
  }

  @Override public void setOnTouchListener(OnTouchListener l) {
    super.setOnTouchListener(l);
    this.childView.setOnTouchListener(l);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void setTextSelectionCallback(TextSelectionCallback callback,
      ActionModeListener listener) {
    if (Build.VERSION.SDK_INT >= Configuration.TEXT_SELECTION_PLATFORM_VERSION) {
      this.childView.setCustomSelectionActionModeCallback(
          new TextSelectionActions(getContext(), listener, callback, this));
    }
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
      setPadding(this.horizontalMargin, this.verticalMargin, this.horizontalMargin,
          this.verticalMargin);
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
      setPadding(this.horizontalMargin, this.verticalMargin, this.horizontalMargin,
          this.verticalMargin);
      if (strategy != null) {
        strategy.updatePosition();
      }
    }
  }

  public Option<String> getSelectedText() {

    int start = getSelectionStart();
    int end = getSelectionEnd();

    if (start > 0 && end > 0 && end > start) {
      return some(
          childView.getText().subSequence(getSelectionStart(), getSelectionEnd()).toString());
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

  void loadText() {

    if (spine == null && !textLoader.hasCachedBook(this.fileName)) {
      taskQueue.executeTask(new OpenStreamingBookTask());
      taskQueue.executeTask(new LoadStreamingTextTask(true /* initialLoad*/));
    } else {

      if (spine == null) {
        try {
          Book book = initBookAndSpine();

          if (book != null) {
            bookOpened(book);
          }
        } catch (IOException io) {
          errorOnBookOpening(io.getMessage());
          return;
        } catch (OutOfMemoryError e) {
          errorOnBookOpening(getContext().getString(R.string.out_of_memory));
          return;
        }
      }

      //TODO: what if the resource is None?
      spine.getCurrentResource().forEach(new Command<Resource>() {
        @Override public void execute(Resource resource) {
          loadText(resource);
        }
      });
    }
  }

  private void loadText(Resource resource) {
    Log.d(TAG, "Trying to load text for resource " + resource);

    Option<Spannable> cachedText = textLoader.getCachedTextForResource(resource);

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
    parseEntryComplete(spine.getCurrentTitle().getOrElse(""),
        spine.getCurrentResource().unsafeGet());
  }

  private Book initBookAndSpine() throws IOException {
    Book book = textLoader.initBook(fileName);

    this.book = book;
    this.spine = new PageTurnerSpine(book, resourcesLoader);

    this.spine.navigateByIndex(BookView.this.storedIndex);

    if (configuration.isShowPageNumbers()) {

      Option<List<List<Integer>>> offsets = configuration.getPageOffsets(fileName);

      offsets.filter(new Filter<List<List<Integer>>>() {
        @Override public Boolean execute(List<List<Integer>> o) {
          return o.size() > 0;
        }
      }).forEach(new Command<List<List<Integer>>>() {
        @Override public void execute(List<List<Integer>> o) {
          spine.setPageOffsets(o);
        }
      });
    }

    return book;
  }

  private Book initStreamingBookAndSpine(InputStream is) {
    this.book = textLoader.initBook(is);

    try {
      InputStream inputStream = resourcesLoader.loadResource(tocResourcePath);
      book.getNcxResource().setData(inputStream);
    } catch (IOException e) {
      book.getNcxResource().setData(new byte[] {});
    }

    EpubReader.processNcxResource(book);

    this.spine = new PageTurnerSpine(this.book, this.resourcesLoader);

    this.spine.navigateByIndex(BookView.this.storedIndex);

    return this.book;
  }

  public void setFontFamily(FontFamily family) {
    this.childView.setTypeface(family.getDefaultTypeface());
    this.tableHandler.setTypeFace(family.getDefaultTypeface());
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
    for (BookViewListener listener : BookView.this.listeners) {
      listener.onPageDownFirstPage();
    }
  }

  private void notifyListenersPageDownEvent() {
    for (BookViewListener listener : BookView.this.listeners) {
      listener.onPageDown();
    }
  }

  private void notifyListenersLastPageDownEvent() {
    for (BookViewListener listener : BookView.this.listeners) {
      listener.onLastScreenPageDown();
    }
  }

  TextView getInnerView() {
    return childView;
  }

  PageTurnerSpine getSpine() {
    return this.spine;
  }

  private Option<Integer> findOffsetForPosition(float x, float y) {

    if (childView == null || childView.getLayout() == null) {
      return none();
    }

    Layout layout = this.childView.getLayout();
    int line = layout.getLineForVertical((int) y);

    return option(layout.getOffsetForHorizontal(line, x));
  }

  /**
   * Returns the full word containing the character at the selected location.
   */
  public Option<SelectedWord> getWordAt(float x, float y) {

    if (childView == null) {
      return none();
    }

    CharSequence text = this.childView.getText();

    if (text.length() == 0) {
      return none();
    }

    Option<Integer> offsetOption = findOffsetForPosition(x, y);

    if (isEmpty(offsetOption)) {
      return none();
    }

    int offset = offsetOption.unsafeGet();

    if (offset < 0 || offset > text.length() - 1 || isBoundaryCharacter(text.charAt(offset))) {
      return none();
    }

    int left = Math.max(0, offset - 1);
    int right = Math.min(text.length(), offset);

    CharSequence word = text.subSequence(left, right);
    while (left > 0 && !isBoundaryCharacter(word.charAt(0))) {
      left--;
      word = text.subSequence(left, right);
    }

    if (word.length() == 0) {
      return none();
    }

    while (right < text.length() && !isBoundaryCharacter(word.charAt(word.length() - 1))) {
      right++;
      word = text.subSequence(left, right);
    }

    int start = 0;
    int end = word.length();

    if (isBoundaryCharacter(word.charAt(0))) {
      start = 1;
      left++;
    }

    if (isBoundaryCharacter(word.charAt(word.length() - 1))) {
      end = word.length() - 1;
      right--;
    }

    if (start > 0 && start < word.length() && end < word.length()) {
      word = word.subSequence(start, end);

      return some(new SelectedWord(left, right, word));
    }

    return none();
  }

  private static boolean isBoundaryCharacter(char c) {
    char[] boundaryChars = {
        ' ', '.', ',', '\"', '\'', '\n', '\t', ':', '!', '\''
    };

    for (int i = 0; i < boundaryChars.length; i++) {
      if (boundaryChars[i] == c) {
        return true;
      }
    }

    return false;
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
      href =
          URLDecoder.decode(StringUtil.substringBefore(rawHref, Constants.FRAGMENT_SEPARATOR_CHAR),
              charsetName);
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

        Resource resource = book.getResources().getByHref(href);

        if (resource != null) {
          loadText(resource);
        } else {
          loadText(new SpannedString(getContext().getString(R.string.dead_link)));
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
    if (this.book != null) {
      List<TocEntry> result = new ArrayList<>();
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

      String title = "";

      for (int i = 0; i < level; i++) {
        title += "  ";
      }

      title += ref.getTitle();

      if (ref.getResource() != null) {
        entries.add(new TocEntry(title, spine.resolveTocHref(ref.getCompleteHref())));
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
   *
   * Call this after setPosition() to actually go there.
   */
  private void restorePosition() {

    if (this.storedAnchor != null) {

      spine.getCurrentHref().forEach(new Command<String>() {
        @Override public void execute(String href) {
          Option<Integer> anchorValue = BookView.this.textLoader.getAnchor(href, storedAnchor);

          if (!isEmpty(anchorValue)) {
            strategy.setPosition(anchorValue.getOrElse(0));
            BookView.this.storedAnchor = null;
          }
        }
      });
    }

    this.strategy.updatePosition();
  }

  private void setImageSpan(SpannableStringBuilder builder, Drawable drawable, int start, int end) {
    builder.setSpan(new ImageSpan(drawable), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    //if (spine != null && spine.isCover()) {
    builder.setSpan(new CenterSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    //}
  }

  private class ImageCallback implements ResourceCallback {

    private SpannableStringBuilder builder;
    private int start;
    private int end;

    private String storedHref;

    private boolean fakeImages;

    public ImageCallback(String href, SpannableStringBuilder builder, int start, int end,
        boolean fakeImages) {
      this.builder = builder;
      this.start = start;
      this.end = end;
      this.storedHref = href;
      this.fakeImages = fakeImages;
    }

    @Override public void onLoadResource(String href, InputStream input) {
      if (fakeImages) {
        Log.d(TAG, "Faking image for href: " + href);
        setFakeImage(input);
      } else {
        Log.d(TAG, "Loading real image for href: " + href);
        setBitmapDrawable(input);
      }
    }

    private void setFakeImage(InputStream input) {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(input, null, options);

      final Triplet<Integer, Integer, Boolean> sizes = calculateProperImageSize(options.outWidth, options.outHeight);

      ShapeDrawable draw = new ShapeDrawable(new RectShape());
      draw.setBounds(0, 0, sizes.getValue0(), sizes.getValue1());

      setImageSpan(builder, draw, start, end);
    }

    private void setBitmapDrawable(InputStream input) {
      Bitmap bitmap = null;
      try {
        bitmap = getBitmap(input);

        if (bitmap == null || bitmap.getHeight() < 1 || bitmap.getWidth() < 1) {
          return;
        }
      } catch (OutOfMemoryError outofmem) {
        Log.e(TAG, "Could not load image", outofmem);
      }

      if (bitmap != null) {

        FastBitmapDrawable drawable = new FastBitmapDrawable(bitmap);

        drawable.setBounds(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        setImageSpan(builder, drawable, start, end);

        Log.d(TAG, "Storing image in cache: " + storedHref);

        textLoader.storeImageInChache(storedHref, drawable);
      }
    }

    private Bitmap getBitmap(InputStream input) {
      //if (Configuration.IS_NOOK_TOUCH) {
      // Workaround for skia failing to load larger (>8k?) JPEGs on Nook Touch and maybe other older Eclair devices (unknown!)
      // seems the underlying problem is the ZipInputStream returning data in chunks,
      // may be as per http://code.google.com/p/android/issues/detail?id=6066
      // workaround is to stream the whole image out of the Zip to a ByteArray, then pass that on to the bitmap decoder
      //  try {
      //    input = new ByteArrayInputStream(IOUtil.toByteArray(input));
      //  } catch (IOException ex) {
      //    Log.e(TAG, "Failed to extract full image from epub stream: " + ex.toString());
      //  }
      // }

      final Bitmap originalBitmap = BitmapFactory.decodeStream(input);

      if (originalBitmap != null) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

        Triplet<Integer, Integer, Boolean> targetSizes = calculateProperImageSize(originalWidth, originalHeight);
        int targetWidth = targetSizes.getValue0();
        int targetHeight = targetSizes.getValue1();

        if (targetHeight != originalHeight || targetWidth != originalWidth) {
          return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
        }
      }

      return originalBitmap;
    }
  }

  private class StreamingResourceCallback implements ResourcesLoader.ImageResourceCallback {

    private SpannableStringBuilder builder;
    private int start;
    private int end;
    private String storedHref;

    public StreamingResourceCallback(String href, SpannableStringBuilder builder, int start,
        int end) {
      this.builder = builder;
      this.start = start;
      this.end = end;
      this.storedHref = href;
    }

    @Override public void onLoadImageResource(String href, InputStream stream) {
      setBitmapDrawable(stream);
    }

    private void setBitmapDrawable(InputStream input) {
      Bitmap bitmap = null;
      try {
        bitmap = getBitmapOptimized(input);

        if (bitmap == null || bitmap.getHeight() < 1 || bitmap.getWidth() < 1) {
          return;
        }
      } catch (OutOfMemoryError | IOException e) {
        Log.e(TAG, "Could not load image", e);
      }

      if (bitmap != null) {
        final FastBitmapDrawable drawable = new FastBitmapDrawable(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);

        setImageSpan(builder, drawable, start, end);

        Log.d(TAG, "Storing image in cache: " + storedHref);

        textLoader.storeImageInChache(storedHref, drawable);
      }
    }

    @Nullable private Bitmap getBitmapOptimized(InputStream input) throws IOException {
      // First, let's decode image size (to avoid having the image loaded in memory
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;

      // Let's obtain the size of the bitmap image
      BitmapFactory.decodeStream(input, null, options);

      // Reset InputStream to beginning for decoding properly later the image
      input.reset();

      if (options.outHeight != -1 && options.outWidth != -1) {
        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;

        final Triplet<Integer, Integer, Boolean> targetSizes = calculateProperImageSize(originalWidth, originalHeight);
        final int targetWidth = targetSizes.getValue0();
        final int targetHeight = targetSizes.getValue1();
        final boolean isResized = targetSizes.getValue2();

        if (targetHeight == 0 || targetWidth == 0) {
          return null;
        }

        // Set properly the new sizes to be decoded
        options.outWidth = targetWidth;
        options.outHeight = targetHeight;

        // Allow to decode the whole InputStream
        options.inJustDecodeBounds = false;

        if (!isResized) {
          // Let's calculate insamplesize to resize the bitmap accordingly
          options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);

          // Let's try to return the most optimized version for the bitmap
          return BitmapFactory.decodeStream(input, null, options);
        } else {
          // Unluckily, we have to resize the Bitmap and for that we need to load Bitmap into memory :( (there's no other option
          final Bitmap originalBitmap = BitmapFactory.decodeStream(input);
          return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
        }
      }

      return null;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
      // Raw height and width of image
      final int height = options.outHeight;
      final int width = options.outWidth;
      int inSampleSize = 1;

      if (height > reqHeight || width > reqWidth) {
        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
          inSampleSize *= 2;
        }
      }

      return inSampleSize;
    }
  }

  private Triplet<Integer, Integer, Boolean> calculateProperImageSize(int originalWidth, int originalHeight) {
    final int screenHeight = getHeight() - (verticalMargin * 2);
    final int screenWidth = getWidth() - (horizontalMargin * 2);

    // We scale to screen width for the cover or if the image is too wide.
    //if (originalWidth > screenWidth || originalHeight > screenHeight) {

      final float ratio = (float) originalWidth / (float) originalHeight;

      int targetHeight = screenHeight - 1;
      int targetWidth = (int) (targetHeight * ratio);

      if (targetWidth > screenWidth - 1) {
        targetWidth = screenWidth - 1;
        targetHeight = (int) (targetWidth * (1 / ratio));
      }

      Log.d(TAG, "Rescaling from "
          + originalWidth
          + "x"
          + originalHeight
          + " to "
          + targetWidth
          + "x"
          + targetHeight);

      return Triplet.with(targetWidth, targetHeight, true);
    //}

    //// Let's try to upscale the image
    //final int scaledWidth = (int) (originalWidth * 1.5);
    //final int scaledHeight = (int) (originalHeight * 1.5);
    //
    //// If the new upscaled dimensions are not higher than screen device
    //if (scaledWidth < screenWidth || scaledHeight < screenHeight) {
    //  return Triplet.with(scaledWidth, scaledHeight, true);
    //}
    //
    //// Otherwise let's return the original size
    //return Triplet.with(originalWidth, originalHeight, false);
  }

  //  private Bitmap getBitmap(InputStream input) {
  //    final Bitmap originalBitmap = BitmapFactory.decodeStream(input);
  //
  //    if (originalBitmap != null) {
  //      int originalWidth = originalBitmap.getWidth();
  //      int originalHeight = originalBitmap.getHeight();
  //
  //      Tuple2<Integer, Integer> targetSizes = calculateProperImageSize(originalWidth, originalHeight);
  //      int targetWidth = targetSizes.a();
  //      int targetHeight = targetSizes.b();
  //
  //      if (targetHeight != originalHeight || targetWidth != originalWidth) {
  //        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
  //      }
  //    }
  //
  //    return originalBitmap;
  //  }
  //}

  private class ImageTagHandler extends TagNodeHandler {

    private boolean fakeImages;

    public ImageTagHandler(boolean fakeImages) {
      this.fakeImages = fakeImages;
    }

    @TargetApi(Build.VERSION_CODES.FROYO) @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end,
        SpanStack span) {

      String src = node.getAttributeByName("src");

      if (src == null) {
        src = node.getAttributeByName("href");
      }

      if (src == null) {
        src = node.getAttributeByName("xlink:href");
      }

      if (src == null) {
        return;
      }

      builder.append("\uFFFC");

      if (src.startsWith("data:image")) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {

          try {
            String dataString = src.substring(src.indexOf(',') + 1);

            byte[] binData = Base64.decode(dataString, Base64.DEFAULT);

            setImageSpan(builder, new BitmapDrawable(getContext().getResources(),
                    BitmapFactory.decodeByteArray(binData, 0, binData.length)), start,
                builder.length());
          } catch (OutOfMemoryError | IllegalArgumentException ia) {
            //Out of memory or invalid Base64, ignore
          }
        }
      } else if (spine != null) {

        String resolvedHref = spine.resolveHref(src);

        if (textLoader.hasCachedImage(resolvedHref) && !fakeImages) {
          Drawable drawable = textLoader.getCachedImage(resolvedHref);
          setImageSpan(builder, drawable, start, builder.length());
          Log.d(TAG, "Got cached href: " + resolvedHref);
        } else {
          Log.d(TAG, "Loading href: " + resolvedHref);
          this.registerCallback(resolvedHref,
              new ImageCallback(resolvedHref, builder, start, builder.length(), fakeImages));
        }
      }
    }

    protected void registerCallback(String resolvedHref, ImageCallback callback) {
      BookView.this.loader.registerCallback(resolvedHref, callback);
    }
  }

  private class StreamingImageTagHandler extends TagNodeHandler {

    protected void registerCallback(String resolvedHref,
        ResourcesLoader.ImageResourceCallback callback) {
      BookView.this.resourcesLoader.registerImageCallback(resolvedHref, callback);
    }

    @TargetApi(Build.VERSION_CODES.FROYO) @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end,
        SpanStack span) {

      String src = node.getAttributeByName("src");

      if (src == null) {
        src = node.getAttributeByName("href");
      }

      if (src == null) {
        src = node.getAttributeByName("xlink:href");
      }

      if (src == null) {
        return;
      }

      builder.append("\uFFFC");

      if (src.startsWith("data:image")) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {

          try {
            String dataString = src.substring(src.indexOf(',') + 1);

            byte[] binData = Base64.decode(dataString, Base64.DEFAULT);

            setImageSpan(builder, new BitmapDrawable(getContext().getResources(), BitmapFactory.decodeByteArray(binData, 0, binData.length)), start, builder.length());
          } catch (OutOfMemoryError | IllegalArgumentException ia) {
            //Out of memory or invalid Base64, ignore
          }
        }
      } else if (spine != null) {

        String resolvedHref = spine.resolveHref(src);

        if (textLoader.hasCachedImage(resolvedHref)) {
          final Drawable drawable = textLoader.getCachedImage(resolvedHref);
          setImageSpan(builder, drawable, start, builder.length());
          Log.d(TAG, "Got cached href: " + resolvedHref);
        } else {
          Log.d(TAG, "Loading href: " + resolvedHref);
          this.registerCallback(resolvedHref, new StreamingResourceCallback(resolvedHref, builder, start, builder.length()));
        }
      }
    }
  }

  public void setTextColor(int color) {
    if (this.childView != null) {
      this.childView.setTextColor(color);
    }

    this.tableHandler.setTextColor(color);
  }

  public Book getBook() {
    return book;
  }

  public void setTextSize(float textSize) {
    this.childView.setTextSize(textSize);
    this.tableHandler.setTextSize(textSize);
  }

  public void addListener(BookViewListener listener) {
    this.listeners.add(listener);
  }

  private void bookOpened(Book book) {
    for (BookViewListener listener : this.listeners) {
      listener.bookOpened(book);
    }
  }

  private void errorOnBookOpening(String errorMessage) {
    for (BookViewListener listener : this.listeners) {
      listener.errorOnBookOpening(errorMessage);
    }
  }

  private void parseEntryStart(int entry) {
    for (BookViewListener listener : this.listeners) {
      listener.parseEntryStart(entry);
    }
  }

  private void parseEntryComplete(String name, Resource resource) {
    for (BookViewListener listener : this.listeners) {
      listener.parseEntryComplete(name, resource);
    }
  }

  private void fireOpenFile() {
    for (BookViewListener listener : this.listeners) {
      listener.readingFile();
    }
  }

  @Override public void setBackgroundColor(int color) {
    super.setBackgroundColor(color);

    if (this.childView != null) {
      this.childView.setBackgroundColor(color);
    }
  }

  private void fireRenderingText() {
    for (BookViewListener listener : this.listeners) {
      listener.renderingText();
    }
  }

  public int getPercentageFor(int index, int offset) {
    if (spine != null) {
      return spine.getProgressPercentage(index, offset);
    }

    return -1;
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
        int pagesOffset =
            ((FixedPagesStrategy) BookView.this.getStrategy()).getPageOffsets().size();
        int currentPage = ((FixedPagesStrategy) BookView.this.getStrategy()).getCurrentPage();
        int progressPercentage = (int) Math.floor(((double) currentPage / pagesOffset) * 100);

        for (BookViewListener listener : BookView.this.listeners) {
          listener.progressUpdate(progressPercentage, 0, 0);
        }
        //}
      }
    });
  }

  public int getTotalNumberOfPages() {
    if (spine != null) {
      return spine.getTotalNumberOfPages();
    }

    return -1;
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

  public int getPageNumberFor(int index, int position) {

    if (spine == null) {
      return -1;
    }

    Log.d(TAG, "Looking for pageNumber for index=" + index + ", position=" + position);

    int pageNum = -1;

    List<List<Integer>> pageOffsets = spine.getPageOffsets();

    if (pageOffsets == null || index >= pageOffsets.size()) {
      return -1;
    }

    for (int i = 0; i < index; i++) {

      int pages = pageOffsets.get(i).size();

      pageNum += pages;

      Log.d(TAG, "Index " + i + ": pages=" + pages);
    }

    List<Integer> offsets = pageOffsets.get(index);

    Log.d(TAG, "Pages before this index: " + pageNum);

    Log.d(TAG, "Offsets according to spine: " + asString(offsets));

    if (this.strategy instanceof FixedPagesStrategy) {
      List<Integer> strategyOffsets = ((FixedPagesStrategy) this.strategy).getPageOffsets();
      Log.d(TAG, "Offsets according to strategy: " + asString(strategyOffsets));
    }

    for (int i = 0; i < offsets.size() && offsets.get(i) <= position; i++) {
      pageNum++;
    }

    Log.d(TAG, "Calculated pageNumber=" + pageNum);
    return pageNum;
  }

  private static String asString(List<Integer> offsets) {

    final StringBuilder stringBuilder = new StringBuilder("[ ");

    forEach(offsets, new Command<Integer>() {
      @Override public void execute(Integer o) {
        stringBuilder.append(o + " ");
      }
    });

    stringBuilder.append(" ]");

    return stringBuilder.toString();
  }

  private boolean needsPageNumberCalculation() {
    if (!configuration.isShowPageNumbers()) {
      return false;
    }

    Option<List<List<Integer>>> offsets = configuration.getPageOffsets(fileName);

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

      if (enableScrolling) {
        this.strategy = new ScrollingStrategy(getContext());
      } else {
        FixedPagesStrategy s = new FixedPagesStrategy();
        s.setConfig(configuration);
        s.setLayoutFactory(new StaticLayoutFactory());
        this.strategy = s;
      }

      strategy.setBookView(this);

      if (!wasNull) {
        this.strategy.setPosition(pos);
      }

      if (text != null && text.length() > 0) {
        this.strategy.loadText(text);
      }
    }
  }

  public static class InnerView extends TextView {

    private BookView bookView;

    private long blockUntil = 0l;

    public InnerView(Context context, AttributeSet attributes) {
      super(context, attributes);
    }

    public void setBookView(BookView bookView) {
      this.bookView = bookView;
    }

    public void setBlockUntil(long blockUntil) {
      this.blockUntil = blockUntil;
    }

    @Override protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      super.onSizeChanged(w, h, oldw, oldh);
      bookView.onInnerViewResize();
    }

    @Override public void onWindowFocusChanged(boolean hasWindowFocus) {
      //We override this method to do nothing, since the base
      //implementation closes the ActionMode.
      //
      //This means that when the user clicks the overflow menu,
      //the ActionMode is stopped and text selection is ended.
      //
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
      if (System.currentTimeMillis() > blockUntil) {
        Log.d(TAG, "InnerView starting action-mode");
        return super.startActionMode(callback);
      } else {
        Log.d(TAG, "Not starting action-mode yet, since block time hasn't expired.");
        clearFocus();
        return null;
      }
    }
  }

  private enum BookReadPhase {
    START,
    OPEN_FILE,
    FETCH_TEXT,
    PARSE_TEXT,
    DONE
  }

  private class OpenStreamingBookTask extends QueueableAsyncTask<None, BookReadPhase, Book> {

    private String error;

    @Override public void doOnPreExecute() {
      fireOpenFile();
    }

    @Override public Option<Book> doInBackground(None... nones) {
      try {
        InputStream is = resourcesLoader.loadResource(contentOpf);
        return some(initStreamingBookAndSpine(is));
      } catch (Exception e) {
        Log.d(TAG, "Exception while reading streaming book has occurred!", e);
      }
      return none();
    }

    @Override public void doOnPostExecute(Option<Book> book) {
      book.match(new Command<Book>() {
                   @Override public void execute(Book book1) {
                     BookView.this.bookOpened(book1);
                   }
                 }, //some
          new Command0() {
            @Override public void execute() {
              errorOnBookOpening(OpenStreamingBookTask.this.error);
            }
          } //none
      );
    }
  }

  private class LoadStreamingTextTask extends QueueableAsyncTask<Resource, BookReadPhase, Spanned> {

    private String name;
    private boolean initialLoad;

    public LoadStreamingTextTask(boolean initialLoad) {
      this.initialLoad = initialLoad;
    }

    public LoadStreamingTextTask() {
    }

    public Option<Spanned> doInBackground(Resource... resources) {
      publishProgress(BookReadPhase.START);

      if (loader != null) {
        loader.clear();
      }

      try {

        this.name = spine.getCurrentTitle().getOrElse("");

        Resource resource;

        if (resources != null && resources.length > 0) {
          resource = resources[0];
        } else if (initialLoad && spine.shouldNavigateToFirstContent(storedIndex)) {
          resource = spine.tryToNavigateToChapterContent().getOrElse(new Resource(""));
        } else {
          resource = spine.getCurrentResource().getOrElse(new Resource(""));
        }

        publishProgress(BookReadPhase.FETCH_TEXT);

        publishProgress(BookReadPhase.PARSE_TEXT);

        Spannable result = textLoader.getText(resource, new HtmlSpanner.CancellationCallback() {
          @Override public boolean isCancelled() {
            return LoadStreamingTextTask.this.isCancelled();
          }
        });
        // Load all image resources.
        resourcesLoader.loadImageResources();

        //If the view isn't ready yet, wait a bit.
        while (getInnerView().getWidth() == 0) {
          Thread.sleep(100);
        }

        strategy.loadText(result);

        return option((Spanned) result);
      } catch (Exception | OutOfMemoryError io) {
        Log.e(TAG, "Error loading text", io);

        //FIXME: actually use this error
        //this.error = String.format( getContext().getString(R.string.could_not_load),
        //      io.getMessage());

        //this.error = getContext().getString(R.string.out_of_memory);
      }

      return none();
    }

    @Override public void doOnProgressUpdate(BookReadPhase... values) {
      BookReadPhase phase = values[0];

      switch (phase) {
        case START:
          parseEntryStart(getIndex());
          break;
        case PARSE_TEXT:
          fireRenderingText();
          break;
        case DONE:
          if (spine == null) {
            parseEntryComplete(this.name, null);
          } else {
            parseEntryComplete(this.name, spine.getCurrentResource().unsafeGet());
          }
          break;
      }
    }

    @Override public void doOnPostExecute(Option<Spanned> result) {
      restorePosition();
      strategy.updateGUI();
      progressUpdate();

      onProgressUpdate(BookReadPhase.DONE);

      /**
       * This is a hack for scrolling not updating to the right position
       * on Android 4+
       */
      if (strategy.isScrolling()) {
        scrollHandler.postDelayed(new Runnable() {
          @Override public void run() {
            BookView.this.restorePosition();
          }
        }, 100);
      }

      taskQueue.executeTask(new PreLoadTask(spine, resourcesLoader));
    }
  }

  private class CalculatePageNumbersTask
      extends QueueableAsyncTask<Object, Void, List<List<Integer>>> {

    private void checkForCancellation() {
      if (isCancelRequested()) {
        throw new IllegalStateException("Cancel requested");
      }
    }

    /**
     * Loads the text offsets for the whole book,
     * with minimal use of resources.
     *
     * @throws IOException
     */
    private Option<List<List<Integer>>> getOffsets() throws IOException {
      List<List<Integer>> result = new ArrayList<>();

      ImageTagHandler tagHandler = new ImageTagHandler(true) {
        protected void registerCallback(String resolvedHref, ImageCallback callback) {
          //imageLoader.registerCallback(resolvedHref, callback);
        }
      };
      final HtmlSpanner mySpanner = new HtmlSpanner();
      mySpanner.setAllowStyling(configuration.isAllowStyling());
      mySpanner.setFontResolver(fontResolver);
      mySpanner.registerHandler("table", tableHandler);
      mySpanner.registerHandler("img", tagHandler);
      mySpanner.registerHandler("image", tagHandler);
      mySpanner.registerHandler("link", new CSSLinkHandler(textLoader));

      FixedPagesStrategy fixedPagesStrategy = CalculatePageNumbersTask.this.getFixedPagesStrategy();
      fixedPagesStrategy.setBookView(BookView.this);

      Resource currentResource = spine.getCurrentResource().unsafeGet();

      if (currentResource != null) {
        Spannable cachedText = textLoader.getCachedTextForResource(currentResource).unsafeGet();
        if (cachedText != null) {
          result.add(fixedPagesStrategy.getPageOffsets(cachedText, true));
        }
      }

      // ------------------------------------------------

      //List<List<Integer>> result = new ArrayList<>();
      //final ResourceLoader imageLoader = new ResourceLoader(fileName);
      //final ResourceLoader textResourceLoader = new ResourceLoader(fileName);
      ////Image loader which only loads image dimensions
      //ImageTagHandler tagHandler = new ImageTagHandler(true) {
      //  protected void registerCallback(String resolvedHref, ImageCallback callback) {
      //    imageLoader.registerCallback(resolvedHref, callback);
      //  }
      //};
      //
      ////Private spanner
      //final HtmlSpanner mySpanner = new HtmlSpanner();
      //mySpanner.setAllowStyling(configuration.isAllowStyling());
      //mySpanner.setFontResolver(fontResolver);
      //mySpanner.registerHandler("table", tableHandler);
      //mySpanner.registerHandler("img", tagHandler);
      //mySpanner.registerHandler("image", tagHandler);
      //mySpanner.registerHandler("link", new CSSLinkHandler(textLoader));
      //
      //final Map<String, List<Integer>> offsetsPerSection = new HashMap<>();
      //
      ////We use the ResourceLoader here to load all the text in the book in 1 pass,
      ////but we only keep a single section in memory at each moment
      //ResourceCallback callback = new ResourceCallback() {
      //  @Override public void onLoadResource(String href, InputStream stream) {
      //    try {
      //      CalculatePageNumbersTask.this.checkForCancellation();
      //      Log.d(TAG, "CalculatePageNumbersTask: loading text for: " + href);
      //      InputStream input = new ByteArrayInputStream(IOUtil.toByteArray(stream));
      //
      //      CalculatePageNumbersTask.this.checkForCancellation();
      //      Spannable text = mySpanner.fromHtml(input, new HtmlSpanner.CancellationCallback() {
      //        @Override public boolean isCancelled() {
      //          return CalculatePageNumbersTask.this.isCancelled();
      //        }
      //      });
      //
      //      CalculatePageNumbersTask.this.checkForCancellation();
      //      imageLoader.load();
      //
      //      CalculatePageNumbersTask.this.checkForCancellation();
      //      FixedPagesStrategy fixedPagesStrategy =
      //          CalculatePageNumbersTask.this.getFixedPagesStrategy();
      //      fixedPagesStrategy.setBookView(BookView.this);
      //
      //      offsetsPerSection.put(href, fixedPagesStrategy.getPageOffsets(text, true));
      //    } catch (IOException io) {
      //      Log.e(TAG, "CalculatePageNumbersTask: failed to load text for " + href, io);
      //    }
      //  }
      //};
      //
      //
      ////Do first pass: grab either cached text, or schedule a callback
      //for (PageTurnerSpine.SpineEntry spineEntry : spine) {
      //  checkForCancellation();
      //  Resource res = spineEntry.getResource();
      //
      //  Option<Spannable> cachedText = textLoader.getCachedTextForResource(res);
      //
      //  if (!isEmpty(cachedText)) {
      //    Log.d(TAG, "CalculatePageNumbersTask: Got cached text for href: " + res.getHref());
      //
      //    FixedPagesStrategy fixedPagesStrategy = getFixedPagesStrategy();
      //    fixedPagesStrategy.setBookView(BookView.this);
      //
      //    offsetsPerSection.put(res.getHref(),
      //        fixedPagesStrategy.getPageOffsets(cachedText.unsafeGet(), true));
      //  } else {
      //    Log.d(TAG, "CalculatePageNumbersTask: Registering callback for href: " + res.getHref());
      //    textResourceLoader.registerCallback(res.getHref(), callback);
      //  }
      //}
      //
      ////Load callbacks, this will fill renderedText
      //textResourceLoader.load();
      //imageLoader.load();
      //
      ////Do a second pass and order the offsets correctly
      //for (PageTurnerSpine.SpineEntry spineEntry : spine) {
      //  checkForCancellation();
      //
      //  Resource res = spineEntry.getResource();
      //  Option<List<Integer>> offsets = none();
      //
      //  //Scan for the full href
      //  for (String href : offsetsPerSection.keySet()) {
      //    if (href.endsWith(res.getHref())) {
      //      offsets = some(offsetsPerSection.get(href));
      //      break;
      //    }
      //  }
      //
      //  if (isEmpty(offsets)) {
      //    Log.e(TAG, "CalculatePageNumbersTask: Missing text for href " + res.getHref());
      //    return none();
      //  }
      //
      //  result.add(offsets.getOrElse(new ArrayList<Integer>()));
      //}

      return some(result);
    }

    //Injection doesn't work from inner classes, so we construct it ourselves.
    private FixedPagesStrategy getFixedPagesStrategy() {
      FixedPagesStrategy fixedPagesStrategy = new FixedPagesStrategy();
      fixedPagesStrategy.setConfig(configuration);
      fixedPagesStrategy.setLayoutFactory(new StaticLayoutFactory());

      return fixedPagesStrategy;
    }

    @Override public void doOnPreExecute() {
      for (BookViewListener listener : listeners) {
        listener.onStartCalculatePageNumbers();
      }
    }

    @Override public Option<List<List<Integer>>> doInBackground(Object... params) {
      if (!needsPageNumberCalculation()) {
        return none();
      }

      try {
        Option<List<List<Integer>>> offsets = getOffsets();
        offsets.forEach(new Command<List<List<Integer>>>() {
          @Override public void execute(List<List<Integer>> o) {
            configuration.setPageOffsets(fileName, o);
          }
        });

        Log.d(TAG, "Calculated offsets: " + offsets);
        return offsets;
      } catch (OutOfMemoryError | Exception e) {
        Log.e(TAG, "Could not read page-numbers", e);
      }

      return none();
    }

    @Override public void doOnCancelled(Option<List<List<Integer>>> lists) {
      if (taskQueue.isEmpty()) {
        for (BookViewListener listener : listeners) {
          listener.onCalculatePageNumbersComplete();

          //listener.onCalculatePageNumbersPerChapterComplete(null);
        }
      }
    }

    @Override public void doOnPostExecute(Option<List<List<Integer>>> result) {
      Log.d(TAG, "Pagenumber calculation completed.");

      for (BookViewListener listener : listeners) {
        listener.onCalculatePageNumbersComplete();
      }

      result.filter(new Filter<List<List<Integer>>>() {
        @Override public Boolean execute(List<List<Integer>> r) {
          return r.size() > 0;
        }
      }).forEach(new Command<List<List<Integer>>>() {
        @Override public void execute(List<List<Integer>> r) {
          spine.setPageOffsets(r);
          progressUpdate();
        }
      });
    }
  }
}
