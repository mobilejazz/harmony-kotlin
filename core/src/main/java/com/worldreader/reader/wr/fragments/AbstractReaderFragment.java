package com.worldreader.reader.wr.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.worldreader.core.R;
import com.worldreader.core.analytics.reader.ReaderAnalytics;
import com.worldreader.core.application.helper.AndroidFutures;
import com.worldreader.core.application.helper.ui.Dimens;
import com.worldreader.core.application.ui.dialog.DialogFactory;
import com.worldreader.core.application.ui.widget.CheckableImageButton;
import com.worldreader.core.application.ui.widget.TutorialView;
import com.worldreader.core.application.ui.widget.discretebar.DiscreteSeekBar;
import com.worldreader.core.domain.interactors.dictionary.GetWordDefinitionWordnikInteractor;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.WordDefinition;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.FontFamilies;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.TocEntry;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.AbstractFastBitmapDrawable;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.ActionModeListener;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookNavigationGestureDetector;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookViewListener;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.DICompanion;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.ReaderSavedConfigState;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.TextSelectionCallback;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoaderFactory;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.spanner.HtmlSpannerFactory;
import com.worldreader.reader.wr.activities.AbstractReaderActivity;
import com.worldreader.reader.wr.helper.LayoutDirectionHelper;
import com.worldreader.reader.wr.widget.DefinitionView;
import jedi.option.Option;
import me.zhanghai.android.systemuihelper.SystemUiHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

public abstract class AbstractReaderFragment extends Fragment implements BookViewListener, SystemUiHelper.OnVisibilityChangeListener {

  public static final String CHANGE_FONT_KEY = "change_font_key";
  public static final String CHANGE_BACKGROUND_KEY = "change.background.key";

  private static final String TAG = AbstractReaderFragment.class.getSimpleName();

  private static final String POS_KEY = "offset:";
  private static final String IDX_KEY = "index:";

  private final Handler handler = new Handler(Looper.getMainLooper());
  private final ReaderSavedConfigState savedConfigState = new ReaderSavedConfigState();

  private Context context;

  protected BookMetadata bookMetadata;

  private TextLoader textLoader;
  private List<TocEntry> tableOfContents;

  private OnBookTocEntryListener bookTocEntryListener;

  protected BookView bookView;
  protected TextView readingTitleProgressTv;
  protected DiscreteSeekBar chapterProgressDsb;
  protected TextView chapterProgressTv;
  protected DefinitionView definitionView;
  protected TutorialView tutorialView;
  protected View containerTutorialView;
  protected View progressContainer;
  protected TextView readerOptionsTv;
  protected View arrowLeftIv;
  protected View arrowRightIv;
  public MaterialDialog dialog;

  protected DICompanion di;

