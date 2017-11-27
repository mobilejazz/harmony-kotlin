package com.worldreader.reader.wr.fragments;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.R;
import com.worldreader.core.application.helper.AndroidFutures;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.application.helper.ui.Dimens;
import com.worldreader.core.application.ui.dialog.DialogFactory;
import com.worldreader.core.application.ui.widget.TutorialView;
import com.worldreader.core.application.ui.widget.discretebar.DiscreteSeekBar;
import com.worldreader.core.common.date.Dates;
import com.worldreader.core.datasource.StreamingBookDataSource;
import com.worldreader.core.domain.interactors.dictionary.GetWordDefinitionInteractor;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.WordDefinition;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.TocEntry;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.AbstractFastBitmapDrawable;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.ActionModeListener;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookNavigationGestureDetector;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookViewListener;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.TextSelectionCallback;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoaderFactory;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.spanner.HtmlSpannerFactory;
import com.worldreader.reader.wr.activities.AbstractReaderActivity;
import com.worldreader.reader.wr.helper.BrightnessManager;
import com.worldreader.reader.wr.helper.systemUi.SystemUiHelper;
import com.worldreader.reader.wr.widget.DefinitionView;
import jedi.option.Option;

import javax.inject.Inject;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

// TODO: 27/11/2017 Move interactors to concrete implementations
// TODO: 27/11/2017 Check visibility bugs (onFocusVisibilityChanged)
// TODO: 27/11/2017 Prepare design to be the same as Pablo's invision
// TODO: 27/11/2017 Fix dagger injection on all projects
public abstract class AbstractReaderFragment extends Fragment implements BookViewListener, SystemUiHelper.OnVisibilityChangeListener {

  public static final String CHANGE_FONT_KEY = "change_font_key";
  public static final String CHANGE_BACKGROUND_KEY = "change.background.key";

  private static final String TAG = AbstractReaderFragment.class.getSimpleName();

  private static final String POS_KEY = "offset:";
  private static final String IDX_KEY = "index:";

  private final Handler handler = new Handler(Looper.getMainLooper());
  private final SavedConfigState savedConfigState = new SavedConfigState();

  private Context context;

  protected BookMetadata bookMetadata;

  private TextLoader textLoader;
  private List<TocEntry> tableOfContents;

  private OnBookTocEntryListener bookTocEntryListener;

  protected BookView bookView;
  protected TextView readingTitleProgressTv;
  protected DiscreteSeekBar chapterProgressDsb;
  protected TextView chapterProgressPagesTv;
  protected DefinitionView definitionView;
  protected TutorialView tutorialView;
  protected View containerTutorialView;
  protected View progressContainer;
  protected ProgressDialog progressDialog;

  protected DICompanion di;

  protected int currentScrolledPages = 0;
  private boolean hasSharedText;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    try {
      bookTocEntryListener = (OnBookTocEntryListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_reader, container, false);
  }

  @SuppressLint("ClickableViewAccessibility") @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    this.context = getActivity();

    // Inject views
    final View view = getView();
    this.bookView = view.findViewById(R.id.reading_fragment_bookView);
    this.readingTitleProgressTv = view.findViewById(R.id.title_chapter_tv);
    this.chapterProgressDsb = view.findViewById(R.id.chapter_progress_dsb);
    this.chapterProgressPagesTv = view.findViewById(R.id.chapter_progress_pages_tv);
    this.definitionView = view.findViewById(R.id.reading_fragment_word_definition_dv);
    this.tutorialView = view.findViewById(R.id.reading_fragment_tutorial_view);
    this.containerTutorialView = view.findViewById(R.id.reading_fragment_container_tutorial_view);
    this.progressContainer = view.findViewById(R.id.reading_fragment_chapter_progress_container);

    // Call injector
    di = onProvideDICompanionObject();

    // Intent content checked properly in AbstractReaderActivity (if BookMetadata is not present we can't continue further)
    final Intent intent = getActivity().getIntent();
    this.bookMetadata = (BookMetadata) intent.getSerializableExtra(AbstractReaderActivity.BOOK_METADATA_KEY);

    // Gather status variables for the reader
    final boolean isFontChanged = intent.getBooleanExtra(CHANGE_FONT_KEY, false);
    final boolean isBackgroundChanged = intent.getBooleanExtra(CHANGE_BACKGROUND_KEY, false);

