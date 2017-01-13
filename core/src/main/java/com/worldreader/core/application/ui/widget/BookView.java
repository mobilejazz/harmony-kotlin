package com.worldreader.core.application.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.core.application.helper.image.ImageLoader;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.domain.model.Book;

public class BookView extends FrameLayout {

  private View container;
  private PercentageCropImageView imgBook;
  private TextView txtTitle;
  private ImageView imgOfflineBookIcon;
  private View editModeContainer;

  private Book book;
  private boolean isEditMode;
  private OnBookClickListener listener;

  public BookView(Context context) {
    super(context);
    init();

  }

  public BookView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BookView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    View rootView =
        LayoutInflater.from(getContext()).inflate(R.layout.global_book_layout, this, true);

    container = rootView.findViewById(R.id.global_book_layout_container);
    imgBook = (PercentageCropImageView) rootView.findViewById(R.id.global_book_layout_img_book);
    txtTitle = (TextView) rootView.findViewById(R.id.global_book_layout_txt_title);
    imgOfflineBookIcon = (ImageView) rootView.findViewById(R.id.global_book_layout_img_offline);
    editModeContainer = rootView.findViewById(R.id.global_book_layout_container_edit_mode);
  }

  public void setOnBookClickListener(OnBookClickListener listener) {
    this.listener = listener;
  }

  public void setBook(final Book book, final ImageLoader imageLoader,
      final Reachability reachability) {
    this.book = book;

    txtTitle.setText(book.getTitle());
    imgBook.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override public boolean onPreDraw() {
        imgBook.getViewTreeObserver().removeOnPreDrawListener(this);

        int measuredHeight = imgBook.getMeasuredHeight();
        int measuredWidth = imgBook.getMeasuredWidth();

        String cover = book.getCoverUrlWithSize(measuredWidth, measuredHeight);
        if (cover != null) {
          imgBook.setCropYCenterOffsetPct(0f);

          if (reachability.isReachable()) {
            imageLoader.load(book.getId(), cover, R.drawable.as_book_placeholder, imgBook);
          } else {
            if (book.isBookDownloaded()) {
              imageLoader.load(book.getId(), cover, R.drawable.as_offline_book_cover, imgBook);
            } else {
              imageLoader.load(R.drawable.as_offline_book_cover_inactive, imgBook);
            }
          }
        }
        return false;
      }
    });
    container.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (listener != null) {
          listener.onClick(BookView.this);
        }
      }
    });

    imgOfflineBookIcon.setVisibility(View.GONE);
    imgOfflineBookIcon.setVisibility(book.isBookDownloaded() ? View.VISIBLE : View.GONE);

    editModeContainer.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
  }

  public void setIsEditMode(boolean isEditMode) {
    this.isEditMode = isEditMode;
  }

  public Book getBook() {
    return book;
  }

  public interface OnBookClickListener {

    void onClick(BookView view);
  }
}
