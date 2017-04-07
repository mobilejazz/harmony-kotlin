package com.worldreader.reader.wr.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.R;
import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.BasicAnalyticsEvent;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.application.helper.ui.Dimens;
import com.worldreader.core.application.ui.dialog.DialogFactory;
import com.worldreader.core.application.ui.widget.TutorialView;
import com.worldreader.core.application.ui.widget.discretebar.DiscreteSeekBar;
import com.worldreader.core.common.date.Dates;
import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.datasource.StreamingBookDataSource;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.interactors.book.GetBookDetailInteractor;
import com.worldreader.core.domain.interactors.book.SaveBookCurrentlyReadingInteractor;
import com.worldreader.core.domain.interactors.dictionary.GetWordDefinitionInteractor;
import com.worldreader.core.domain.interactors.user.FinishBookInteractor;
import com.worldreader.core.domain.interactors.user.SendPageReadsInteractor;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.model.WordDefinition;
import com.worldreader.core.domain.thread.MainThread;
import com.worldreader.core.userflow.UserFlowTutorial;
import com.worldreader.core.userflow.model.TutorialModel;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Author;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.animation.Animations;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.animation.Animator;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.animation.PageCurlAnimator;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.animation.PageTimer;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.animation.RollingBlindAnimator;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.AnimationStyle;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.ColorProfile;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.ReadingDirection;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.ScrollStyle;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.dto.TocEntry;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.helper.TextUtil;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.tts.SpeechCompletedCallback;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.tts.TTSFailedException;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.tts.TTSPlaybackItem;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.tts.TTSPlaybackQueue;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.AnimatedImageView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.NavGestureDetector;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.ActionModeListener;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.BookViewListener;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.SelectedWord;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.StreamingTextLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.TextLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.TextSelectionCallback;
import com.worldreader.reader.wr.activities.AbstractReaderActivity;
import com.worldreader.reader.wr.helper.BrightnessManager;
import com.worldreader.reader.wr.helper.StreamingResourcesLoader;
import com.worldreader.reader.wr.helper.systemUi.SystemUiHelper;
import com.worldreader.reader.wr.widget.DefinitionView;
import jedi.functional.Command;
import jedi.functional.Functor;
import jedi.option.Option;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.spans.CenterSpan;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.regex.*;

import static jedi.functional.FunctionalPrimitives.firstOption;
import static jedi.functional.FunctionalPrimitives.isEmpty;
import static jedi.option.Options.none;
import static jedi.option.Options.option;

