package com.worldreader.core.application.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.worldreader.core.R;
import com.worldreader.core.application.helper.analytics.ProductList;
import com.worldreader.core.application.helper.image.ImageLoader;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.application.ui.widget.BookView;
import com.worldreader.core.domain.model.Book;

import java.util.*;

public class BooksAdapter extends HeaderRecyclerViewAdapter {

  public interface BooksAdapterClickListener {

    void onClickBook(Book book);

    void onClickDeleteBook(Book book);
  }

  private List<Book> mBooks;

  private final ImageLoader mImageLoader;
  private final Reachability reachability;

  private BooksAdapterClickListener listener;

  private boolean useFooter;
  private boolean isEditMode;
  private final boolean useHeader;
  private ProductList productList;

  public BooksAdapter(List<Book> mBooks, ImageLoader mImageLoader, Reachability reachability,
      boolean useFooter, boolean isEditMode, boolean useHeader, ProductList productList) {
    this.mBooks = mBooks;
    this.mImageLoader = mImageLoader;
    this.reachability = reachability;
    this.useFooter = useFooter;
    this.isEditMode = isEditMode;
    this.useHeader = useHeader;
    this.productList = productList;
  }

  public void setUseFooter(boolean useFooter) {
    this.useFooter = useFooter;
    notifyDataSetChanged();
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public boolean useHeader() {
    return useHeader;
  }

  @Override
  public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View rootView = inflater.inflate(R.layout.global_header_title_layout, parent, false);
    return new HeaderViewHolder(rootView);
  }

  @Override public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {
    // Nothing to do
  }

  @Override public boolean useFooter() {
    return useFooter;
  }

  @Override
  public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View rootView = inflater.inflate(R.layout.global_book_progress, parent, false);
    return new FooterViewHolder(rootView);
  }

  @Override public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {
    // Nothing to do
  }

  @Override
  public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());

    View rootView = inflater.inflate(R.layout.home_books_adapter, parent, false);

    return new BookViewHolder(rootView);
  }

  @Override public void onBindBasicItemView(final RecyclerView.ViewHolder holder, int position) {
    final Book book = mBooks.get(position);
    final BookViewHolder bookViewHolder = (BookViewHolder) holder;
    bookViewHolder.bookView.setIsEditMode(isEditMode);
    bookViewHolder.bookView.setBook(book, mImageLoader, reachability);
    bookViewHolder.bookView.setOnBookClickListener(new BookView.OnBookClickListener() {
      @Override public void onClick(BookView view) {
        if (listener != null) {
          if (!isEditMode) {
            listener.onClickBook(view.getBook());
          } else {
            listener.onClickDeleteBook(view.getBook());
          }
        }
      }
    });
  }

  @Override public int getBasicItemCount() {
    return mBooks.size();
  }

  @Override public int getBasicItemType(int position) {
    return 0;
  }

  @Nullable public Book getBook(final int position) {
    if (mBooks != null) {
      try {
        return mBooks.get(position);
      } catch (IndexOutOfBoundsException e) {
        return null;
      }
    } else {
      return null;
    }
  }

  @Nullable public ProductList getProductList() {
    return productList;
  }

  public void setProductList(ProductList productList) {
    this.productList = productList;
  }

  public void setListener(BooksAdapterClickListener listener) {
    this.listener = listener;
  }

  public void setIsEditMode(boolean isEditMode) {
    this.isEditMode = isEditMode;
    notifyDataSetChanged();
  }

  public boolean isEditMode() {
    return isEditMode;
  }

  public static class BookViewHolder extends RecyclerView.ViewHolder {

    BookView bookView;

    public BookViewHolder(View itemView) {
      super(itemView);

      this.bookView = (BookView) itemView.findViewById(R.id.home_books_adapter_view_book);
    }
  }

  public static class HeaderViewHolder extends RecyclerView.ViewHolder {

    public HeaderViewHolder(View itemView) {
      super(itemView);

      //ButterKnife.bind(this, itemView);
    }
  }

  public static class FooterViewHolder extends RecyclerView.ViewHolder {

    public FooterViewHolder(View itemView) {
      super(itemView);

      //ButterKnife.bind(this, itemView);
    }
  }
}