    // Call gamification events
    onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_INITIALIZE_EVENT);
    if (bookMetadata.collectionId > 0) {
      onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_START_BOOK_FROM_COLLECTION_EVENT);
    }
    onNotifyGamificationFontOrBackgroundReaderEvents(isFontChanged, isBackgroundChanged);
    onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_READ_ON_X_CONTINOUS_DAYS_EVENT);
    onReaderFragmentEvent(BookReaderEvents.SAVE_CURRENTLY_BOOK_READING_EVENT);

    // Prepare resources loader from BookMetadata
    final ResourcesLoader resourcesLoader = ResourcesLoaderFactory.create(bookMetadata, di);

    // Prepare resources loader
    this.textLoader = new TextLoader(HtmlSpannerFactory.create(di.config), resourcesLoader);

    // Initialize BookView
    this.bookView.init(bookMetadata, resourcesLoader, textLoader, di.logger);
    this.bookView.setListener(this);
    this.bookView.setTextSelectionCallback(new ReaderTextSelectionCallback(), new ReaderActionModeListener());

    // Setup definitionView listener
    this.definitionView.setOnClickCrossListener(new DefinitionView.OnClickCrossListener() {
      @Override public void onClick(DefinitionView view) {
        hideDefinitionView();
      }
    });

    // Calculate dimensions for progress container with navigation bar height
    final int pl = progressContainer.getPaddingLeft();
    final int pt = progressContainer.getPaddingTop();
    final int pr = progressContainer.getPaddingRight();
    final int pb = progressContainer.getPaddingBottom() + Dimens.obtainNavBarHeight(context);
    this.progressContainer.setPadding(pl, pt, pr, pb);

    this.chapterProgressDsb.setEnabled(true);
    this.chapterProgressDsb.setOnProgressChangeListener(new DiscreteSeekBar.SimpleOnProgressChangeListener() {

      private int seekValue;

      @Override public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        this.seekValue = value;
      }

      @Override public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        bookView.navigateToPercentageInChapter(this.seekValue);
        formatPageChapterProgress();
      }
    });

    final DisplayMetrics metrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

    // Setup our special gesture detector to be aware of user movements in the reader
    final GestureDetector gestureDetector = new GestureDetector(context, new BookNavigationGestureDetector(bookView, metrics, this));

    bookView.setOnTouchListener(new View.OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility") @Override public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
      }
    });

    // Prepare registration for menu in BookView
    registerForContextMenu(bookView);

    // Save current state
    saveConfigState();

    // Load state in reader
    updateFromPrefs();

    // Try to restore last read position
    restoreLastReadPosition(savedInstanceState);

    // Kick start loading of text
    bookView.startLoadingText();
  }

  @Override public void onResume() {
    super.onResume();
    checkIfHasBeenSharedQuote();
  }

  @Override public void onPause() {
    Log.d(TAG, "onPause() called.");
    saveReadingPosition();
    onReaderFragmentEvent(BookReaderEvents.READ_PAGE_ANALYTICS_EVENT);
    super.onPause();
  }

  @Override public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop() called.");
    dismissProgressDialog();
  }

  @Override public void onDestroy() {
    this.bookView.releaseResources();
    this.dismissProgressDialog();
    super.onDestroy();
  }

  @Override public void onLowMemory() {
    super.onLowMemory();
    this.textLoader.clearCachedText();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    onFragmentActivityResult(requestCode, resultCode, data);
  }

  @Override public void onSaveInstanceState(final Bundle outState) {
    if (this.bookView != null) {
      outState.putInt(POS_KEY, this.bookView.getProgressPosition());
      outState.putInt(IDX_KEY, this.bookView.getIndex());
    }
  }

  @Override public void onVisibilityChange(final boolean visible) {
    final AbstractReaderActivity activity = ((AbstractReaderActivity) getActivity());
    if (activity == null) {
      return;
    }

    final AppBarLayout toolbarLayout = activity.getToolbarLayout();
    final AnimatorSet set = new AnimatorSet();
    final ObjectAnimator progressViewAnimator;
    final ObjectAnimator toolbarViewAnimator;

    if (visible) {
      progressViewAnimator = ViewPropertyObjectAnimator.animate(progressContainer)
          .alpha(1)
          .translationY(0)
          .setDuration(300)
          .setInterpolator(new FastOutSlowInInterpolator())
          .addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(android.animation.Animator animation) {
              progressContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
          })
          .get();
      toolbarViewAnimator =
          ViewPropertyObjectAnimator.animate(toolbarLayout).alpha(1).translationY(0).setDuration(300).setInterpolator(new FastOutSlowInInterpolator()).get();
    } else {
      progressViewAnimator = ViewPropertyObjectAnimator.animate(progressContainer)
          .alpha(0)
          .translationY(progressContainer.getBottom())
          .setDuration(300)
          .setInterpolator(new FastOutSlowInInterpolator())
          .addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(android.animation.Animator animation) {
              progressContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
          })
          .get();
      toolbarViewAnimator = ViewPropertyObjectAnimator.animate(toolbarLayout)
          .alpha(0)
          .translationY(-toolbarLayout.getBottom())
          .setDuration(300)
          .setInterpolator(new FastOutSlowInInterpolator())
          .get();
    }

    set.playTogether(progressViewAnimator, toolbarViewAnimator);
    set.start();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();

    if (itemId == R.id.show_book_content) {
      bookTocEntryListener.displayBookTableOfContents();
      return true;
    } else if (itemId == R.id.display_options) {
      final ReaderSettingsDialog d = new ReaderSettingsDialog();
      d.setBrightnessManager(di.brightnessManager);
      d.setConfiguration(di.config);
      d.setOnModifyReaderSettingsListener(new ReaderSettingsDialog.ModifyReaderSettingsListener() {
        @Override public void onReaderSettingsModified(ReaderSettingsDialog.Action action) {
          switch (action) {
            case MODIFIED:
              updateFromPrefs();
              break;
          }
        }
      });
      final FragmentManager fm = getFragmentManager();
      d.show(fm, ReaderSettingsDialog.TAG);
      return true;
    } else if (itemId == R.id.text_to_speech) {
      onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_TEXT_TO_SPEECH_ACTIVATED_EVENT);
      return true;
    } else if (itemId == android.R.id.home) {
      if (isPhotoViewerDisplayed()) {
        hidePhotoViewer();
        return false;
      }
      getActivity().finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  protected abstract void onFragmentActivityResult(final int requestCode, final int resultCode, final Intent data);

  private void checkIfHasBeenSharedQuote() {
    if (hasSharedText) {
      hasSharedText = false;
      handler.postDelayed(new Runnable() {
        @Override public void run() {
          onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_SHARED_QUOTE_EVENT);
        }
      }, 500);
    }
  }

  private void updateFromPrefs() {
    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    if (activity == null) {
      return;
    }

    bookView.setTextSize(di.config.getTextSize());

    int marginH = di.config.getHorizontalMargin();
    int marginV = di.config.getVerticalMargin();

    bookView.setFontFamily(di.config.getSerifFontFamily());

    textLoader.fromConfiguration(di.config);

    bookView.setHorizontalMargin(marginH);
    bookView.setVerticalMargin(marginV);
    bookView.setEnableScrolling(di.config.isScrollingEnabled());
    bookView.setLineSpacing(di.config.getLineSpacing());

    final ActionBar actionBar = activity.getSupportActionBar();
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle("");

    final SystemUiHelper systemUiHelper = getSystemUiHelper();
    if (systemUiHelper != null) {
      if (di.config.isKeepScreenOn()) {
        systemUiHelper.keepScreenOn();
      } else {
        systemUiHelper.keepScreenOff();
      }
    }

    restoreColorProfile();

    // Check if we need a restart

    final boolean isFontChanged = !di.config.getSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.serifFontName);
    final boolean isBackgroundChanged = di.config.getColourProfile() != savedConfigState.colorProfile;
    final boolean isStripWhiteSpaceEnabled = di.config.isStripWhiteSpaceEnabled() != savedConfigState.stripWhiteSpace;
    final boolean fontNameChanged = di.config.getDefaultFontFamily().getName().equalsIgnoreCase(savedConfigState.fontName);
    final boolean isSansSerifFontNameEqual = di.config.getSansSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.sansSerifFontName);
    if (!savedConfigState.usePageNum
        || isStripWhiteSpaceEnabled
        || !fontNameChanged
        || isFontChanged
        || !isSansSerifFontNameEqual
        || di.config.getHorizontalMargin() != savedConfigState.hMargin
        || di.config.getVerticalMargin() != savedConfigState.vMargin
        || di.config.getTextSize() != savedConfigState.textSize
        || di.config.isScrollingEnabled() != savedConfigState.scrolling
        || di.config.isUseColoursFromCSS() != savedConfigState.allowColoursFromCSS) {

      textLoader.invalidateCachedText();
      restartActivity(isFontChanged, isBackgroundChanged);
    }
  }

  @Nullable private SystemUiHelper getSystemUiHelper() {
    if (getActivity() != null) {
      return ((AbstractReaderActivity) getActivity()).getSystemUiHelper();
    }
    return null;
  }

  private void restoreColorProfile() {
    this.bookView.setBackgroundColor(di.config.getBackgroundColor());
    this.bookView.setTextColor(di.config.getTextColor());
    this.bookView.setLinkColor(di.config.getLinkColor());

    int brightness = di.config.getBrightness();
    di.brightnessManager.setBrightness(getActivity().getWindow(), brightness);
  }

  private void restartActivity(boolean isChangedFont, boolean isBackgroundModified) {
    onStop();

    //Clear any cached text.
    textLoader.closeCurrentBook();

    final Intent intent = getActivity().getIntent();
    intent.putExtra(AbstractReaderActivity.BOOK_METADATA_KEY, bookMetadata);
    intent.putExtra(CHANGE_FONT_KEY, isChangedFont);
    intent.putExtra(CHANGE_BACKGROUND_KEY, isBackgroundModified);
    startActivity(intent);

    AppCompatActivity activity = (AppCompatActivity) getActivity();

    if (activity != null) {
      activity.finish();
    }
  }

  private void dismissProgressDialog() {
    if (progressDialog != null) {
      this.progressDialog.dismiss();
      this.progressDialog = null;
    }
  }

  public void saveReadingPosition() {
    if (this.bookView != null) {
      int index = this.bookView.getIndex();
      int position = this.bookView.getProgressPosition();

      if (index != -1 && position != -1 && !bookView.isAtEnd()) {
        di.config.setLastPosition(this.bookMetadata.bookId, position);
        di.config.setLastIndex(this.bookMetadata.bookId, index);
      } else if (bookView.isAtEnd()) {
        di.config.setLastPosition(this.bookMetadata.bookId, -1);
        di.config.setLastIndex(this.bookMetadata.bookId, -1);
      }
    }
  }

  protected abstract DICompanion onProvideDICompanionObject();

  private void onNotifyGamificationFontOrBackgroundReaderEvents(final boolean isFontChanged, final boolean isBackgroundChanged) {
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        if (isFontChanged) {
          onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_FONT_SIZE_CHANGED_EVENT);
        } else if (isBackgroundChanged) {
          onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_BACKGROUND_CHANGED_EVENT);
        }
      }
    }, 500);
  }

  protected abstract void onReaderFragmentEvent(@BookReaderFragmentEvent int event);

  public void saveConfigState() {
    // Cache old settings to check if we'll need a restart later
    savedConfigState.stripWhiteSpace = di.config.isStripWhiteSpaceEnabled();

    savedConfigState.usePageNum = true;

    savedConfigState.hMargin = di.config.getHorizontalMargin();
    savedConfigState.vMargin = di.config.getVerticalMargin();

    savedConfigState.textSize = di.config.getTextSize();
    savedConfigState.fontName = di.config.getDefaultFontFamily().getName();
    savedConfigState.serifFontName = di.config.getSerifFontFamily().getName();
    savedConfigState.sansSerifFontName = di.config.getSansSerifFontFamily().getName();

    savedConfigState.scrolling = di.config.isScrollingEnabled();
    savedConfigState.allowColoursFromCSS = di.config.isUseColoursFromCSS();

    savedConfigState.colorProfile = di.config.getColourProfile();
  }

  private void restoreLastReadPosition(Bundle savedInstanceState) {
    int lastPos = di.config.getLastPosition(bookMetadata.bookId);
    int lastIndex = di.config.getLastIndex(bookMetadata.bookId);

    if (savedInstanceState != null) {
      lastPos = savedInstanceState.getInt(POS_KEY, lastPos);
      lastIndex = savedInstanceState.getInt(IDX_KEY, lastIndex);
    }

    this.bookView.setFileName(bookMetadata.bookId);
    this.bookView.setPosition(lastPos);
    this.bookView.setIndex(lastIndex);
  }

  public void onWindowFocusChanged(boolean hasFocus) {
    //final SystemUiHelper systemUiHelper = getSystemUiHelper();
    //if (hasFocus) {
    //  updateFromPrefs();
    //  if (systemUiHelper != null && systemUiHelper.isShowing()) {
    //    systemUiHelper.delayHide(SystemUiHelper.SHORT_DELAY);
    //  }
    //} else {
    //  if (systemUiHelper != null) {
    //    systemUiHelper.keepScreenOff();
    //  }
    //}
  }

  public boolean onTouchEvent(MotionEvent event) {
    return bookView.onTouchEvent(event);
  }

  @Override public void onBookOpened(final Book book) {
    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    if (activity == null) {
      return;
    }

    if (bookTocEntryListener != null) {
      final Option<List<TocEntry>> optionalToc = bookView.getTableOfContents();
      tableOfContents = optionalToc.unsafeGet();
      bookTocEntryListener.onBookTableOfContentsLoaded(optionalToc);
    }

    updateFromPrefs();
  }

  @Override public void onStartRenderingText() {
    if (isAdded()) {
      final ProgressDialog progressDialog = getProgressDialog(R.string.ls_loading_text);
      progressDialog.show();
    }
  }

  @Override public void onErrorOnBookOpening() {
    dismissProgressDialog();
  }

  @Override public void onParseEntryStart(int entry) {
    if (!isAdded() || getActivity() == null) {
      return;
    }

    restoreColorProfile();

    final ProgressDialog progressDialog = getProgressDialog(R.string.ls_loading_text);
    progressDialog.show();
  }

  @Override public void onParseEntryComplete(final Resource resource) {
    final Activity activity = getActivity();
    if (activity == null) {
      return;
    }

    dismissProgressDialog();

    // Set chapter
    String currentChapter = null;
    if (resource != null && resource.getHref() != null && tableOfContents != null) {
      for (TocEntry content : tableOfContents) {
        final String contentHref = content.getHref();
        if (resource.getHref().equals(contentHref)) {
          currentChapter = content.getTitle();
          break;
        }
      }
    }

    readingTitleProgressTv.setText(currentChapter != null ? currentChapter : bookMetadata.title);
    formatPageChapterProgress();

    onReaderFragmentEvent(BookReaderEvents.READER_PARSE_ENTRY_FINISHED_EVENT);
  }

  @Override public void onProgressUpdate(int progressPercentage) {
    chapterProgressDsb.setProgress(progressPercentage);
  }

  @Override public boolean onSwipeLeft() {
    final SystemUiHelper systemUiHelper = getSystemUiHelper();
    if (systemUiHelper != null && systemUiHelper.isShowing()) {
      systemUiHelper.hide();
    }

    if (di.config.getReadingDirection() == Configuration.ReadingDirection.LEFT_TO_RIGHT) {
      pageDown();
    } else {
      pageUp();
    }

    if (bookView.isAtEnd()) {
      onReaderFragmentEvent(BookReaderEvents.READER_FINISHED_BOOK_EVENT);
    }

    return true;
  }

  @Override public boolean onSwipeRight() {
    final SystemUiHelper systemUiHelper = getSystemUiHelper();
    if (systemUiHelper != null && systemUiHelper.isShowing()) {
      systemUiHelper.hide();
    }

    if (di.config.getReadingDirection() == Configuration.ReadingDirection.LEFT_TO_RIGHT) {
      pageUp();
    } else {
      pageDown();
    }

    return true;
  }

  @Override public boolean onTapLeftEdge() {
    final SystemUiHelper systemUiHelper = getSystemUiHelper();
    if (systemUiHelper != null && systemUiHelper.isShowing()) {
      systemUiHelper.hide();
    }

    if (di.config.getReadingDirection() == Configuration.ReadingDirection.LEFT_TO_RIGHT) {
      pageUp();
    } else {
      pageDown();
    }

    return true;
  }

  @Override public boolean onTapRightEdge() {
    final SystemUiHelper systemUiHelper = getSystemUiHelper();
    if (systemUiHelper != null && systemUiHelper.isShowing()) {
      systemUiHelper.hide();
    }

    if (di.config.getReadingDirection() == Configuration.ReadingDirection.LEFT_TO_RIGHT) {
      pageDown();
    } else {
      pageUp();
    }

    if (bookView.isAtEnd()) {
      onReaderFragmentEvent(BookReaderEvents.READER_FINISHED_BOOK_EVENT);
    }

    return true;
  }

  @Override public boolean onPreSlide() {
    if (isDefinitionViewDisplayed()) {
      hideDefinitionView();
    }
    return false;
  }

  @Override public boolean onLeftEdgeSlide(int value) {
    return false;
  }

  @Override public boolean onRightEdgeSlide(int value) {
    return false;
  }

  @Override public boolean onPreScreenTap() {
    if (isDefinitionViewDisplayed()) {
      hideDefinitionView();
      return true;
    }

    return isPhotoViewerDisplayed();
  }

  @Override public void onScreenTap() {
    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    if (activity == null) {
      return;
    }
    getSystemUiHelper().toggle();
  }

  @Override public void onPageDown() {
    currentScrolledPages += 1;
    onReaderFragmentEvent(BookReaderEvents.GAMIFICATION_PAGE_DOWN_EVENT);
  }

  @Override public void onPageDownFirstPage() {
  }

  @Override public void onLastScreenPageDown() {
    currentScrolledPages += 1;
    onReaderFragmentEvent(BookReaderEvents.NAVIGATION_TO_BOOK_FINISHED_SCREEN_EVENT);
  }

  @Override public void onBookImageClicked(final Drawable drawable) {
    displayPhotoViewer((AbstractFastBitmapDrawable) drawable);
  }

  private void displayPhotoViewer(final AbstractFastBitmapDrawable drawable) {
    final FragmentActivity activity = getActivity();
    if (activity != null && !activity.isFinishing()) {
      final View imageViewContainer = activity.findViewById(R.id.photo_viewer);
      final View closeButton = activity.findViewById(R.id.photo_viewer_close_btn);
      closeButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(final View v) {
          hidePhotoViewer();
        }
      });

      final Bitmap bitmap = drawable.getBitmap();
      if (bitmap != null) {
        final ImageSource imageSource = ImageSource.cachedBitmap(bitmap);
        final SubsamplingScaleImageView imageScaleView = activity.findViewById(R.id.photo_viewer_iv);
        imageScaleView.setImage(imageSource);
        imageScaleView.setMaxScale(3);
        imageViewContainer.setVisibility(View.VISIBLE);
      }
    }
  }

  private void hidePhotoViewer() {
    final FragmentActivity activity = getActivity();
    if (activity != null) {
      final View imageViewContainer = activity.findViewById(R.id.photo_viewer);
      imageViewContainer.setVisibility(View.GONE);

      final View closeButton = activity.findViewById(R.id.photo_viewer_close_btn);
      closeButton.setOnClickListener(null);

      final SubsamplingScaleImageView imageScaleView = activity.findViewById(R.id.photo_viewer_iv);
      imageScaleView.recycle();
    }
  }

  private boolean isPhotoViewerDisplayed() {
    final FragmentActivity activity = getActivity();
    if (activity != null) {
      final View imageViewContainer = activity.findViewById(R.id.photo_viewer);
      return imageViewContainer.getVisibility() == View.VISIBLE;
    } else {
      return false;
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // BookViewListener Callbacks
  ///////////////////////////////////////////////////////////////////////////

  private boolean isDefinitionViewDisplayed() {
    return definitionView.getVisibility() == View.VISIBLE;
  }

  private void hideDefinitionView() {
    definitionView.setVisibility(View.GONE);
  }

  private ProgressDialog getProgressDialog(@StringRes int message) {
    if (this.progressDialog == null) {
      this.progressDialog = new ProgressDialog(context);
      this.progressDialog.setOwnerActivity(getActivity());
      this.progressDialog.setCancelable(false);
      this.progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
        @Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
          return true;
        }
      });
      this.progressDialog.setMessage(getString(message));
    }

    return this.progressDialog;
  }

  private void setShareFlag() {
    hasSharedText = true;
  }

  private void showDefinitionView() {
    definitionView.setVisibility(View.VISIBLE);
  }

  public boolean dispatchKeyEvent(KeyEvent event) {
    int action = event.getAction();
    int keyCode = event.getKeyCode();

    Log.d(TAG, "Got key event: " + keyCode + " with action " + action);

    switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_RIGHT:
        if (action == KeyEvent.ACTION_DOWN) {
          pageDown();
        }
        return true;

      case KeyEvent.KEYCODE_DPAD_LEFT:
        if (action == KeyEvent.ACTION_DOWN) {
          pageUp();
        }
        return true;

      case KeyEvent.KEYCODE_BACK:
        if (action == KeyEvent.ACTION_DOWN) {
          if (isDefinitionViewDisplayed()) {
            hideDefinitionView();
            return true;
          }

          if (isPhotoViewerDisplayed()) {
            hidePhotoViewer();
            return true;
          }

          getActivity().finish();
          return true;
        } else {
          return false;
        }
    }

    Log.d(TAG, "Not handling key event: returning false.");
    return false;
  }

  private void pageDown() {
    if (bookView.isAtEnd()) {
      bookView.lastPageDown();
      return;
    }

    bookView.pageDown();
    formatPageChapterProgress();
  }

  private void pageUp() {
    if (bookView.isAtStart()) {
      return;
    }

    bookView.pageUp();
    formatPageChapterProgress();
  }

  public void onNavigateToTocEntry(TocEntry tocEntry) {
    this.bookView.navigateTo(tocEntry);
  }

  private void formatPageChapterProgress() {
    final int pagesForResource = bookView.getPagesForResource();
    final int currentPage = bookView.getCurrentPage();
    chapterProgressPagesTv.setText(pagesForResource < currentPage ? "" : String.format("%s / %s", currentPage, pagesForResource));
    onReaderFragmentEvent(BookReaderEvents.READER_FORMATTED_PAGE_CHAPTER_EVENT);
  }

  public interface OnBookTocEntryListener {

    void onBookTableOfContentsLoaded(Option<List<TocEntry>> book);

    void displayBookTableOfContents();
  }

  private class ReaderTextSelectionCallback implements TextSelectionCallback {

    @Override public void lookupDictionary(String text) {
      if (di.reachability.isReachable()) {
        if (text != null) {
          text = text.trim();

          final StringTokenizer st = new StringTokenizer(text);
          if (st.countTokens() == 1) {
            definitionView.showLoading();
            showDefinitionView();
            final ListenableFuture<WordDefinition> getWordDefinitionFuture = di.getWordDefinitionInteractor.execute(text);
            AndroidFutures.addCallbackMainThread(getWordDefinitionFuture, new FutureCallback<WordDefinition>() {
              @Override public void onSuccess(@Nullable WordDefinition result) {
                if (isAdded()) {
                  definitionView.setWordDefinition(result);
                  definitionView.showDefinition();
                }
              }

              @Override public void onFailure(@NonNull Throwable t) {
                // Ignore error
              }
            });
          } else {
            Toast.makeText(getContext(), R.string.ls_book_reading_select_one_word, Toast.LENGTH_SHORT).show();
          }
        }
      } else {
        final MaterialDialog networkErrorDialog =
            DialogFactory.createDialog(getContext(), R.string.ls_error_signup_network_title, R.string.ls_error_definition_not_internet,
                R.string.ls_generic_accept, DialogFactory.EMPTY, new DialogFactory.ActionCallback() {
                  @Override public void onResponse(MaterialDialog dialog, final DialogFactory.Action action) {
                  }
                });

        networkErrorDialog.setCancelable(false);
        networkErrorDialog.show();
      }
    }

    @Override public void share(String selectedText) {
      final String text = TextUtils.isEmpty(bookMetadata.author) ? bookMetadata.title + " \n\n" + selectedText
                                                                 : bookMetadata.title + ", " + bookMetadata.author + "\n\n" + selectedText;

      final Intent sendIntent = new Intent();
      sendIntent.setAction(Intent.ACTION_SEND);
      sendIntent.putExtra(Intent.EXTRA_TEXT, text);
      sendIntent.setType("text/plain");

      startActivity(Intent.createChooser(sendIntent, getString(R.string.ls_generic_share)));
      setShareFlag();
    }
  }

  private class ReaderActionModeListener implements ActionModeListener {

    @Override public void onCreateActionMode() {
      final SystemUiHelper uiHelper = getSystemUiHelper();
      if (uiHelper != null && !uiHelper.isShowing()) {
        uiHelper.show();
      }
    }

    @Override public void onDestroyActionMode() {
      final SystemUiHelper uiHelper = getSystemUiHelper();
      if (uiHelper != null && uiHelper.isShowing()) {
        uiHelper.hide();
      }
    }
  }

  private static class SavedConfigState {

    private boolean stripWhiteSpace;
    private String fontName;
    private String serifFontName;
    private String sansSerifFontName;
    private boolean usePageNum;
    private int vMargin;
    private int hMargin;
    private int textSize;
    private boolean scrolling;
    private boolean allowColoursFromCSS;
    private Configuration.ColorProfile colorProfile;
  }

  public static class DICompanion {

    @Inject public StreamingBookDataSource streamingBookDataSource;
    @Inject public GetWordDefinitionInteractor getWordDefinitionInteractor;

    @Inject public Configuration config;
    @Inject public BrightnessManager brightnessManager;
    @Inject public Dates dateUtils;
    @Inject public Reachability reachability;
    @Inject public Logger logger;
  }

  public static class BookReaderEvents {

    public static final int GAMIFICATION_INITIALIZE_EVENT = 0;
    public static final int GAMIFICATION_START_BOOK_FROM_COLLECTION_EVENT = 1;
    public static final int GAMIFICATION_SHARED_QUOTE_EVENT = 2;
    public static final int GAMIFICATION_READ_ON_X_CONTINOUS_DAYS_EVENT = 3;
    public static final int GAMIFICATION_FONT_SIZE_CHANGED_EVENT = 4;
    public static final int GAMIFICATION_BACKGROUND_CHANGED_EVENT = 5;
    public static final int GAMIFICATION_TEXT_TO_SPEECH_ACTIVATED_EVENT = 6;
    public static final int GAMIFICATION_PAGE_DOWN_EVENT = 7;
    public static final int NAVIGATION_TO_BOOK_FINISHED_SCREEN_EVENT = 8;
    public static final int NAVIGATION_TO_GOALS_SCREEN_EVENT = 9;
    public static final int NAVIGATION_TO_SIGNUP_SCREEN_EVENT = 10;
    public static final int READ_PAGE_ANALYTICS_EVENT = 11;
    public static final int SAVE_CURRENTLY_BOOK_READING_EVENT = 12;
    public static final int READER_PARSE_ENTRY_FINISHED_EVENT = 13;
    public static final int READER_FINISHED_BOOK_EVENT = 14;
    public static final int READER_FORMATTED_PAGE_CHAPTER_EVENT = 15;
  }

  @IntDef({
      BookReaderEvents.GAMIFICATION_INITIALIZE_EVENT, BookReaderEvents.GAMIFICATION_START_BOOK_FROM_COLLECTION_EVENT,
      BookReaderEvents.GAMIFICATION_SHARED_QUOTE_EVENT, BookReaderEvents.GAMIFICATION_READ_ON_X_CONTINOUS_DAYS_EVENT,
      BookReaderEvents.GAMIFICATION_FONT_SIZE_CHANGED_EVENT, BookReaderEvents.GAMIFICATION_BACKGROUND_CHANGED_EVENT,
      BookReaderEvents.GAMIFICATION_TEXT_TO_SPEECH_ACTIVATED_EVENT, BookReaderEvents.GAMIFICATION_PAGE_DOWN_EVENT,
      BookReaderEvents.NAVIGATION_TO_BOOK_FINISHED_SCREEN_EVENT, BookReaderEvents.NAVIGATION_TO_GOALS_SCREEN_EVENT,
      BookReaderEvents.NAVIGATION_TO_SIGNUP_SCREEN_EVENT, BookReaderEvents.READ_PAGE_ANALYTICS_EVENT, BookReaderEvents.SAVE_CURRENTLY_BOOK_READING_EVENT,
      BookReaderEvents.READER_PARSE_ENTRY_FINISHED_EVENT, BookReaderEvents.READER_FINISHED_BOOK_EVENT, BookReaderEvents.READER_FORMATTED_PAGE_CHAPTER_EVENT
  }) @Retention(RetentionPolicy.SOURCE) @interface BookReaderFragmentEvent {

  }
}

