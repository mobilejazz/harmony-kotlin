package com.worldreader.core.application.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.core.application.ui.presenter.LoadingBookPresenter;
import com.worldreader.core.common.anims.Anims;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.common.intents.Intents;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.Collection;

import javax.inject.Inject;

public abstract class LoadingBookActivity extends AppCompatActivity
    implements LoadingBookPresenter.View {

  public abstract void initializeDependencies();

  public abstract void navigateToReadingAcivity(BookMetadata bookMetadata);

  public abstract void processError(ErrorCore<?> errorCore);

  private static final String KEY_BOOK = "book.key";
  private static final String KEY_COLLECTION = "collection.key";
  private static final String HOCK_READER_KEY = "hock.reader.key";
  public static final String EXTRA_COLOR_ANIM = "extra.color.anim";
  private static final int TWO_SECONDS = 2000;

  private LinearLayout loadingBookContainer;
  private TextView titleTv;

  @Inject LoadingBookPresenter presenter;

  private Handler handler = new Handler();
  private Runnable runnable = null;

  public static Intent getCallingIntent(Context context, Book book, Collection collection,
      boolean navigateToReader, Class<?> claszz) {
    return Intents.with(context, claszz)
        .putExtra(KEY_BOOK, book)
        .putExtra(KEY_COLLECTION, collection)
        .putExtra(HOCK_READER_KEY, navigateToReader)
        .build();
  }

  public static Intent getCallingIntent(Context context, Book book, Collection collection,
      boolean navigateToReader, Intent extraDataIntent, Class<?> claszz) {
    return getCallingIntent(context, book, collection, navigateToReader, claszz).putExtras(
        extraDataIntent);
  }

  public static Intent getCallingIntent(final Context context, final Book book,
      final boolean navigateToReader, final Class<?> clazz) {
    return getCallingIntent(context, book, null /*collection*/, navigateToReader, clazz);
  }

  public static Intent getCallingIntent(final Context context, final Book book,
      final boolean navigateToReader, Intent extraDataIntent, final Class<?> clazz) {
    return getCallingIntent(context, book, null /*collection*/, navigateToReader, clazz).putExtras(
        extraDataIntent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      overridePendingTransition(R.anim.no_animation, R.anim.no_animation);
    } else {
      overridePendingTransition(0, R.anim.fade_in);
    }

    super.onCreate(savedInstanceState);

    setContentView(R.layout.reading_loading_book_activity);

    initialize();
  }

  @Override protected void onStop() {
    super.onStop();

    if (runnable != null) {
      handler.removeCallbacks(runnable);
    }
  }

  @Override public void finish() {
    super.finish();
    overridePendingTransition(0, R.anim.fade_out);
  }

  private long startTime;

  //region Private methods
  private void initialize() {
    initializeInjectors();
    initializeView();
    initializePresenter();

    Book book = (Book) getIntent().getExtras().get(KEY_BOOK);
    Collection collection = (Collection) getIntent().getExtras().get(KEY_COLLECTION);
    boolean navigateDirectlyToReader = getIntent().getBooleanExtra(HOCK_READER_KEY, false);
    final int animationColor = getIntent().getExtras().getInt(EXTRA_COLOR_ANIM, R.color.yellow);

    startTime = System.nanoTime();

    titleTv.setText(book.getTitle());

    presenter.initialize(book, collection);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !navigateDirectlyToReader) {
      final ViewGroup viewGroup =
          (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
      viewGroup.getViewTreeObserver()
          .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
              viewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              Intent intent = getIntent();
              final int cx = intent.getIntExtra(Anims.CX, 0);
              final int cy = intent.getIntExtra(Anims.CY, 0);
              final int radius = intent.getIntExtra(Anims.RADIUS, 0);
              Anims.circularRevealShow(LoadingBookActivity.this, viewGroup, animationColor,
                  R.color.background, cx, cy, radius, new Anims.OnRevealAnimationListener() {
                    @Override public void onRevealHide() {
                    }

                    @Override public void onRevealShow() {
                      new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override public void run() {
                          Animation animation =
                              AnimationUtils.loadAnimation(LoadingBookActivity.this,
                                  android.R.anim.fade_in);
                          animation.setDuration(650);
                          loadingBookContainer.startAnimation(animation);
                          loadingBookContainer.setVisibility(View.VISIBLE);
                        }
                      });
                    }
                  });
            }
          });
    } else {
      loadingBookContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.background));
      loadingBookContainer.setVisibility(View.VISIBLE);
    }
  }

  private void initializePresenter() {
    presenter.attachView(this);
  }

  private void initializeInjectors() {
    initializeDependencies();
  }

  private void initializeView() {
    loadingBookContainer = (LinearLayout) findViewById(R.id.loading_book_container);
    titleTv = (TextView) findViewById(R.id.loading_book_title_tv);
  }

  //endregion

  @Override public void showError(ErrorCore errorCore) {
    processError(errorCore);
  }

  //region View methods
  @Override public void showProgressView() {
    // Nothing to do
  }

  @Override public void hideProgressView() {
    // Nothing to do
  }

  @Override public void onNotifyDisplayReader(final BookMetadata bookMetadata) {
    long endTime = System.nanoTime();
    long diff = (endTime - startTime) / 1000000;

    if (diff > TWO_SECONDS) {
      openReader(bookMetadata);
    } else {
      openReaderWithDelay(bookMetadata);
    }
  }
  //endregion

  //region Private Methods
  private void openReaderWithDelay(final BookMetadata bookMetadata) {
    runnable = new BookMetadataRunnable(bookMetadata);
    handler.postDelayed(runnable, TWO_SECONDS);
  }

  private void openReader(BookMetadata bookMetadata) {
    navigateToReadingAcivity(bookMetadata);
  }
  //endregion

  class BookMetadataRunnable implements Runnable {

    private final BookMetadata bookMetadata;

    public BookMetadataRunnable(BookMetadata bookMetadata) {
      this.bookMetadata = bookMetadata;
    }

    @Override public void run() {
      openReader(bookMetadata);
    }
  }
}
