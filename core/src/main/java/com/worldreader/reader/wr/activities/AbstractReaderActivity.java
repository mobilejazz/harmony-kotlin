package com.worldreader.reader.wr.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.worldreader.core.R;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.TocEntry;
import com.worldreader.reader.wr.fragments.AbstractReaderFragment;
import com.worldreader.reader.wr.fragments.BookTocFragment;
import com.worldreader.reader.wr.helper.systemUi.SystemUiHelper;
import jedi.option.Option;

import java.lang.reflect.Constructor;
import java.util.*;

public abstract class AbstractReaderActivity extends AppCompatActivity
    implements AbstractReaderFragment.OnBookTocEntryListener, BookTocFragment.BookIndexListener {

  public static final String READING_FRAGMENT_CLASS_KEY = "reading.fragment.class.key";
  public static final String BOOK_METADATA_KEY = "book.metadata.key";

  private SystemUiHelper systemUiHelper;
  private AbstractReaderFragment abstractReaderFragment;
  private BookTocFragment bookTocFragment;

  private AppBarLayout toolbarLayout;
  private Toolbar toolbar;
  private View readingContainer;
  private View bookIndexContainer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    onSetupSystemUiHelper();
    setContentView(R.layout.activity_reader);
    onPostCreate();
  }

  private void onPostCreate() {
    final boolean isCorrect = onCheckIfIntentProvidedIsGood();
    if (isCorrect) {
      onSetupInjector();
      onSetupActionBar();
      onSetupReadingFragment();
      onSetupBookIndexFragment();
      onSetupBindViews();
    } else {
      Toast.makeText(this, R.string.ls_error_fatal_error_message, Toast.LENGTH_SHORT).show();
      finish();
    }
  }

  private boolean onCheckIfIntentProvidedIsGood() {
    final Intent intent = getIntent();
    return intent != null && intent.hasExtra(AbstractReaderActivity.BOOK_METADATA_KEY) && intent.hasExtra(AbstractReaderActivity.READING_FRAGMENT_CLASS_KEY);
  }

  protected void onSetupInjector() {
  }

  private void onSetupActionBar() {
    this.toolbarLayout = findViewById(R.id.appbar_layout);
    this.toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Obtain size of the status bar and add it as margin to toolbar
    int height = 0;
    final int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      height = getResources().getDimensionPixelSize(resourceId);
    }

    final CoordinatorLayout.LayoutParams params = ((CoordinatorLayout.LayoutParams) toolbarLayout.getLayoutParams());
    params.setMargins(params.leftMargin, height, params.rightMargin, params.bottomMargin);
  }

  private void onSetupSystemUiHelper() {
    this.systemUiHelper =
        new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE, SystemUiHelper.FLAG_IMMERSIVE_STICKY, new SystemUiHelper.OnVisibilityChangeListener() {
          @Override public void onVisibilityChange(boolean visible) {
            if (abstractReaderFragment != null && isVisibleReadingFragment()) {
              abstractReaderFragment.onVisibilityChange(visible);
            }
          }
        });
  }

  private void onSetupReadingFragment() {
    final Intent intent = getIntent();
    final String fragmentClass = intent.getStringExtra(READING_FRAGMENT_CLASS_KEY);
    try {
      final Class<?> clazz = Class.forName(fragmentClass);
      final Constructor<?> constructor = clazz.getConstructor();
      final AbstractReaderFragment fragment = ((AbstractReaderFragment) constructor.newInstance());
      final FragmentManager fm = getSupportFragmentManager();
      fm.beginTransaction().replace(R.id.fragment_reading, fragment).commitNow();
    } catch (Exception e) {
      throw new RuntimeException("Problem with instantiation for the fragment specified!", e);
    }
  }

  private void onSetupBookIndexFragment() {
    final Fragment fragment = new BookTocFragment();
    final FragmentManager fm = getSupportFragmentManager();
    fm.beginTransaction().replace(R.id.fragment_book_index, fragment, BookTocFragment.TAG).commitNow();
  }

  private void onSetupBindViews() {
    final FragmentManager fm = getSupportFragmentManager();
    abstractReaderFragment = (AbstractReaderFragment) fm.findFragmentById(R.id.fragment_reading);
    bookTocFragment = (BookTocFragment) fm.findFragmentById(R.id.fragment_book_index);

    this.readingContainer = findViewById(R.id.fragment_reading);
    this.bookIndexContainer = findViewById(R.id.content_book_index);
  }

  private boolean isVisibleReadingFragment() {
    return readingContainer != null && readingContainer.getVisibility() == View.VISIBLE;
  }

  @Override public boolean dispatchKeyEvent(KeyEvent event) {
    return (isVisibleReadingFragment() && abstractReaderFragment.dispatchKeyEvent(event)) || super.dispatchKeyEvent(event);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (isVisibleReadingFragment()) {
      abstractReaderFragment.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override public void onBackPressed() {
    if (!isVisibleReadingFragment()) {
      showBookReadingFragment();
    }
  }

  private void showBookReadingFragment() {
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("");
    }

    readingContainer.setVisibility(View.VISIBLE);
    bookIndexContainer.setVisibility(View.GONE);

    final View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    invalidateOptionsMenu();
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    return abstractReaderFragment.onTouchEvent(event);
  }

  @Override public void onWindowFocusChanged(boolean hasFocus) {
    if (isVisibleReadingFragment()) {
      abstractReaderFragment.onWindowFocusChanged(hasFocus);
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater menuInflater = getMenuInflater();
    if (isVisibleReadingFragment()) {
      menuInflater.inflate(R.menu.menu_reading, menu);
      changeActionBarColor(R.color.reader_actionbar_color, R.color.reader_statusbar_color, R.color.reader_actionbar_arrow_color);
    } else {
      changeActionBarColor(R.color.reader_book_index_actionbar_color, R.color.reader_book_index_statusbar_color,
          R.color.reader_book_index_actionbar_arrow_color);
    }
    return true;
  }

  private void changeActionBarColor(int color, int colorStatus, int arrowColor) {
    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(color)));
      actionBar.setHomeAsUpIndicator(getColoredArrow(arrowColor));
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      final Window window = getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.setStatusBarColor(ContextCompat.getColor(this, colorStatus));
    }
  }

  private Drawable getColoredArrow(int color) {
    final Drawable arrowDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_black_24dp);
    Drawable wrapped = DrawableCompat.wrap(arrowDrawable);
    if (arrowDrawable != null && wrapped != null) {
      arrowDrawable.mutate();
      DrawableCompat.setTint(wrapped, getResources().getColor(color));
    }

    return wrapped;
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    if (abstractReaderFragment == null || bookTocFragment == null) {
      return false;
    }

    if (isVisibleReadingFragment()) {
      abstractReaderFragment.onPrepareOptionsMenu(menu);
    } else {
      bookTocFragment.onPrepareOptionsMenu(menu);
    }

    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (isVisibleReadingFragment()) {
      return abstractReaderFragment.onOptionsItemSelected(item);
    } else {
      return bookTocFragment.onOptionsItemSelected(item);
    }
  }

  @Override public void onOptionsMenuClosed(Menu menu) {
    if (isVisibleReadingFragment()) {
      abstractReaderFragment.onOptionsMenuClosed(menu);
    } else {
      bookTocFragment.onOptionsMenuClosed(menu);
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  @Override public void onBookTableOfContentsLoaded(Option<List<TocEntry>> tocEntries) {
    this.bookTocFragment.onBookTableOfContentsLoaded(tocEntries);
  }

  @Override public void displayBookTableOfContents() {
    updateActionBarForTableOfContents();

    readingContainer.setVisibility(View.GONE);
    bookIndexContainer.setVisibility(View.VISIBLE);

    final Window window = getWindow();
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    final View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

    invalidateOptionsMenu();
  }

  private void updateActionBarForTableOfContents() {
    if (getSupportActionBar() != null) {
      final Spannable title = new SpannableString(getString(R.string.ls_book_reading_book_index));
      title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.font_white)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
      getSupportActionBar().setTitle(title);
    }
  }

  @Override public void onBookSectionSelected(TocEntry tocEntry) {
    showBookReadingFragment();
    abstractReaderFragment.onNavigateToTocEntry(tocEntry);
  }

  @Override public void onClickBackButton() {
    showBookReadingFragment();
  }

  public SystemUiHelper getSystemUiHelper() {
    return this.systemUiHelper;
  }

  public AppBarLayout getToolbarLayout() {
    return toolbarLayout;
  }

  public Toolbar getToolbar() {
    return toolbar;
  }
}