  private Menu menu;

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

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_reader, container, false);
  }

  @SuppressLint("ClickableViewAccessibility") @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final Activity activity = getActivity();
    final WindowManager windowManager = activity.getWindowManager();

    this.context = activity;

    // Inject views
    final View view = getView();
    this.bookView = view.findViewById(R.id.reading_fragment_bookView);
    this.readingTitleProgressTv = view.findViewById(R.id.title_chapter_tv);
    this.chapterProgressDsb = view.findViewById(R.id.chapter_progress_dsb);
    this.chapterProgressTv = view.findViewById(R.id.chapter_progress_pages_tv);
    this.definitionView = view.findViewById(R.id.reading_fragment_word_definition_dv);
    this.tutorialView = view.findViewById(R.id.reading_fragment_tutorial_view);
    this.containerTutorialView = view.findViewById(R.id.reading_fragment_container_tutorial_view);
    this.progressContainer = view.findViewById(R.id.reading_fragment_chapter_progress_container);
    this.readerOptionsTv = view.findViewById(R.id.reader_options_tv);

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
    this.textLoader = new TextLoader(bookMetadata.contentOpfPath, HtmlSpannerFactory.create(di.config), resourcesLoader);

    // Initialize BookView
    this.bookView.init(di, bookMetadata, resourcesLoader, textLoader);
    this.bookView.setListener(this);
    this.bookView.setTextSelectionCallback(new ReaderTextSelectionCallback(), new ReaderActionModeListener(), di.config);

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
      private int progress;

      @Override public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        progress = value;

        if (fromUser) { // User is sliding the view, so we update the number without relying on bookview
          final int total = bookView.getPagesForResource();
          final int current = (int) Math.floor(((double) value / 100)  * total) + 1;
          updateChapterProgress(current, total);
        } else {
          updateChapterProgress();
        }

        // Notify analytics
        onNotifyPageProgressAnalytics();
      }

      @Override public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        bookView.navigateToPercentageInChapter(progress);
        updateChapterProgress();
      }
    });

    // Setup default enabled font in RadioGroup
    final String family = di.config.getSerifFontFamilyString();
    int radioBtnId = 0;
    if (FontFamilies.LORA.DEFAULT.fontName.equals(family)) {
      radioBtnId = R.id.lora_rb;
    } else if (FontFamilies.OPEN_SANS.DEFAULT.fontName.equals(family)) {
      radioBtnId = R.id.open_sans_rb;
    } else if (FontFamilies.POPPINS.DEFAULT.fontName.equals(family)) {
      radioBtnId = R.id.popins_rb;
    }

    final RadioGroup fontsRg = activity.findViewById(R.id.fonts_rg);
    fontsRg.check(radioBtnId);
    fontsRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        FontFamilies.FontFamily fontFamily = null;
        if (checkedId == R.id.open_sans_rb) {
          fontFamily = FontFamilies.OPEN_SANS.DEFAULT;
        } else if (checkedId == R.id.popins_rb) {
          fontFamily = FontFamilies.POPPINS.DEFAULT;
        } else if (checkedId == R.id.lora_rb) {
          fontFamily = FontFamilies.LORA.DEFAULT;
        }
        di.config.setSerifFontFamily(fontFamily);
        updateReaderState();
      }
    });

    // Setup gravity for the font RadioButtons based on layout direction
    // For some reason system RTL support does not configure it in a proper way
    final RadioButton openSansRb = activity.findViewById(R.id.open_sans_rb);
    final RadioButton poppinsRb = activity.findViewById(R.id.popins_rb);
    final RadioButton loraRb = activity.findViewById(R.id.lora_rb);

    if (LayoutDirectionHelper.isAppLayoutRTL()) {
      openSansRb.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
      poppinsRb.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
      loraRb.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
    }

    // Setup listeners for reader font sizes
    final int textSize = di.config.getTextSize();

    final LinearLayout fontSizesContainerLl = activity.findViewById(R.id.font_sizes_container_ll);
    final int childCount = fontSizesContainerLl.getChildCount();

    // Setup fake radio group listener for font sizes
    final View.OnClickListener textSizeChangeListener = new View.OnClickListener() {
      @Override public void onClick(View v) {
        final int id = v.getId();

        for (int i = 0; i < childCount; i++) {
          final CheckedTextView child = ((android.support.v7.widget.AppCompatCheckedTextView) fontSizesContainerLl.getChildAt(i));
          final boolean checked = child.getId() == id;
          child.setChecked(checked);
          final int color = checked ? R.color.primary : R.color.font_gray;
          child.setTextColor(ContextCompat.getColor(activity, color));
        }

        final String rawSize = ((String) v.getTag());
        di.config.setTextSize(Integer.valueOf(rawSize));
        updateReaderState();
      }
    };

    for (int i = 0; i < childCount; i++) {
      final CheckedTextView child = ((android.support.v7.widget.AppCompatCheckedTextView) fontSizesContainerLl.getChildAt(i));
      final Integer viewFontSize = Integer.valueOf((String) child.getTag());
      child.setTextColor(ContextCompat.getColor(activity, viewFontSize == textSize ? R.color.primary : R.color.font_gray));
      child.setOnClickListener(textSizeChangeListener);
    }

    // Setup listeners for reader profile colors
    final DiscreteSeekBar brightnessSb = activity.findViewById(R.id.brightness_sb);
    brightnessSb.setProgress(di.config.getBrightness());
    brightnessSb.setOnProgressChangeListener(new DiscreteSeekBar.SimpleOnProgressChangeListener() {
      private int level;

      @Override public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        final Window window = getActivity().getWindow();
        di.brightnessManager.setBrightness(window, value);
        this.level = value;
      }

      @Override public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        di.config.setBrightness(level);
      }
    });

    final CheckableImageButton dayProfileBtn = activity.findViewById(R.id.day_profile_btn);
    final CheckableImageButton nightProfileBtn = activity.findViewById(R.id.night_profile_btn);
    final CheckableImageButton creamProfileBtn = activity.findViewById(R.id.cream_profile_btn);

    // Setup initial checked element for color profile
    final Configuration.ColorProfile profile = di.config.getColorProfile();
    switch (profile) {
      case DAY:
        dayProfileBtn.setChecked(true);
        break;
      case NIGHT:
        nightProfileBtn.setChecked(true);
        break;
      case CREAM:
        creamProfileBtn.setChecked(true);
        break;
    }

    // Create "fake" radio button group
    final View.OnClickListener readerProfileChangeListener = new View.OnClickListener() {
      @Override public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.day_profile_btn) {
          di.config.setColorProfile(Configuration.ColorProfile.DAY);
          dayProfileBtn.setChecked(true);
          nightProfileBtn.setChecked(false);
          creamProfileBtn.setChecked(false);
        } else if (id == R.id.night_profile_btn) {
          di.config.setColorProfile(Configuration.ColorProfile.NIGHT);
          dayProfileBtn.setChecked(false);
          nightProfileBtn.setChecked(true);
          creamProfileBtn.setChecked(false);
        } else if (id == R.id.cream_profile_btn) {
          di.config.setColorProfile(Configuration.ColorProfile.CREAM);
          dayProfileBtn.setChecked(false);
          nightProfileBtn.setChecked(false);
          creamProfileBtn.setChecked(true);
        }
        updateReaderState();
      }
    };

    dayProfileBtn.setOnClickListener(readerProfileChangeListener);
    nightProfileBtn.setOnClickListener(readerProfileChangeListener);
    creamProfileBtn.setOnClickListener(readerProfileChangeListener);

    // Setup navigation forward and backward listener
    this.arrowLeftIv = view.findViewById(R.id.arrow_left_iv);
    this.arrowRightIv = view.findViewById(R.id.arrow_right_iv);

    final View.OnClickListener prevNextClickListener = new View.OnClickListener() {
      @Override public void onClick(View v) {
        final PageTurnerSpine spine = bookView.getSpine();
        if (spine != null) {
          final int id = v.getId();
          final boolean toNextChapter = id == R.id.arrow_right_iv;
          if (toNextChapter) {
            bookView.pageDown();
          } else {
            bookView.pageUp();
          }
        }
      }
    };
    this.arrowLeftIv.setOnClickListener(prevNextClickListener);
    this.arrowRightIv.setOnClickListener(prevNextClickListener);

    // Setup listener for reader options
    readerOptionsTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        final SystemUiHelper systemUiHelper = getSystemUiHelper();
        if (systemUiHelper != null && !systemUiHelper.isShowing()) {
          systemUiHelper.show();
        }
      }
    });

    // Obtain display metrics from current display
    final DisplayMetrics metrics = new DisplayMetrics();
    windowManager.getDefaultDisplay().getMetrics(metrics);

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
    updateReaderState();

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
    saveReadingPosition();
    onReaderFragmentEvent(BookReaderEvents.READ_PAGE_ANALYTICS_EVENT);
    super.onPause();
  }

  @Override public void onStop() {
    dismissProgressDialog();
    super.onStop();
  }

  @Override public void onDestroy() {
    releaseBookViewResources();
    dismissProgressDialog();
    clearWasabiIfNecessary();
    super.onDestroy();
  }

  private void clearWasabiIfNecessary() {
    if (di.wasabiManager != null) {
      di.wasabiManager.clearTmp();
    }
  }

  @Override public void onLowMemory() {
    textLoader.clearCachedText();
    super.onLowMemory();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    onFragmentActivityResult(requestCode, resultCode, data);
  }

  @Override public void onSaveInstanceState(@NonNull final Bundle outState) {
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

    // Check if PhotoViewer is being displayed, and if it is, hide the photo viewer
    if (isPhotoViewerDisplayed()) {
      hidePhotoViewer();
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

  @Override public void onPrepareOptionsMenu(Menu menu) {
    this.menu = menu;

    // Check that activity is in good shape
    final FragmentActivity activity = getActivity();
    if (activity == null || activity.isFinishing()) {
      return;
    }

    // Obtain the views from the activity
    final View fontOptionsContainer = activity.findViewById(R.id.font_options_container);
    final View brightnessOptionsContainer = activity.findViewById(R.id.brightness_options_container);

    // Retrieve menu items
    final MenuItem fontsItem = menu.findItem(R.id.show_font_options);
    final MenuItem brightnessItem = menu.findItem(R.id.show_brightness_options);

    fontOptionsContainer.setVisibility(fontsItem.isChecked() ? View.VISIBLE : View.GONE);
    brightnessOptionsContainer.setVisibility(brightnessItem.isChecked() ? View.VISIBLE : View.GONE);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();
    final AbstractReaderActivity activity = ((AbstractReaderActivity) getActivity());

    // Before doing anything else, we have to hide the photo viewer if is being displayed
    if (isPhotoViewerDisplayed()) {
      hidePhotoViewer();
      if (itemId == android.R.id.home) { // If for some reason, the user clicks on back, don't send him to previous activity, but should not be needed
        return true;
      }
    }

    if (itemId == R.id.show_book_content) {
      menu.findItem(R.id.show_font_options).setChecked(false);
      menu.findItem(R.id.show_brightness_options).setChecked(false);
      activity.findViewById(R.id.font_options_container).setVisibility(View.GONE);
      activity.findViewById(R.id.brightness_options_container).setVisibility(View.GONE);
      bookTocEntryListener.displayBookTableOfContents();
      ReaderAnalytics.sendOpenTocEvent(di.analytics, bookMetadata.bookId, bookMetadata.title);
      return true;
    } else if (itemId == R.id.show_font_options || itemId == R.id.show_brightness_options) {
      item.setChecked(!item.isChecked());
      menu.findItem(itemId == R.id.show_font_options ? R.id.show_brightness_options : R.id.show_font_options).setChecked(false);
      if (activity != null) {
        activity.invalidateOptionsMenu();
      }
      return true;
    } else if (itemId == android.R.id.home) {
      if (activity != null) {
        activity.finish();
      }
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  protected abstract void onFragmentActivityResult(final int requestCode, final int resultCode, final Intent data);

  private void releaseBookViewResources() {
    if (bookView != null) {
      bookView.releaseResources();
    }
  }

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

  private void updateReaderState() {
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

    final Window window = getActivity().getWindow();
    final int brightness = di.config.getBrightness();
    di.brightnessManager.setBrightness(window, brightness);

    final ActionBar actionBar = activity.getSupportActionBar();
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle("");

    if (di.config.isKeepScreenOn()) {
      keepScreenOn(window);
    } else {
      keepScreenOff(window);
    }

    updateReaderColorProfile();

    // Check if we need a restart

    final boolean fontNameChanged = di.config.getDefaultFontFamily().getName().equalsIgnoreCase(savedConfigState.fontName);
    final boolean isFontChanged = !di.config.getSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.serifFontName);
    final boolean isBackgroundChanged = di.config.getColorProfile() != savedConfigState.colorProfile;
    final boolean isStripWhiteSpaceEnabled = di.config.isStripWhiteSpaceEnabled() != savedConfigState.stripWhiteSpace;
    final boolean isSansSerifFontNameEqual = di.config.getSansSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.sansSerifFontName);

    if (isStripWhiteSpaceEnabled
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

  private void keepScreenOff(Window window) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  private void keepScreenOn(Window window) {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  @Nullable private SystemUiHelper getSystemUiHelper() {
    if (getActivity() != null) {
      return ((AbstractReaderActivity) getActivity()).getSystemUiHelper();
    }
    return null;
  }

  private void updateReaderColorProfile() {
    this.bookView.setBackgroundColor(di.config.getBackgroundColor());
    this.bookView.setTextColor(di.config.getTextColor());
    this.bookView.setLinkColor(di.config.getLinkColor());

    final Configuration.ColorProfile profile = di.config.getColorProfile();
    int readerOptionsDrawableRes;
    switch (profile) {
      case DAY:
      default:
        readerOptionsDrawableRes = R.drawable.shape_button_read_options_white_background;
        break;
      case NIGHT:
        readerOptionsDrawableRes = R.drawable.shape_button_read_options_black_background;
        break;
      case CREAM:
        readerOptionsDrawableRes = R.drawable.shape_button_read_options_cream_background;
        break;
    }
    this.readerOptionsTv.setBackgroundResource(readerOptionsDrawableRes);
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

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    if (activity != null) {
      activity.finish();
    }
  }

  private void dismissProgressDialog() {
    if (dialog != null) {
      this.dialog.dismiss();
      this.dialog = null;
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

    savedConfigState.hMargin = di.config.getHorizontalMargin();
    savedConfigState.vMargin = di.config.getVerticalMargin();

    savedConfigState.textSize = di.config.getTextSize();
    savedConfigState.fontName = di.config.getDefaultFontFamily().getName();
    savedConfigState.serifFontName = di.config.getSerifFontFamily().getName();
    savedConfigState.sansSerifFontName = di.config.getSansSerifFontFamily().getName();

    savedConfigState.scrolling = di.config.isScrollingEnabled();
    savedConfigState.allowColoursFromCSS = di.config.isUseColoursFromCSS();

    savedConfigState.colorProfile = di.config.getColorProfile();
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

  public boolean onTouchEvent(MotionEvent event) {
    return bookView.onTouchEvent(event);
  }

  @Override public void onBookParsed(final Book book) {
    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    if (activity == null) {
      return;
    }

    this.bookView.setFileName(bookMetadata.bookId);

    if (bookTocEntryListener != null) {
      final Option<List<TocEntry>> optionalToc = bookView.getTableOfContents();
      tableOfContents = optionalToc.unsafeGet();
      bookTocEntryListener.onBookTableOfContentsLoaded(optionalToc);
    }

    final SystemUiHelper uiHelper = getSystemUiHelper();
    if (uiHelper != null) {
      uiHelper.delayHide(300);
    }

    updateReaderState();
  }

  @Override public void onStartRenderingText() {
    if (isAdded()) {
      final MaterialDialog progressDialog = getProgressDialog(R.string.ls_loading_text);
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

    updateReaderColorProfile();

    final MaterialDialog progressDialog = getProgressDialog(R.string.ls_loading_text);
    progressDialog.show();
  }

  @Override public void onParseEntryComplete(final Resource resource) {
    final Activity activity = getActivity();
    if (activity == null) {
      return;
    }

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

    readingTitleProgressTv.setText(TextUtils.isEmpty(currentChapter) ? currentChapter : bookMetadata.title);
    updateChapterProgress();

    dismissProgressDialog();

    onReaderFragmentEvent(BookReaderEvents.READER_PARSE_ENTRY_FINISHED_EVENT);
  }

  @Override public void onProgressUpdate(int progressPercentage) {
    // Enable/Disable chapter arrows
    final boolean atStart = bookView.isAtStart();
    arrowLeftIv.setEnabled(!atStart);

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
    showPhotoViewer((AbstractFastBitmapDrawable) drawable);
  }

  private void showPhotoViewer(final AbstractFastBitmapDrawable drawable) {
    final FragmentActivity activity = getActivity();
    final SystemUiHelper systemUiHelper = getSystemUiHelper();

    if (activity != null && !activity.isFinishing() && systemUiHelper != null) {
      // Hide always
      systemUiHelper.hide();

      final View imageViewContainer = activity.findViewById(R.id.photo_viewer);
      final View closeButton = activity.findViewById(R.id.photo_viewer_close_btn);
      final View crossButton = activity.findViewById(R.id.photo_viewer_close_cross_btn);

      final View.OnClickListener closeListener = new View.OnClickListener() {
        @Override public void onClick(final View v) {
          hidePhotoViewer();
        }
      };

      closeButton.setOnClickListener(closeListener);
      crossButton.setOnClickListener(closeListener);

      final Bitmap bitmap = drawable.getBitmap();
      if (bitmap != null) {
        final ImageSource imageSource = ImageSource.cachedBitmap(bitmap);
        final SubsamplingScaleImageView imageScaleView = activity.findViewById(R.id.photo_viewer_iv);
        imageScaleView.setImage(imageSource);
        imageScaleView.setMaxScale(3);

        imageViewContainer.setAlpha(0);
        imageViewContainer.setVisibility(View.VISIBLE);

        final ObjectAnimator animator = ViewPropertyObjectAnimator.animate(imageViewContainer)
            .alpha(1)
            .setDuration(300)
            .setInterpolator(new FastOutSlowInInterpolator())
            .get();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          animator.setAutoCancel(true);
        }
        animator.start();
      }
    }
  }

  private void hidePhotoViewer() {
    final FragmentActivity activity = getActivity();
    if (activity != null) {
      final View closeButton = activity.findViewById(R.id.photo_viewer_close_btn);
      final View crossButton = activity.findViewById(R.id.photo_viewer_close_cross_btn);
      closeButton.setOnClickListener(null);
      crossButton.setOnClickListener(null);

      final View imageViewContainer = activity.findViewById(R.id.photo_viewer);
      final ObjectAnimator animator = ViewPropertyObjectAnimator.animate(imageViewContainer)
          .alpha(0)
          .setDuration(300)
          .setInterpolator(new FastOutSlowInInterpolator())
          .addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
              imageViewContainer.setVisibility(View.GONE);
              imageViewContainer.setAlpha(1);
            }
          })
          .get();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
        animator.setAutoCancel(true);
      }
      animator.start();

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

  private MaterialDialog getProgressDialog(@StringRes int message) {
    if (this.dialog == null) {
      final MaterialDialog dialog = new MaterialDialog.Builder(context)
          .progress(true, 100)
          .cancelable(false)
          .content(getString(message))
          .build();

      MDTintHelper.setTint(dialog.getProgressBar(), ContextCompat.getColor(context, R.color.primary_dark));

      final View decorView = dialog.getWindow().getDecorView();

      final int systemUiVisibility = decorView.getSystemUiVisibility();
      decorView.setSystemUiVisibility(
          systemUiVisibility
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_LOW_PROFILE
      );

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        decorView.setSystemUiVisibility(decorView.getWindowSystemUiVisibility() | View.SYSTEM_UI_FLAG_IMMERSIVE);
      }

      this.dialog = dialog;
    }

    return this.dialog;
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
    bookView.pageDown();
    updateChapterProgress();
  }

  private void pageUp() {
    bookView.pageUp();
    updateChapterProgress();
  }

  public void onNavigateToTocEntry(TocEntry tocEntry) {
    this.bookView.navigateTo(tocEntry);
    ReaderAnalytics.sendOpenTocEntryEvent(di.analytics, bookMetadata.bookId, bookMetadata.title, tocEntry.getTitle(), tocEntry.getHref());
  }

  private void updateChapterProgress(final int current, final int total) {
    final String chapterProgress = current != 0 ? current + " / " + total : "";
    chapterProgressTv.setText(chapterProgress);
  }

  private void updateChapterProgress() {
    final int current = bookView.getCurrentPage();
    final int total = bookView.getPagesForResource();
    updateChapterProgress(current, total);
  }

  private void onNotifyPageProgressAnalytics() {
    final int pagesForResource = bookView.getPagesForResource();
    final int currentPage = bookView.getCurrentPage();

    final Option<Spanned> text = bookView.getStrategy().getText();
    final Spanned spanned = text.getOrElse(new SpannableString(""));
    final int tocSize = bookView.getTableOfContents().getOrElse(new ArrayList<TocEntry>()).size();
    final int spineSize = bookView.getSpineSize();
    final int spinePosition = bookView.getIndex();
    final int textSizeInChars = bookView.getStrategy().getSizeChartDisplayed();
    ReaderAnalytics.sendFormattedChapterEvent(
        di.analytics,
        bookMetadata.bookId,
        bookMetadata.title,
        pagesForResource,
        currentPage,
        spanned,
        tocSize,
        spineSize,
        spinePosition,
        textSizeInChars
    );
  }

  public interface OnBookTocEntryListener {

    void onBookTableOfContentsLoaded(Option<List<TocEntry>> book);

    void displayBookTableOfContents();
  }

  private class ReaderTextSelectionCallback implements TextSelectionCallback {

    @Override public void lookupDictionary(String text) {
      final boolean isLocalDictionary = !(di.getWordDefinitionInteractor instanceof GetWordDefinitionWordnikInteractor);
      final boolean isNetworkReachable = di.reachability.isReachable();
      if (isLocalDictionary || isNetworkReachable) {
        if (!TextUtils.isEmpty(text)) {
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
  }

  @IntDef({
      BookReaderEvents.GAMIFICATION_INITIALIZE_EVENT, BookReaderEvents.GAMIFICATION_START_BOOK_FROM_COLLECTION_EVENT,
      BookReaderEvents.GAMIFICATION_SHARED_QUOTE_EVENT, BookReaderEvents.GAMIFICATION_READ_ON_X_CONTINOUS_DAYS_EVENT,
      BookReaderEvents.GAMIFICATION_FONT_SIZE_CHANGED_EVENT, BookReaderEvents.GAMIFICATION_BACKGROUND_CHANGED_EVENT,
      BookReaderEvents.GAMIFICATION_TEXT_TO_SPEECH_ACTIVATED_EVENT, BookReaderEvents.GAMIFICATION_PAGE_DOWN_EVENT,
      BookReaderEvents.NAVIGATION_TO_BOOK_FINISHED_SCREEN_EVENT, BookReaderEvents.NAVIGATION_TO_GOALS_SCREEN_EVENT,
      BookReaderEvents.NAVIGATION_TO_SIGNUP_SCREEN_EVENT, BookReaderEvents.READ_PAGE_ANALYTICS_EVENT, BookReaderEvents.SAVE_CURRENTLY_BOOK_READING_EVENT,
      BookReaderEvents.READER_PARSE_ENTRY_FINISHED_EVENT, BookReaderEvents.READER_FINISHED_BOOK_EVENT
  }) @Retention(RetentionPolicy.SOURCE) @interface BookReaderFragmentEvent {

  }
}

