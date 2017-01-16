package com.worldreader.core.application.ui.adapter.helper;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

  public static String TAG = EndlessScrollListener.class.getSimpleName();

  private static final int DEFAULT_VISIBLE_THRESHOLD = 5;

  private int previousTotal = 0; // The total number of items in the dataset after the last load
  private boolean loading = true; // True if we are still waiting for the last set of data to load.
  private int visibleThreshold;

  private int currentPage = 1;

  private LinearLayoutManager mLinearLayoutManager;

  public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
    this(linearLayoutManager, DEFAULT_VISIBLE_THRESHOLD);
  }

  public EndlessScrollListener(LinearLayoutManager linearLayoutManager, int visibleThreshold) {
    this.mLinearLayoutManager = linearLayoutManager;
    this.visibleThreshold = visibleThreshold;
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    int visibleItemCount = recyclerView.getChildCount();
    int totalItemCount = mLinearLayoutManager.getItemCount();
    int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

    if (loading) {
      if (totalItemCount > previousTotal) {
        loading = false;
        previousTotal = totalItemCount;
      }
    }

    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
      // End has been reached
      // Do something
      currentPage++;

      onLoadMore(currentPage);

      loading = true;
    }
  }

  public void setVisibleThreshold(int visibleThreshold) {
    if (visibleThreshold < 1) {
      throw new IllegalArgumentException(
          "visibleThreshold is less than 1. Choose a higher number!");
    }
    this.visibleThreshold = visibleThreshold;
  }

  public void resetValues() {
    this.loading = false;
    this.previousTotal = 0;
  }

  public abstract void onLoadMore(int currentPage);
}