public abstract class AbstractReaderFragment extends Fragment
    implements BookViewListener, TextSelectionCallback, ActionModeListener, SystemUiHelper.OnVisibilityChangeListener {

  public static final String CHANGE_FONT_KEY = "change_font_key";
  public static final String CHANGE_BACKGROUND_KEY = "change.background.key";

  private static final String TAG = AbstractReaderFragment.class.getSimpleName();
  private static final String POS_KEY = "offset:";
  private static final String IDX_KEY = "index:";
  private static final String DEFAULT_BOOK_LANGUAGE = "en";

  private Context context;
  private TelephonyManager telephonyManager;
  private AudioManager audioManager;
  private boolean ttsAvailable;
  private TextToSpeech textToSpeech;
  private TTSPlaybackQueue ttsPlaybackItemQueue;
  private Map<String, TTSPlaybackItem> ttsItemPrep = new HashMap<>();
  private TextLoader textLoader;
  private ProgressDialog waitDialog;
  private String bookTitle;
  private String titleBase;
  private String author;
  private String language = DEFAULT_BOOK_LANGUAGE;
  private int currentPageNumber = -1;
  private List<TocEntry> tableOfContents;
  private boolean hasSharedText;
  private SavedConfigState savedConfigState = new SavedConfigState();
  private SelectedWord selectedWord = null;
  private Handler uiHandler;
  private OnBookTocEntryListener bookTocEntryListener;

  private ViewSwitcher viewSwitcher;
  private BookView bookView;
  private TextView wordView;
  private AnimatedImageView dummyView;
  private TextView pageNumberView;
  private LinearLayout mediaLayout;
  private ImageButton playPauseButton;
  private DiscreteSeekBar mediaProgressBar;

  private ImageButton stopButton;
  private ImageButton nextButton;
  private ImageButton prevButton;
  private TextView readingTitleProgressTv;
  private DiscreteSeekBar chapterProgressDsb;
  private TextView chapterProgressPagesTv;
  private DefinitionView definitionView;
  private RelativeLayout mainContainerRl;
  private TutorialView tutorialView;
  private View containerTutorialView;
  private View progressContainer;
  private Runnable mediaPlayerSeekBarUpdaterRunnable = new Runnable() {
    private boolean pausedBecauseOfCall = false;

    public void run() {
      if (!ttsIsRunning()) {
        return;
      }

      long delay = 1000;

      synchronized (ttsPlaybackItemQueue) {
        TTSPlaybackItem item = ttsPlaybackItemQueue.peek();

        if (item != null) {
          MediaPlayer mediaPlayer = item.getMediaPlayer();
          int phoneState = telephonyManager.getCallState();

          if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            if (phoneState == TelephonyManager.CALL_STATE_RINGING || phoneState == TelephonyManager.CALL_STATE_OFFHOOK) {
              Log.d(TAG, "Detected call, pausing TTS.");
              mediaPlayer.pause();
              this.pausedBecauseOfCall = true;
            } else {
              double percentage = (double) mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration();

              mediaProgressBar.setMax(mediaPlayer.getDuration());
              mediaProgressBar.setProgress(mediaPlayer.getCurrentPosition());

              int currentDuration = item.getOffset() + (int) (percentage * item.getText().length());

              bookView.navigateTo(bookView.getIndex(), currentDuration);

              wordView.setText(item.getText());

              delay = 100;
            }
          } else if (mediaPlayer != null && phoneState == TelephonyManager.CALL_STATE_IDLE && pausedBecauseOfCall) {
            Log.d(TAG, "Call over, resuming TTS.");

            //We reset to the start of the current section before resuming playback.
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            pausedBecauseOfCall = false;
            delay = 100;
          }
        }
      }

      // Running this thread after 100 milliseconds
      uiHandler.postDelayed(this, delay);
    }
  };

  protected Configuration config;
  protected StreamingBookDataSource streamingBookDataSource;
  protected Logger logger;
  protected GetWordDefinitionInteractor getWordDefinitionInteractor;
  protected FinishBookInteractor finishBookInteractor;
  protected SendPageReadsInteractor sendPageReadsInteractor;
  protected MainThread mainThread;
  protected UserFlowTutorial userFlowTutorial;
  protected BrightnessManager brightnessManager;
  protected SaveBookCurrentlyReadingInteractor saveBookCurrentlyReadingInteractor;
  protected GetBookDetailInteractor getBookDetailInteractor;
  protected Dates dateUtils;
  protected Reachability reachability;
  protected int currentScrolledPages = 0;
  protected BookMetadata bookMetadata;
  protected Analytics analytics;

  @Override public void onVisibilityChange(boolean visible) {
    progressContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    onFragmentActivityResult(requestCode, resultCode, data);
  }

  protected abstract void onFragmentActivityResult(final int requestCode, final int resultCode, final Intent data);

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    try {
      bookTocEntryListener = (OnBookTocEntryListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    onInitializeInjectors();

    this.context = getActivity();
    this.telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
    this.audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);

    this.ttsPlaybackItemQueue = new TTSPlaybackQueue();

    this.textLoader = StreamingTextLoader.getInstance();
    this.textLoader.setHtmlSpanner(new HtmlSpanner());

    //this.actionModeBuilder = new ActionModeBuilder();

    // Restore preferences
    this.uiHandler = new Handler();
    HandlerThread bgThread = new HandlerThread("background");
    bgThread.start();

    // Load metadata
    Intent intent = getActivity().getIntent();
    if (intent != null) {
      bookMetadata = (BookMetadata) intent.getSerializableExtra(AbstractReaderActivity.BOOK_METADATA_KEY);
      onGamificationInitialize();

      if (bookMetadata.getCollectionId() > 0) {
        onGamificationEventStartBookFromCollection();
      }

      final boolean isFontChanged = intent.getBooleanExtra(CHANGE_FONT_KEY, false);
      final boolean isBackgroundChanged = intent.getBooleanExtra(CHANGE_BACKGROUND_KEY, false);
      executeGamification(isFontChanged, isBackgroundChanged);

      onGamificationEventReadOnXContinousDays();

      // Save the book that the user is currently reading
      getBookDetailInteractor.execute(bookMetadata.getBookId(), new DomainCallback<com.worldreader.core.domain.model.Book, ErrorCore<?>>(mainThread) {
        @Override public void onSuccessResult(com.worldreader.core.domain.model.Book book) {
          saveBookCurrentlyReadingInteractor.execute(book, new DomainCallback<Boolean, ErrorCore<?>>(mainThread) {
            @Override public void onSuccessResult(Boolean aBoolean) {
              // Nothing to do
            }

            @Override public void onErrorResult(ErrorCore<?> errorCore) {
              // Nothing to do
            }
          });
        }

        @Override public void onErrorResult(ErrorCore<?> errorCore) {

        }
      });
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_reader, container, false);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.viewSwitcher = (ViewSwitcher) view.findViewById(R.id.reading_fragment_main_container);
    this.bookView = (BookView) view.findViewById(R.id.reading_fragment_bookView);
    this.wordView = (TextView) view.findViewById(R.id.reading_fragment_word_view);
    this.dummyView = (AnimatedImageView) view.findViewById(R.id.reading_fragment_dummy_view);
    this.pageNumberView = (TextView) view.findViewById(R.id.reading_fragment_page_number_view);
    this.mediaLayout = (LinearLayout) view.findViewById(R.id.reading_fragment_media_player_layout);
    this.playPauseButton = (ImageButton) view.findViewById(R.id.reading_fragment_play_pause_button);
    this.mediaProgressBar = (DiscreteSeekBar) view.findViewById(R.id.reading_fragment_media_progress);

    this.stopButton = (ImageButton) view.findViewById(R.id.reading_fragment_stop_button);
    this.nextButton = (ImageButton) view.findViewById(R.id.reading_fragment_next_button);
    this.prevButton = (ImageButton) view.findViewById(R.id.reading_fragment_prev_button);
    this.readingTitleProgressTv = (TextView) view.findViewById(R.id.reading_fragment_progress_chapter_title_tv);
    this.chapterProgressDsb = (DiscreteSeekBar) view.findViewById(R.id.reading_fragment_chapter_progress_dsb);
    this.chapterProgressPagesTv = (TextView) view.findViewById(R.id.reading_fragment_chapter_progress_pages_tv);
    this.definitionView = (DefinitionView) view.findViewById(R.id.reading_fragment_word_definition_dv);
    this.mainContainerRl = (RelativeLayout) view.findViewById(R.id.reading_fragment_parent_container);
    this.tutorialView = (TutorialView) view.findViewById(R.id.reading_fragment_tutorial_view);
    this.containerTutorialView = view.findViewById(R.id.reading_fragment_container_tutorial_view);
    this.progressContainer = view.findViewById(R.id.reading_fragment_chapter_progress_container);

    this.bookView.init(bookMetadata.getContentOpfName(), bookMetadata.getTocResource(),
        new StreamingResourcesLoader(bookMetadata, this.streamingBookDataSource, this.logger));

    this.bookView.addListener(this);
    this.bookView.setTextSelectionCallback(this, this);

    this.mediaProgressBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
      @Override public void onProgressChanged(DiscreteSeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          seekToPointInPlayback(progress);
        }
      }

      @Override public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

      }
    });

    this.textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
      @Override public void onInit(int status) {
        AbstractReaderFragment.this.onTextToSpeechInit(status);
      }
    });

    // Hide the tutorial view because we need to wait that the book is loaded
    setTutorialViewVisibility(View.INVISIBLE);

    this.definitionView.setOnClickCrossListener(new DefinitionView.OnClickCrossListener() {
      @Override public void onClick(DefinitionView view) {
        hideDefinitionView();
      }
    });

    this.progressContainer.setPadding(progressContainer.getPaddingLeft(), progressContainer.getPaddingTop(), progressContainer.getPaddingRight(),
        progressContainer.getPaddingBottom() + Dimens.obtainNavBarHeight(context));

    this.chapterProgressDsb.setEnabled(true);
    this.chapterProgressDsb.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {

      private int seekValue;

      @Override public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        this.seekValue = value;
      }

      @Override public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        bookView.navigateToPercentageInChapter(this.seekValue);
        formatPageChapterProgress();
      }
    });
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    AppCompatActivity activity = (AppCompatActivity) getActivity();

    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    final GestureDetector gestureDetector = new GestureDetector(context, new NavGestureDetector(bookView, this, metrics));

    displayPageNumber(-1); // Initializes the pagenumber view properly

    View.OnTouchListener gestureListener = new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        return !AbstractReaderFragment.this.ttsIsRunning() && gestureDetector.onTouchEvent(event);
      }
    };

    this.viewSwitcher.setOnTouchListener(gestureListener);
    this.bookView.setOnTouchListener(gestureListener);
    this.dummyView.setOnTouchListener(gestureListener);

    registerForContextMenu(bookView);
    saveConfigState();
    updateFromPrefs();
    updateFileName(savedInstanceState);

    bookView.restore();

    if (ttsIsRunning()) {
      this.mediaLayout.setVisibility(View.VISIBLE);
      this.ttsPlaybackItemQueue.updateSpeechCompletedCallbacks(new SpeechCompletedCallback() {
        @Override public void speechCompleted(TTSPlaybackItem item, MediaPlayer mediaPlayer) {
          AbstractReaderFragment.this.speechCompleted(item, mediaPlayer);
        }
      });
      uiHandler.post(mediaPlayerSeekBarUpdaterRunnable);
    }
  }

  @Override public void onResume() {
    super.onResume();
    checkIfHasBeenSharedQuote();
  }

  private void checkIfHasBeenSharedQuote() {
    if (hasSharedText) {
      hasSharedText = false;
      uiHandler.postDelayed(new Runnable() {
        @Override public void run() {
          onGamificationEventSharedQuote();
        }
      }, 500);
    }
  }

  protected abstract void onGamificationEventSharedQuote();

  @Override public void onSaveInstanceState(final Bundle outState) {
    if (this.bookView != null) {
      outState.putInt(POS_KEY, this.bookView.getProgressPosition());
      outState.putInt(IDX_KEY, this.bookView.getIndex());
    }
  }

  @Override public void onPause() {
    Log.d(TAG, "onPause() called.");
    saveReadingPosition();
    notifyReadPages();
    super.onPause();
  }

  @Override public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop() called.");
    closeWaitDialog();
  }

  @Override public void onLowMemory() {
    super.onLowMemory();
    this.textLoader.clearCachedText();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    this.textToSpeech.shutdown();
    this.closeWaitDialog();
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    AppCompatActivity activity = (AppCompatActivity) getActivity();

    if (activity == null) {
      return;
    }

    MenuItem tts = menu.findItem(R.id.text_to_speech);
    tts.setEnabled(ttsAvailable);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.show_book_content) {
      bookTocEntryListener.displayBookTableOfContents();
      return true;
    } else if (itemId == R.id.display_options) {
      final ModifyReaderSettingsDialog d = new ModifyReaderSettingsDialog();
      d.setBrightnessManager(brightnessManager);
      d.setConfiguration(config);
      d.setOnModifyReaderSettingsListener(new ModifyReaderSettingsDialog.ModifyReaderSettingsListener() {
        @Override public void onReaderSettingsModified(ModifyReaderSettingsDialog.Action action) {
          switch (action) {
            case MODIFIED:
              updateFromPrefs();
              break;
          }
        }
      });
      final FragmentManager fm = getFragmentManager();
      d.show(fm, ModifyReaderSettingsDialog.TAG);
      return true;
    } else if (itemId == R.id.text_to_speech) {
      startTextToSpeech();
      onGamificationEventTextToSpeechActivated();
      return true;
    } else if (itemId == android.R.id.home) {
      getActivity().finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onOptionsMenuClosed(Menu menu) {
    updateFromPrefs();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void updateFromPrefs() {
    AppCompatActivity activity = (AppCompatActivity) getActivity();

    if (activity == null) {
      return;
    }

    bookView.setTextSize(config.getTextSize());

    int marginH = config.getHorizontalMargin();
    int marginV = config.getVerticalMargin();

    this.bookView.setFontFamily(config.getSerifFontFamily());

    this.textLoader.setFontFamily(config.getSerifFontFamily());
    this.textLoader.setSansSerifFontFamily(config.getSansSerifFontFamily());
    this.textLoader.setSerifFontFamily(config.getSerifFontFamily());

    bookView.setHorizontalMargin(marginH);
    bookView.setVerticalMargin(marginV);

    if (!isAnimating()) {
      bookView.setEnableScrolling(config.isScrollingEnabled());
    }

    textLoader.setStripWhiteSpace(config.isStripWhiteSpaceEnabled());
    textLoader.setAllowStyling(config.isAllowStyling());
    textLoader.setUseColoursFromCSS(config.isUseColoursFromCSS());

    bookView.setLineSpacing(config.getLineSpacing());

    activity.getSupportActionBar().setHomeButtonEnabled(true);
    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    activity.getSupportActionBar().setTitle("");

    if (config.isDimSystemUI()) {
      activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    if (config.isKeepScreenOn()) {
      if (getSystemUiHelper() != null) {
        getSystemUiHelper().keepScreenOn();
      }
    } else {
      if (getSystemUiHelper() != null) {
        getSystemUiHelper().keepScreenOff();
      }
    }

    restoreColorProfile();

    boolean isFontChanged = !config.getSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.serifFontName);
    boolean isBackgroundChanged = config.getColourProfile() != savedConfigState.colorProfile;

    // Check if we need a restart
    if (config.isShowPageNumbers() != savedConfigState.usePageNum
        || config.isStripWhiteSpaceEnabled() != savedConfigState.stripWhiteSpace
        || !config.getDefaultFontFamily().getName().equalsIgnoreCase(savedConfigState.fontName)
        || isFontChanged
        || !config.getSansSerifFontFamily().getName().equalsIgnoreCase(savedConfigState.sansSerifFontName)
        || config.getHorizontalMargin() != savedConfigState.hMargin
        || config.getVerticalMargin() != savedConfigState.vMargin
        || config.getTextSize() != savedConfigState.textSize
        || config.isScrollingEnabled() != savedConfigState.scrolling
        || config.isAllowStyling() != savedConfigState.allowStyling
        || config.isUseColoursFromCSS() != savedConfigState.allowColoursFromCSS) {

      textLoader.invalidateCachedText();

      restartActivity(isFontChanged, isBackgroundChanged);
    }

    com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Orientation orientation = config.getScreenOrientation();

    switch (orientation) {
      case PORTRAIT:
      default:
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        break;
    }
  }

  private boolean isAnimating() {
    Animator anim = dummyView.getAnimator();
    return anim != null && !anim.isFinished();
  }

  private SystemUiHelper getSystemUiHelper() {
    if (getActivity() != null) {
      return ((AbstractReaderActivity) getActivity()).getSystemUiHelper();
    }
    return null;
  }

  private void restoreColorProfile() {
    this.bookView.setBackgroundColor(config.getBackgroundColor());
    this.viewSwitcher.setBackgroundColor(config.getBackgroundColor());

    this.bookView.setTextColor(config.getTextColor());
    this.bookView.setLinkColor(config.getLinkColor());

    int brightness = config.getBrightness();
    brightnessManager.setBrightness(getActivity().getWindow(), brightness);
  }

  private void restartActivity(boolean isChangedFont, boolean isBackgroundModified) {
    onStop();

    //Clear any cached text.
    textLoader.closeCurrentBook();

    final Intent intent = getActivity().getIntent();
    intent.putExtra(AbstractReaderActivity.BOOK_METADATA_KEY, this.bookMetadata);
    intent.putExtra(CHANGE_FONT_KEY, isChangedFont);
    intent.putExtra(CHANGE_BACKGROUND_KEY, isBackgroundModified);
    startActivity(intent);

    AppCompatActivity activity = (AppCompatActivity) getActivity();

    if (activity != null) {
      activity.finish();
    }
  }

  private void closeWaitDialog() {
    if (waitDialog != null) {
      this.waitDialog.dismiss();
      this.waitDialog = null;
    }
  }

  public void saveReadingPosition() {
    if (this.bookView != null) {
      int index = this.bookView.getIndex();
      int position = this.bookView.getProgressPosition();

      if (index != -1 && position != -1 && !bookView.isAtEnd()) {
        config.setLastPosition(this.bookMetadata.getBookId(), position);
        config.setLastIndex(this.bookMetadata.getBookId(), index);
      } else if (bookView.isAtEnd()) {
        config.setLastPosition(this.bookMetadata.getBookId(), -1);
        config.setLastIndex(this.bookMetadata.getBookId(), -1);
      }
    }
  }

  private void notifyReadPages() {
    if (currentScrolledPages > 0) {
      final String bookId = bookMetadata.getBookId();
      final Date now = dateUtils.now();
      try {
        sendPageReadsInteractor.execute(bookId, currentScrolledPages, now);
      } catch (Exception e) {
        // Ignore
      }
      onNotifyReadPagesAnalytics();

      // Restart counter
      currentScrolledPages = 0;
    }
  }

  protected abstract void onNotifyReadPagesAnalytics();

  protected abstract void onInitializeInjectors();

  protected abstract void onGamificationInitialize();

  protected abstract void onGamificationEventStartBookFromCollection();

  private void executeGamification(final boolean isFontChanged, final boolean isBackgroundChanged) {
    uiHandler.postDelayed(new Runnable() {
      @Override public void run() {
        if (isFontChanged) {
          onGamificationEventFontSizeChanged();
        } else if (isBackgroundChanged) {
          onGamificationEventBackgroundChanged();
        }
      }
    }, 500);
  }

  protected abstract void onGamificationEventReadOnXContinousDays();

  protected abstract void onGamificationEventFontSizeChanged();

  protected abstract void onGamificationEventBackgroundChanged();

  protected abstract void onGamificationEventTextToSpeechActivated();

  private void seekToPointInPlayback(int position) {
    TTSPlaybackItem item = this.ttsPlaybackItemQueue.peek();

    if (item != null) {
      item.getMediaPlayer().seekTo(position);
    }
  }

  public void onMediaButtonEvent(int buttonId) {
    if (buttonId == R.id.reading_fragment_play_pause_button && !ttsIsRunning()) {
      startTextToSpeech();
      playPauseButton.setImageResource(R.drawable.ic_reader_pause);
      return;
    }

    TTSPlaybackItem item = this.ttsPlaybackItemQueue.peek();

    if (item == null) {
      stopTextToSpeech();
      playPauseButton.setImageResource(R.drawable.ic_reader_play);
      return;
    }

    MediaPlayer mediaPlayer = item.getMediaPlayer();
    uiHandler.removeCallbacks(mediaPlayerSeekBarUpdaterRunnable);

    if (buttonId == R.id.reading_fragment_stop_button) {
      stopTextToSpeech();
    } else if (buttonId == R.id.reading_fragment_next_button) {
      performSkip(true);
      uiHandler.post(mediaPlayerSeekBarUpdaterRunnable);
    } else if (buttonId == R.id.reading_fragment_prev_button) {
      performSkip(false);
      uiHandler.post(mediaPlayerSeekBarUpdaterRunnable);
    } else if (buttonId == R.id.reading_fragment_play_pause_button) {
      if (mediaPlayer.isPlaying()) {
        playPauseButton.setImageResource(R.drawable.ic_reader_play);
        mediaPlayer.pause();
      } else {
        playPauseButton.setImageResource(R.drawable.ic_reader_pause);
        mediaPlayer.start();
        uiHandler.post(mediaPlayerSeekBarUpdaterRunnable);
      }
    }
  }

  private void performSkip(boolean toEnd) {
    if (!ttsIsRunning()) {
      return;
    }

    TTSPlaybackItem item = this.ttsPlaybackItemQueue.peek();

    if (item != null) {
      MediaPlayer player = item.getMediaPlayer();

      if (toEnd) {
        player.seekTo(player.getDuration());
      } else {
        player.seekTo(0);
      }
    }
  }

  public void saveConfigState() {
    // Cache old settings to check if we'll need a restart later
    savedConfigState.stripWhiteSpace = config.isStripWhiteSpaceEnabled();

    savedConfigState.usePageNum = config.isShowPageNumbers();

    savedConfigState.hMargin = config.getHorizontalMargin();
    savedConfigState.vMargin = config.getVerticalMargin();

    savedConfigState.textSize = config.getTextSize();
    savedConfigState.fontName = config.getDefaultFontFamily().getName();
    savedConfigState.serifFontName = config.getSerifFontFamily().getName();
    savedConfigState.sansSerifFontName = config.getSansSerifFontFamily().getName();

    savedConfigState.scrolling = config.isScrollingEnabled();
    savedConfigState.allowStyling = config.isAllowStyling();
    savedConfigState.allowColoursFromCSS = config.isUseColoursFromCSS();

    savedConfigState.colorProfile = config.getColourProfile();
  }

  private void startTextToSpeech() {
    if (reachability.isReachable()) {
      if (audioManager.isMusicActive()) {
        return;
      }

      this.getWaitDialog().setMessage(getString(R.string.init_tts));
      this.getWaitDialog().show();

      Option<File> fosOption = config.getTTSFolder();

      if (isEmpty(fosOption)) {
        Log.e(TAG, "Could not get base folder for TTS");
        showTTSFailed("Could not get base folder for TTS");
      }

      File fos = fosOption.unsafeGet();

      if (fos.exists() && !fos.isDirectory()) {
        fos.delete();
      }

      fos.mkdirs();

      if (!(fos.exists() && fos.isDirectory())) {
        Log.e(TAG, "Failed to build folder " + fos.getAbsolutePath());
        showTTSFailed("Failed to build folder " + fos.getAbsolutePath());
        return;
      }

      saveReadingPosition();
      //Delete any old TTS files still present.
      for (File f : fos.listFiles()) {
        f.delete();
      }

      ttsItemPrep.clear();

      if (!ttsAvailable) {
        return;
      }

      this.wordView.setTextColor(config.getTextColor());
      this.wordView.setBackgroundColor(config.getBackgroundColor());

      this.ttsPlaybackItemQueue.activate();
      this.mediaLayout.setVisibility(View.VISIBLE);
      this.playPauseButton.setImageResource(R.drawable.ic_reader_pause);

      streamTTSToDisk();
    } else {
      final MaterialDialog networkErrorDialog =
          DialogFactory.createDialog(getContext(), R.string.ls_error_signup_network_title, R.string.ls_error_tts_not_internet,
              R.string.ls_generic_accept, DialogFactory.EMPTY, new DialogFactory.ActionCallback() {
                @Override public void onResponse(MaterialDialog dialog, final DialogFactory.Action action) {
                }
              });
      networkErrorDialog.setCancelable(false);
      networkErrorDialog.show();
    }
  }

  private void streamTTSToDisk() {
    new Thread(new Runnable() {
      @Override public void run() {
        AbstractReaderFragment.this.doStreamTTSToDisk();
      }
    }).start();
  }

  /**
   * Splits the text to be spoken into chunks and streams
   * them to disk. This method should NOT be called on the
   * UI thread!
   */
  private void doStreamTTSToDisk() {
    Option<Spanned> text = bookView.getStrategy().getText();

    if (isEmpty(text) || !ttsIsRunning()) {
      return;
    }

    String textToSpeak = text.map(new Functor<Spanned, String>() {
      @Override public String execute(Spanned c) {
        return c.toString().substring(bookView.getStartOfCurrentPage());
      }
    }).getOrElse("");

    List<String> parts = TextUtil.splitOnPunctuation(textToSpeak);

    int offset = bookView.getStartOfCurrentPage();

    try {

      Option<File> ttsFolderOption = config.getTTSFolder();

      if (isEmpty(ttsFolderOption)) {
        throw new TTSFailedException();
      }

      File ttsFolder = ttsFolderOption.unsafeGet();

      for (int i = 0; i < parts.size() && ttsIsRunning(); i++) {
        Log.d(TAG, "Streaming part " + i + " to disk.");

        String part = parts.get(i);

        boolean lastPart = i == parts.size() - 1;

        //Utterance ID doubles as the filename
        String pageName = "";

        try {
          File pageFile = new File(ttsFolder, "tts_" + UUID.randomUUID().getLeastSignificantBits() + ".wav");
          pageName = pageFile.getAbsolutePath();
          pageFile.createNewFile();
        } catch (IOException io) {
          String message = "Can't write to file \n" + pageName + " because of onError\n" + io.getMessage();
          Log.e(TAG, message);
          showTTSFailed(message);
        }

        streamPartToDisk(pageName, part, offset, textToSpeak.length(), lastPart);

        offset += part.length() + 1;

        Thread.yield();
      }
    } catch (TTSFailedException e) {
      //Just stop streaming
    }
  }

  private void streamPartToDisk(String fileName, String part, int offset, int totalLength, boolean endOfPage) throws TTSFailedException {

    Log.d(TAG, "Request to stream text to file " + fileName + " with text " + part);

    if (part.trim().length() > 0 || endOfPage) {

      HashMap<String, String> params = new HashMap<>();

      params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, fileName);

      TTSPlaybackItem item = new TTSPlaybackItem(part, new MediaPlayer(), totalLength, offset, endOfPage, fileName);
      ttsItemPrep.put(fileName, item);

      int result;
      String errorMessage = "";

      try {
        result = textToSpeech.synthesizeToFile(part, params, fileName);
      } catch (Exception e) {
        Log.e(TAG, "Failed to start TTS", e);
        result = TextToSpeech.ERROR;
        errorMessage = e.getMessage();
      }

      if (result != TextToSpeech.SUCCESS) {
        String message = "Can't write to file \n" + fileName + " because of onError\n" + errorMessage;
        Log.e(TAG, message);
        showTTSFailed(message);
        throw new TTSFailedException();
      }
    } else {
      Log.d(TAG, "Skipping part, since it's empty.");
    }
  }

  private void showTTSFailed(final String message) {
    uiHandler.post(new Runnable() {
      @Override public void run() {

        AbstractReaderFragment.this.stopTextToSpeech();
        AbstractReaderFragment.this.closeWaitDialog();

        if (AbstractReaderFragment.this.isAdded()) {
          StringBuilder textBuilder = new StringBuilder(AbstractReaderFragment.this.getString(R.string.tts_failed));
          textBuilder.append("\n").append(message);

          Toast.makeText(context, textBuilder.toString(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  public void onStreamingCompleted(final String wavFile) {
    Log.d(TAG, "TTS streaming completed for " + wavFile);

    if (!ttsIsRunning()) {
      this.textToSpeech.stop();
      return;
    }

    if (!ttsItemPrep.containsKey(wavFile)) {
      Log.e(TAG, "Got onStreamingCompleted for " + wavFile + " but there is no corresponding TTSPlaybackItem!");
      return;
    }

    final TTSPlaybackItem item = ttsItemPrep.remove(wavFile);

    try {

      MediaPlayer mediaPlayer = item.getMediaPlayer();
      mediaPlayer.reset();
      mediaPlayer.setDataSource(wavFile);
      mediaPlayer.prepare();

      this.ttsPlaybackItemQueue.add(item);
    } catch (Exception e) {
      Log.e(TAG, "Could not play", e);
      showTTSFailed(e.getLocalizedMessage());
      return;
    }

    this.uiHandler.post(new Runnable() {
      @Override public void run() {
        AbstractReaderFragment.this.closeWaitDialog();
      }
    });

    //If the queue is size 1, it only contains the player we just added,
    //meaning this is a first playback start.
    if (ttsPlaybackItemQueue.size() == 1) {
      startPlayback();
    }
  }

  private boolean ttsIsRunning() {
    return ttsPlaybackItemQueue.isActive();
  }

  /**
   * Called when a speech fragment has finished being played.
   */
  public void speechCompleted(TTSPlaybackItem item, MediaPlayer mediaPlayer) {
    Log.d(TAG, "Speech completed for " + item.getFileName());

    if (!ttsPlaybackItemQueue.isEmpty()) {
      this.ttsPlaybackItemQueue.remove();
    }

    if (ttsIsRunning()) {
      startPlayback();

      if (item.isLastElementOfPage()) {
        this.uiHandler.post(new Runnable() {
          @Override public void run() {
            AbstractReaderFragment.this.pageDown(Orientation.VERTICAL);
          }
        });
      }
    }

    mediaPlayer.release();
    new File(item.getFileName()).delete();
  }

  private void stopTextToSpeech() {
    this.ttsPlaybackItemQueue.deactivate();

    this.mediaLayout.setVisibility(View.GONE);

    this.textToSpeech.stop();
    this.ttsItemPrep.clear();

    saveReadingPosition();
  }

  @SuppressWarnings("deprecation") public void onTextToSpeechInit(int status) {
    this.ttsAvailable = (status == TextToSpeech.SUCCESS);

    if (this.ttsAvailable) {
      this.textToSpeech.setSpeechRate(0.7f);
      this.textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
        @Override public void onUtteranceCompleted(String wavFile) {
          AbstractReaderFragment.this.onStreamingCompleted(wavFile);
        }
      });
    } else {
      Log.i(TAG, "Failed to initialize TextToSpeech. Got status " + status);
    }
  }

  private void startPlayback() {
    Log.d(TAG, "startPlayback() - doing peek()");

    final TTSPlaybackItem item = this.ttsPlaybackItemQueue.peek();

    if (item == null) {
      Log.d(TAG, "Got null item, bailing out.");
      return;
    }

    Log.d(TAG, "Start playback for item " + item.getFileName());
    Log.d(TAG, "Text: '" + item.getText() + "'");

    if (item.getMediaPlayer().isPlaying()) {
      return;
    }

    item.setOnSpeechCompletedCallback(new SpeechCompletedCallback() {
      @Override public void speechCompleted(TTSPlaybackItem item1, MediaPlayer mediaPlayer) {
        AbstractReaderFragment.this.speechCompleted(item1, mediaPlayer);
      }
    });
    uiHandler.post(mediaPlayerSeekBarUpdaterRunnable);

    item.getMediaPlayer().start();
  }

  private void updateFileName(Bundle savedInstanceState) {
    int lastPos = config.getLastPosition(this.bookMetadata.getBookId());
    int lastIndex = config.getLastIndex(this.bookMetadata.getBookId());

    if (savedInstanceState != null) {
      lastPos = savedInstanceState.getInt(POS_KEY, lastPos);
      lastIndex = savedInstanceState.getInt(IDX_KEY, lastIndex);
    }

    this.bookView.setFileName(this.bookMetadata.getBookId());
    this.bookView.setPosition(lastPos);
    this.bookView.setIndex(lastIndex);

    //config.setLastOpenedFile(fileName);
  }

  private void restartActivity() {
    restartActivity(false, false);
  }

  public void onWindowFocusChanged(boolean hasFocus) {
    if (hasFocus) {
      updateFromPrefs();
      if (getSystemUiHelper() != null && getSystemUiHelper().isShowing()) {
        getSystemUiHelper().delayHide(SystemUiHelper.SHORT_DELAY);
      }
    } else {
      getSystemUiHelper().keepScreenOff();
    }
  }

  public boolean onTouchEvent(MotionEvent event) {
    return bookView.onTouchEvent(event);
  }

  @Override public void bookOpened(final Book book) {
    AppCompatActivity activity = (AppCompatActivity) getActivity();

    if (activity == null) {
      return;
    }

    this.bookTitle = book.getTitle();

    if (book.getMetadata() != null) {
      // Author
      Author authorsOption = firstOption(book.getMetadata().getAuthors()).unsafeGet();
      if (authorsOption != null) {
        author = authorsOption.getFirstname() + " " + authorsOption.getLastname();
      } else {
        author = context.getString(R.string.ls_book_reading_unknown_author);
      }

      // Language
      //this.language = book.getValue().getLanguage()

    } else {
      // Assuming defaults
      this.language = DEFAULT_BOOK_LANGUAGE;
    }

    this.language = this.bookView.getBook().getMetadata().getLanguage();

    Log.d(TAG, "Got language for book: " + language);

    activity.invalidateOptionsMenu();

    //if (book.getValue() != null && !book.getValue().getAuthors().isEmpty()) {
    //  Author author = book.getValue().getAuthors().get(0);
    //  this.authorField.setText(author.getFirstname() + " " + author.getLastname());
    //}

    if (bookTocEntryListener != null) {
      Option<List<TocEntry>> optionableToc = this.bookView.getTableOfContents();
      tableOfContents = optionableToc.unsafeGet();
      bookTocEntryListener.onBookTableOfContentsLoaded(optionableToc);
    }

    updateFromPrefs();
  }

  @Override public void readingFile() {
    if (isAdded()) {
      this.getWaitDialog().setMessage(getString(R.string.opening_file));
    }
  }

  @Override public void renderingText() {
    if (isAdded()) {
      this.getWaitDialog().setMessage(getString(R.string.ls_loading_text));
    }
  }

  // TODO: 13/11/15 Handle properly an onError ocurred here!
  @Override public void errorOnBookOpening(String errorMessage) {
    //Timber.e("ErrorOnBookOpening called: " + errorMessage);
    closeWaitDialog();
  }

  @Override public void parseEntryStart(int entry) {
    if (!isAdded() || getActivity() == null) {
      return;
    }

    this.viewSwitcher.clearAnimation();
    this.viewSwitcher.setBackgroundDrawable(null);
    restoreColorProfile();
    displayPageNumber(-1); //Clear page number

    ProgressDialog progressDialog = getWaitDialog();
    progressDialog.setMessage(getString(R.string.loading_wait));

    progressDialog.show();
  }

  private void displayPageNumber(int pageNumber) {
    String pageString;

    if (!config.isScrollingEnabled() && pageNumber > 0) {
      pageString = Integer.toString(pageNumber) + "\n";
    } else {
      pageString = "\n";
    }

    SpannableStringBuilder builder = new SpannableStringBuilder(pageString);
    builder.setSpan(new CenterSpan(), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    pageNumberView.setTextColor(config.getTextColor());
    pageNumberView.setTextSize(config.getTextSize());

    pageNumberView.setTypeface(config.getDefaultFontFamily().getDefaultTypeface());

    pageNumberView.setText(builder);
    pageNumberView.invalidate();
  }

  @Override public void parseEntryComplete(String name, Resource resource) {
    if (name != null && !name.equals(this.bookTitle)) {
      this.titleBase = this.bookTitle + " - " + name;
    } else {
      this.titleBase = this.bookTitle;
    }

    Activity activity = getActivity();

    if (activity != null) {
      if (this.ttsPlaybackItemQueue.isActive() && this.ttsPlaybackItemQueue.isEmpty()) {
        streamTTSToDisk();
      }

      closeWaitDialog();
    }

    // Set chapter
    String currentChapter = null;
    if (resource != null && resource.getHref() != null && tableOfContents != null) {
      for (TocEntry content : tableOfContents) {
        String contentHref = content.getHref();
        if (contentHref != null) {
          if (contentHref.equals(resource.getHref())) {
            currentChapter = content.getTitle();
            break;
          }
        }
      }
    }

    // Pages remaining for chapter
    formatPageChapterProgress();

    if (currentChapter != null) {
      readingTitleProgressTv.setText(currentChapter);
    } else {
      readingTitleProgressTv.setText(this.bookTitle);
    }

    // Tutorial feature
    userFlowTutorial.get(UserFlow.Type.READER, new CompletionCallback<List<TutorialModel>>() {
      @Override public void onSuccess(final List<TutorialModel> tutorials) {

        if (tutorials.isEmpty()) {
          tutorialView.setVisibility(View.GONE);
        } else {
          boolean isTutorial = isTutorial(tutorials);

          if (isTutorial) {
            setTutorialViewVisibility(View.VISIBLE);
            tutorialView.setTutorialListener(new TutorialView.TutorialListener() {
              @Override public void onCompleted() {
                tutorialView.setVisibility(View.GONE);
              }
            });
            tutorialView.setIsTrianglesDisabled(true);
            tutorialView.setTutorials(tutorials);
          } else {
            setTutorialViewVisibility(View.GONE);

            TutorialModel tutorialModel = tutorials.get(0);
            if (tutorialModel.getType() == TutorialModel.Type.SET_YOUR_GOALS) {
              if (getActivity() != null) {
                MaterialDialog setYourGoalsDialog = DialogFactory.createSetYourGoalsDialog(getActivity(), new DialogFactory.ActionCallback() {
                  @Override public void onResponse(MaterialDialog dialog, DialogFactory.Action action) {
                    if (action == DialogFactory.Action.OK) {
                      onEventNavigateToGoalsScreen();
                    }
                  }
                });

                if (getActivity().hasWindowFocus()) {
                  setYourGoalsDialog.show();
                }
              }
            } else if (tutorialModel.getType() == TutorialModel.Type.BECOME_WORLDREADER) {
              displayUserNotRegisteredDialog();
            }
          }
        }
      }

      @Override public void onError(ErrorCore errorCore) {
        tutorialView.setVisibility(View.GONE);
      }
    });
  }

  @Override public void progressUpdate(int progressPercentage, int pageNumber, int totalPages) {
    chapterProgressDsb.setProgress(progressPercentage);
  }

  @Override public void onWordLongPressed(int startOffset, int endOffset, CharSequence word) {
    this.selectedWord = new SelectedWord(startOffset, endOffset, word);
    Activity activity = getActivity();
    if (activity != null) {
      activity.openContextMenu(bookView);
    }
  }

  @Override public boolean onSwipeUp() {
    if (config.isVerticalSwipeEnabled()) {
      pageUp(Orientation.VERTICAL);
      return true;
    }
    return false;
  }

  @Override public boolean onSwipeDown() {
    if (config.isVerticalSwipeEnabled()) {
      pageDown(Orientation.VERTICAL);
      return true;
    }
    return false;
  }

  @Override public boolean onSwipeLeft() {
    if (config.isHorizontalSwipeEnabled()) {

      if (getSystemUiHelper() != null && getSystemUiHelper().isShowing()) {
        getSystemUiHelper().hide();
      }

      if (config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT) {
        pageDown(Orientation.HORIZONTAL);
      } else {
        pageUp(Orientation.HORIZONTAL);
      }

      if (bookView.isAtEnd()) {
        notifyFinishedBookEventInteractor();
      }

      return true;
    }

    return false;
  }

  @Override public boolean onSwipeRight() {
    if (config.isHorizontalSwipeEnabled()) {

      if (getSystemUiHelper() != null && getSystemUiHelper().isShowing()) {
        getSystemUiHelper().hide();
      }

      if (config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT) {
        pageUp(Orientation.HORIZONTAL);
      } else {
        pageDown(Orientation.HORIZONTAL);
      }

      return true;
    }

    return false;
  }

  @Override public boolean onTapLeftEdge() {
    if (config.isHorizontalTappingEnabled()) {

      if (getSystemUiHelper() != null && getSystemUiHelper().isShowing()) {
        getSystemUiHelper().hide();
      }

      if (config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT) {
        pageUp(Orientation.HORIZONTAL);
      } else {
        pageDown(Orientation.HORIZONTAL);
      }

      return true;
    }

    return false;
  }

  @Override public boolean onTapRightEdge() {
    if (config.isHorizontalTappingEnabled()) {

      if (getSystemUiHelper() != null && getSystemUiHelper().isShowing()) {
        getSystemUiHelper().hide();
      }

      if (config.getReadingDirection() == ReadingDirection.LEFT_TO_RIGHT) {
        pageDown(Orientation.HORIZONTAL);
      } else {
        pageUp(Orientation.HORIZONTAL);
      }

      if (bookView.isAtEnd()) {
        notifyFinishedBookEventInteractor();
      }

      return true;
    }

    return false;
  }

  @Override public boolean onTapTopEdge() {
    if (config.isVerticalTappingEnabled()) {
      pageUp(Orientation.VERTICAL);
      return true;
    }

    return false;
  }

  @Override public boolean onTapBottomEdge() {
    if (config.isVerticalTappingEnabled()) {
      pageDown(Orientation.VERTICAL);
      return true;
    }

    return false;
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
    return false;
  }

  @Override public void onScreenTap() {
    AppCompatActivity activity = (AppCompatActivity) getActivity();
    if (activity == null) {
      return;
    }

    stopAnimating();

    getSystemUiHelper().toggle();
  }

  @Override public void onStartCalculatePageNumbers() {
  }

  @Override public void onCalculatePageNumbersComplete() {
  }

  @Override public void onPageDown() {
    currentScrolledPages += 1;
    onGamificationEventPageDown();
  }

  protected abstract void onGamificationEventPageDown();

  @Override public void onPageDownFirstPage() {
  }

  @Override public void onLastScreenPageDown() {
    currentScrolledPages += 1;
    // Update book metadata with author and title
    bookMetadata.setTitle(bookTitle);
    bookMetadata.setAuthor(author);
    onEventNavigateToBookFinishedScreen();
  }

  protected abstract void onEventNavigateToBookFinishedScreen();

  private void stopAnimating() {

    if (dummyView.getAnimator() != null) {
      dummyView.getAnimator().stop();
      this.dummyView.setAnimator(null);
    }

    if (viewSwitcher.getCurrentView() == this.dummyView) {
      viewSwitcher.showNext();
    }

    this.pageNumberView.setVisibility(View.VISIBLE);
    bookView.setKeepScreenOn(false);
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

  private ProgressDialog getWaitDialog() {
    if (this.waitDialog == null) {
      this.waitDialog = new ProgressDialog(context);
      this.waitDialog.setOwnerActivity(getActivity());
      this.waitDialog.setCancelable(false);
      this.waitDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
        @Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
          return true;
        }
      });
    }

    return this.waitDialog;
  }

  protected abstract void onEventNavigateToGoalsScreen();

  @Override public void lookupDictionary(String text) {
    if (reachability.isReachable()) {
      if (text != null) {
        text = text.trim();
        StringTokenizer st = new StringTokenizer(text);

        if (st.countTokens() == 1) {
          definitionView.showLoading();
          showDefinitionView();
          getWordDefinitionInteractor.execute(text, new DomainCallback<WordDefinition, ErrorCore>(mainThread) {
            @Override public void onSuccessResult(WordDefinition wordDefinition) {
              definitionView.setWordDefinition(wordDefinition);
              definitionView.showDefinition();
            }

            @Override public void onErrorResult(ErrorCore errorCore) {
              // TODO: 03/12/15 Handle properly the error
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

  public void share(int from, int to, String selectedText) {
    String author = null;
    if (!bookView.getBook().getMetadata().getAuthors().isEmpty()) {
      author = bookView.getBook().getMetadata().getAuthors().get(0).toString();
    }

    String text;
    if (author == null) {
      text = bookTitle + " \n\n" + selectedText;
    } else {
      text = bookTitle + ", " + author + "\n\n" + selectedText;
    }

    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
    sendIntent.setType("text/plain");

    startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
    setShareFlag();
  }

  private void setShareFlag() {
    hasSharedText = true;
  }

  private void showDefinitionView() {
    definitionView.setVisibility(View.VISIBLE);
  }

  private String getLanguageCode() {
    if (this.language == null || this.language.equals("") || this.language.equalsIgnoreCase("und")) {
      return Locale.getDefault().getLanguage();
    }

    return this.language;
  }

  private boolean isTutorial(List<TutorialModel> tutorials) {
    boolean isTutorial = false;

    for (TutorialModel tutorialModel : tutorials) {
      if (tutorialModel.getType() == TutorialModel.Type.TUTORIAL) {
        isTutorial = true;
        break;
      }
    }
    return isTutorial;
  }

  public boolean dispatchMediaKeyEvent(KeyEvent event) {
    int action = event.getAction();
    int keyCode = event.getKeyCode();

    if (audioManager.isMusicActive() && !ttsIsRunning()) {
      return false;
    }

    switch (keyCode) {

      case KeyEvent.KEYCODE_MEDIA_PLAY:
      case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
      case KeyEvent.KEYCODE_MEDIA_PAUSE:
        return simulateButtonPress(action, R.id.reading_fragment_play_pause_button, playPauseButton);

      case KeyEvent.KEYCODE_MEDIA_STOP:
        return simulateButtonPress(action, R.id.reading_fragment_stop_button, stopButton);

      case KeyEvent.KEYCODE_MEDIA_NEXT:
        return simulateButtonPress(action, R.id.reading_fragment_next_button, nextButton);

      case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
        return simulateButtonPress(action, R.id.reading_fragment_prev_button, prevButton);
    }

    return false;
  }

  private boolean simulateButtonPress(int action, int idToSend, ImageButton buttonToClick) {
    if (action == KeyEvent.ACTION_DOWN) {
      onMediaButtonEvent(idToSend);
      buttonToClick.setPressed(true);
    } else {
      buttonToClick.setPressed(false);
    }

    buttonToClick.invalidate();
    return true;
  }

  public boolean dispatchKeyEvent(KeyEvent event) {
    int action = event.getAction();
    int keyCode = event.getKeyCode();

    Log.d(TAG, "Got key event: " + keyCode + " with action " + action);

    if (isAnimating() && action == KeyEvent.ACTION_DOWN) {
      stopAnimating();
      return true;
    }

		/*
     * Tricky bit of code here: if we are NOT running TTS,
		 * we want to be able to start it using the play/pause button.
		 *
		 * When we ARE running TTS, we'll get every media event twice:
		 * once through the receiver and once here if focused.
		 *
		 * So, we only try to read media events here if tts is running.
		 */
    if (!ttsIsRunning() && dispatchMediaKeyEvent(event)) {
      return true;
    }

    Log.d(TAG, "Key event is NOT a media key event.");

    switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_RIGHT:
        if (action == KeyEvent.ACTION_DOWN) {
          pageDown(Orientation.HORIZONTAL);
        }
        return true;

      case KeyEvent.KEYCODE_DPAD_LEFT:
        if (action == KeyEvent.ACTION_DOWN) {
          pageUp(Orientation.HORIZONTAL);
        }
        return true;

      case KeyEvent.KEYCODE_BACK:
        if (action == KeyEvent.ACTION_DOWN) {
          if (isDefinitionViewDisplayed()) {
            hideDefinitionView();
            return true;
          }

          if (ttsIsRunning()) {
            stopTextToSpeech();
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

  private void startAutoScroll() {
    if (viewSwitcher.getCurrentView() == this.dummyView) {
      viewSwitcher.showNext();
    }

    this.viewSwitcher.setInAnimation(null);
    this.viewSwitcher.setOutAnimation(null);

    bookView.setKeepScreenOn(true);

    ScrollStyle style = config.getAutoScrollStyle();

    try {
      if (style == ScrollStyle.ROLLING_BLIND) {
        prepareRollingBlind();
      } else {
        preparePageTimer();
      }

      viewSwitcher.showNext();

      uiHandler.post(new Runnable() {
        @Override public void run() {
          AbstractReaderFragment.this.doAutoScroll();
        }
      });
    } catch (IllegalStateException is) {
      Log.e(TAG, "Failed to start autoscroll: " + is.getMessage());
    }
  }

  private void doAutoScroll() {
    if (dummyView.getAnimator() == null) {
      Log.d(TAG, "BookView no longer has an animator. Aborting rolling blind.");
      stopAnimating();
    } else {

      Animator anim = dummyView.getAnimator();

      if (anim.isFinished()) {
        startAutoScroll();
      } else {
        anim.advanceOneFrame();
        dummyView.invalidate();

        uiHandler.postDelayed(new Runnable() {
          @Override public void run() {
            AbstractReaderFragment.this.doAutoScroll();
          }
        }, anim.getAnimationSpeed() * 2);
      }
    }
  }

  private void prepareRollingBlind() {
    Option<Bitmap> before = getBookViewSnapshot();

    bookView.pageDown();
    Option<Bitmap> after = getBookViewSnapshot();

    if (isEmpty(before) || isEmpty(after)) {
      throw new IllegalStateException("Could not initialize images");
    }

    final RollingBlindAnimator anim = new RollingBlindAnimator();
    anim.setAnimationSpeed(config.getScrollSpeed());

    before.forEach(new Command<Bitmap>() {
      @Override public void execute(Bitmap backgroundBitmap) {
        anim.setBackgroundBitmap(backgroundBitmap);
      }
    });
    after.forEach(new Command<Bitmap>() {
      @Override public void execute(Bitmap foregroundBitmap) {
        anim.setForegroundBitmap(foregroundBitmap);
      }
    });

    dummyView.setAnimator(anim);
  }

  private void preparePageTimer() {
    bookView.pageDown();
    Option<Bitmap> after = getBookViewSnapshot();

    if (isEmpty(after)) {
      throw new IllegalStateException("Could not initialize view");
    }

    after.forEach(new Command<Bitmap>() {
      @Override public void execute(Bitmap img) {
        PageTimer timer = new PageTimer(img, pageNumberView.getHeight());

        timer.setSpeed(config.getScrollSpeed());

        dummyView.setAnimator(timer);
      }
    });
  }

  private void doPageCurl(boolean flipRight, boolean pageDown) {

    if (isAnimating() || bookView == null) {
      return;
    }

    this.viewSwitcher.setInAnimation(null);
    this.viewSwitcher.setOutAnimation(null);

    if (viewSwitcher.getCurrentView() == this.dummyView) {
      viewSwitcher.showNext();
    }

    Option<Bitmap> before = getBookViewSnapshot();

    this.pageNumberView.setVisibility(View.GONE);

    final PageCurlAnimator animator = new PageCurlAnimator(flipRight);

    // Pagecurls should only take a few frames. When the screen gets
    // bigger, so do the frames.
    animator.SetCurlSpeed(bookView.getWidth() / 8);

    animator.setBackgroundColor(config.getBackgroundColor());

    if (pageDown) {
      bookView.pageDown();
      formatPageChapterProgress();
    } else {
      bookView.pageUp();
      formatPageChapterProgress();
    }

    Option<Bitmap> after = getBookViewSnapshot();

    //The animator knows how to handle nulls, so
    //we can use unsafeGet() here.
    if (flipRight) {
      animator.setBackgroundBitmap(after.unsafeGet());
      animator.setForegroundBitmap(before.unsafeGet());
    } else {
      animator.setBackgroundBitmap(before.unsafeGet());
      animator.setForegroundBitmap(after.unsafeGet());
    }

    dummyView.setAnimator(animator);
    this.viewSwitcher.showNext();

    uiHandler.post(new Runnable() {
      @Override public void run() {
        AbstractReaderFragment.this.doPageCurl(animator);
      }
    });

    dummyView.invalidate();
  }

  /**
   * Does the actual page-curl animation.
   *
   * This method advances the animator by 1 frame,
   * and then places itself back on the background
   * queue, passing along the same animator.
   *
   * That was the animator is moved along until it's done.
   *
   * Should be called from a background thread.
   */
  private void doPageCurl(final PageCurlAnimator animator) {
    if (animator.isFinished()) {

      if (viewSwitcher.getCurrentView() == dummyView) {
        viewSwitcher.showNext();
      }

      dummyView.setAnimator(null);
      pageNumberView.setVisibility(View.VISIBLE);
    } else {
      animator.advanceOneFrame();
      dummyView.invalidate();

      int delay = 1000 / animator.getAnimationSpeed();

      uiHandler.postDelayed(new Runnable() {
        @Override public void run() {
          AbstractReaderFragment.this.doPageCurl(animator);
        }
      }, delay);
    }
  }

  private Option<Bitmap> getBookViewSnapshot() {

    try {
      Bitmap bitmap = Bitmap.createBitmap(viewSwitcher.getWidth(), viewSwitcher.getHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);

      bookView.layout(0, 0, viewSwitcher.getWidth(), viewSwitcher.getHeight());

      bookView.draw(canvas);

      if (config.isShowPageNumbers()) {

        /**
         * FIXME: creating an intermediate bitmap here because I can't
         * figure out how to draw the pageNumberView directly on the
         * canvas and have it show up in the right place.
         */

        Bitmap pageNumberBitmap = Bitmap.createBitmap(pageNumberView.getWidth(), pageNumberView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas pageNumberCanvas = new Canvas(pageNumberBitmap);

        pageNumberView.layout(0, 0, pageNumberView.getWidth(), pageNumberView.getHeight());
        pageNumberView.draw(pageNumberCanvas);

        canvas.drawBitmap(pageNumberBitmap, 0, viewSwitcher.getHeight() - pageNumberView.getHeight(), new Paint());

        pageNumberBitmap.recycle();
      }

      return option(bitmap);
    } catch (OutOfMemoryError out) {
      viewSwitcher.setBackgroundColor(config.getBackgroundColor());
    }

    return none();
  }

  private void prepareSlide(Animation inAnim, Animation outAnim) {

    Option<Bitmap> bitmap = getBookViewSnapshot();
    /*
    TODO: is this OK?
        We don't set anything when we get None instead of Some.
        */
    bitmap.forEach(new Command<Bitmap>() {
      @Override public void execute(Bitmap bm) {
        dummyView.setImageBitmap(bm);
      }
    });

    this.pageNumberView.setVisibility(View.GONE);

    inAnim.setAnimationListener(new Animation.AnimationListener() {

      public void onAnimationStart(Animation animation) {
      }

      @Override public void onAnimationEnd(Animation animation) {
        onSlideFinished();
      }

      public void onAnimationRepeat(Animation animation) {
      }
    });

    viewSwitcher.layout(0, 0, viewSwitcher.getWidth(), viewSwitcher.getHeight());
    dummyView.layout(0, 0, viewSwitcher.getWidth(), viewSwitcher.getHeight());

    this.viewSwitcher.showNext();

    this.viewSwitcher.setInAnimation(inAnim);
    this.viewSwitcher.setOutAnimation(outAnim);
  }

  private void onSlideFinished() {
    if (currentPageNumber > 0) {
      this.pageNumberView.setVisibility(View.VISIBLE);
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // ActionModeListener events
  ///////////////////////////////////////////////////////////////////////////

  private void pageDown(Orientation o) {
    if (bookView.isAtEnd()) {
      bookView.lastPageDown();
      return;
    }

    stopAnimating();

    if (o == Orientation.HORIZONTAL) {

      AnimationStyle animH = config.getHorizontalAnim();
      ReadingDirection direction = config.getReadingDirection();

      if (animH == AnimationStyle.CURL) {
        doPageCurl(direction == ReadingDirection.LEFT_TO_RIGHT, true);
      } else if (animH == AnimationStyle.SLIDE) {

        if (direction == ReadingDirection.LEFT_TO_RIGHT) {
          prepareSlide(Animations.inFromRightAnimation(), Animations.outToLeftAnimation());
        } else {
          prepareSlide(Animations.inFromLeftAnimation(), Animations.outToRightAnimation());
        }

        viewSwitcher.showNext();
        bookView.pageDown();
        formatPageChapterProgress();
      } else {
        bookView.pageDown();
        formatPageChapterProgress();
      }
    } else {
      if (config.getVerticalAnim() == AnimationStyle.SLIDE) {
        prepareSlide(Animations.inFromBottomAnimation(), Animations.outToTopAnimation());
        viewSwitcher.showNext();
      }

      bookView.pageDown();
      formatPageChapterProgress();
    }
  }

  private void pageUp(Orientation o) {
    if (bookView.isAtStart()) {
      return;
    }

    stopAnimating();

    if (o == Orientation.HORIZONTAL) {

      AnimationStyle animH = config.getHorizontalAnim();
      ReadingDirection direction = config.getReadingDirection();

      if (animH == AnimationStyle.CURL) {
        doPageCurl(direction == ReadingDirection.RIGHT_TO_LEFT, false);
      } else if (animH == AnimationStyle.SLIDE) {
        if (direction == ReadingDirection.LEFT_TO_RIGHT) {
          prepareSlide(Animations.inFromLeftAnimation(), Animations.outToRightAnimation());
        } else {
          prepareSlide(Animations.inFromRightAnimation(), Animations.outToLeftAnimation());
        }
        viewSwitcher.showNext();
        bookView.pageUp();
        formatPageChapterProgress();
      } else {
        bookView.pageUp();
        formatPageChapterProgress();
      }
    } else {

      if (config.getVerticalAnim() == AnimationStyle.SLIDE) {
        prepareSlide(Animations.inFromTopAnimation(), Animations.outToBottomAnimation());
        viewSwitcher.showNext();
      }

      bookView.pageUp();
      formatPageChapterProgress();
    }
  }

  @Override public void onPrepareActionMode() {
  }

  ///////////////////////////////////////////////////////////////////////////
  // AbstractReaderActivity Callbacks
  ///////////////////////////////////////////////////////////////////////////

  @Override public void onCreateActionMode() {
    if (getSystemUiHelper() != null && !getSystemUiHelper().isShowing()) {
      getSystemUiHelper().show();
    }
  }

  //region Private methods

  @Override public void onDestroyActionMode() {
    if (getSystemUiHelper() != null && getSystemUiHelper().isShowing()) {
      getSystemUiHelper().hide();
    }
  }

  public void onNavigateToTocEntry(TocEntry tocEntry) {
    this.bookView.navigateTo(tocEntry);
  }

  private void setTutorialViewVisibility(int visibility) {
    tutorialView.setVisibility(visibility);
    containerTutorialView.setVisibility(visibility);
  }

  private void displayUserNotRegisteredDialog() {
    MaterialDialog dialog =
        DialogFactory.createDialog(getActivity(), R.string.ls_not_registered_dialog_title, R.string.ls_not_registered_dialog_message,
            R.string.ls_generic_accept, R.string.ls_generic_cancel, new DialogFactory.ActionCallback() {
              @Override public void onResponse(MaterialDialog dialog, DialogFactory.Action action) {
                if (action == DialogFactory.Action.OK) {
                  onEventNavigateToSignUpScreen();
                }
              }
            });

    dialog.show();
  }

  protected abstract void onEventNavigateToSignUpScreen();

  private void notifyFinishedBookEventInteractor() {
    finishBookInteractor.execute(bookMetadata.getBookId(), new DomainCallback<Boolean, ErrorCore>(mainThread) {
      @Override public void onSuccessResult(Boolean aBoolean) {
        //Timber.d("Reached end of book! Fired finish book interactor and resolved correctly!");
      }

      @Override public void onErrorResult(ErrorCore errorCore) {
        //Timber.d("Reached end of book! Some problem hapenned!");
      }
    });
  }

  private void formatPageChapterProgress() {

    /*I want to be able to track:

      Book toc size if existent (example: 12 entries in toc)
      Currently reading toc entry number (example, when reading third chapter in toc: 3)
      Chapter size in chars
      Amount of "pages" that entry is going to be divided in

      Size in chars of currently presented text (screen text size in chars)

     */
    chapterProgressPagesTv.setText(String.format("%s / %s", bookView.getCurrentPage(), bookView.getPagesForResource()));

    Option<Spanned> text = bookView.getStrategy().getText();
    Spanned spanned = text.getOrElse(new SpannableString(""));
    if(text != null) {
      final Map<String, String> amaAttributes = new HashMap<String, String>();
      //Book toc size
      amaAttributes.put(AnalyticsEventConstants.BOOK_AMOUNT_OF_TOC_ENTRIES,
          String.valueOf(bookView.getTableOfContents().getOrElse(new ArrayList<TocEntry>()).size()));
      //Book spine size
      amaAttributes.put(AnalyticsEventConstants.BOOK_SPINE_SIZE, String.valueOf(bookView.getSpineSize()));

      //Currently reading toc entry number
      amaAttributes.put(AnalyticsEventConstants.BOOK_READING_CHAPTER_IN_BOOK, String.valueOf(bookView.getIndex()));
      amaAttributes.put(AnalyticsEventConstants.BOOK_READING_CHAPTER_SIZE_IN_CHARS, String.valueOf(spanned.length()));
      amaAttributes.put(AnalyticsEventConstants.BOOK_READING_CURRENT_PAGE_IN_TOC_ENTRY, String.valueOf(bookView.getCurrentPage()));
      amaAttributes.put(AnalyticsEventConstants.BOOK_READING_AMOUNT_OF_PAGES_IN_TOC_ENTRY, String.valueOf(bookView.getPagesForResource()));
      amaAttributes.put(AnalyticsEventConstants.BOOK_READING_SCREEN_TEXT_SIZE_IN_CHARS, String.valueOf(bookView.getStrategy().getSizeChartDisplayed()));


      /*final CharSequence chartDisplayed = bookView.getStrategy().getChartDisplayed();
      final Pattern sPattern = Pattern.compile("(?:<img>)", Pattern.CASE_INSENSITIVE);
      int count = sPattern.matcher(chartDisplayed).groupCount();
      amaAttributes.put(AnalyticsEventConstants.BOOK_READING_SCREEN_AMOUNT_OF_IMAGES, String.valueOf(count));*/

      amaAttributes.put("Controller", AnalyticsEventConstants.BOOK_READ_CONTROLLER);
      amaAttributes.put("Action", AnalyticsEventConstants.BOOK_READ_ACTION);
      amaAttributes.put(AnalyticsEventConstants.BOOK_ATTRIBUTE, bookMetadata.getBookId());
      amaAttributes.put(AnalyticsEventConstants.BOOK_VERSION_ATTRIBUTE, "Latest");

      analytics.sendEvent(new BasicAnalyticsEvent("BOOK_READ", amaAttributes));

    }
    text = null;
    spanned = null;

  }

  private enum Orientation {
    HORIZONTAL, VERTICAL
  }

  public interface OnBookTocEntryListener {

    void onBookTableOfContentsLoaded(Option<List<TocEntry>> book);

    void displayBookTableOfContents();
  }

  private static class SavedConfigState {

    private boolean brightness;
    private boolean stripWhiteSpace;
    private String fontName;
    private String serifFontName;
    private String sansSerifFontName;
    private boolean usePageNum;
    private int vMargin;
    private int hMargin;
    private int textSize;
    private boolean scrolling;
    private boolean allowStyling;
    private boolean allowColoursFromCSS;
    private ColorProfile colorProfile;
  }
  //endregion
}

