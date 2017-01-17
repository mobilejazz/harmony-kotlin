package com.worldreader.core.application.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.core.domain.model.Category;

import java.util.*;

/**
 * As {@link android.support.v7.widget.GridLayoutManager} does not support using WRAP_CONTENT,
 * we have to deal creating rows of Categories.
 */
public class BookDetailCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface Listener {

    void onClickCategory(BookDetailCategoriesAdapter adapter, Category category);
  }

  private final Context context;
  private final List<Category> categories;

  private ColorFilter blueFilter;
  private Listener listener;

  public BookDetailCategoriesAdapter(Context context, List<Category> categories) {
    this.context = context;
    this.categories = categories;

    int blueRGB = Color.rgb(16, 62, 153);
    blueFilter = new LightingColorFilter(blueRGB, blueRGB);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View rootView = inflater.inflate(R.layout.detail_book_categories_adapter_2, parent, false);
    return new CategoriesViewHolder(rootView);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Category category1 = categories.get(position);
    Category category2 = null;
    Category category3 = null;

    try {
      category2 = categories.get(position + 1);
      category3 = categories.get(position + 2);
    } catch (IndexOutOfBoundsException e) {
      // Ignore it
    }

    onBindCategoryHolder((CategoriesViewHolder) holder, category1, category2, category3);
  }

  @Override public int getItemCount() {
    if (categories.size() == 0) {
      return 0;
    } else if (categories.size() % 3 == 0) {
      return categories.size() / 3;
    } else {
      return Math.abs(categories.size() / 3) + 1;
    }
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  //region Private methods
  private void onBindCategory(final View view, final Category category) {
    TextView txtTitle = (TextView) view.findViewById(R.id.home_categories_adapter_txt_title);

    txtTitle.setText(category.getTitle());
    txtTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

    view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

    ImageView imgIcon = (ImageView) view.findViewById(R.id.home_categories_adapter_img_icon);

    Drawable iconDrawable = ContextCompat.getDrawable(context, category.getIconRes());
    iconDrawable.setColorFilter(blueFilter);
    imgIcon.setImageDrawable(iconDrawable);
  }

  private void onBindCategoryHolder(final CategoriesViewHolder holder, final Category categoryOne,
      final Category categoryTwo, final Category categoryThree) {

    holder.categoryContainerFirst.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (listener != null) {
          listener.onClickCategory(BookDetailCategoriesAdapter.this, categoryOne);
        }
      }
    });

    // Category one
    onBindCategory(holder.categoryContainerFirst, categoryOne);

    // Category two
    if (categoryTwo != null) {
      onBindCategory(holder.categoryContainerSecond, categoryTwo);
      holder.categoryContainerSecond.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (listener != null) {
            listener.onClickCategory(BookDetailCategoriesAdapter.this, categoryTwo);
          }
        }
      });
    } else {
      holder.categoryContainerSecond.setVisibility(View.INVISIBLE);
    }

    // Category three
    if (categoryThree != null) {
      onBindCategory(holder.categoryContainerThird, categoryThree);
      holder.categoryContainerThird.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (listener != null) {
            listener.onClickCategory(BookDetailCategoriesAdapter.this, categoryThree);
          }
        }
      });
    } else {
      holder.categoryContainerThird.setVisibility(View.INVISIBLE);
    }
  }
  //endregion

  private static class CategoriesViewHolder extends RecyclerView.ViewHolder {

    View categoryContainerFirst;
    View categoryContainerSecond;
    View categoryContainerThird;

    CategoriesViewHolder(View itemView) {
      super(itemView);

      categoryContainerFirst = itemView.findViewById(R.id.detail_book_category_first);
      categoryContainerSecond = itemView.findViewById(R.id.detail_book_category_second);
      categoryContainerThird = itemView.findViewById(R.id.detail_book_category_third);

    }
  }
}
