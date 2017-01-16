package com.worldreader.core.application.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

// source: https://gist.github.com/sebnapi/fde648c17616d9d3bcde
public abstract class HeaderRecyclerViewAdapter extends RecyclerView.Adapter {

  private static final int TYPE_HEADER = Integer.MIN_VALUE;
  private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
  private static final int TYPE_ADAPTER_OFFSET = 2;

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_HEADER) {
      return onCreateHeaderViewHolder(parent, viewType);
    } else if (viewType == TYPE_FOOTER) {
      return onCreateFooterViewHolder(parent, viewType);
    }
    return onCreateBasicItemViewHolder(parent, viewType - TYPE_ADAPTER_OFFSET);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (position == 0 && holder.getItemViewType() == TYPE_HEADER) {
      onBindHeaderView(holder, position);
    } else if (position == getBasicItemCount() && holder.getItemViewType() == TYPE_FOOTER) {
      onBindFooterView(holder, position);
    } else {
      onBindBasicItemView(holder, position - (useHeader() ? 1 : 0));
    }
  }

  @Override public int getItemCount() {
    int itemCount = getBasicItemCount();
    if (useHeader()) {
      itemCount += 1;
    }
    if (useFooter()) {
      itemCount += 1;
    }
    return itemCount;
  }

  @Override public int getItemViewType(int position) {
    if (position == 0 && useHeader()) {
      return TYPE_HEADER;
    }
    if (position == getBasicItemCount() && useFooter()) {
      return TYPE_FOOTER;
    }
    if (getBasicItemType(position) >= Integer.MAX_VALUE - TYPE_ADAPTER_OFFSET) {
      new IllegalStateException(
          "HeaderRecyclerViewAdapter offsets your BasicItemType by " + TYPE_ADAPTER_OFFSET + ".");
    }
    return getBasicItemType(position) + TYPE_ADAPTER_OFFSET;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Footer methods
  ///////////////////////////////////////////////////////////////////////////

  public abstract boolean useFooter();

  public abstract RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType);

  public abstract void onBindFooterView(RecyclerView.ViewHolder holder, int position);

  ///////////////////////////////////////////////////////////////////////////
  // Basic recycler methods
  ///////////////////////////////////////////////////////////////////////////

  public abstract RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent,
      int viewType);

  public abstract void onBindBasicItemView(RecyclerView.ViewHolder holder, int position);

  public abstract int getBasicItemCount();

  /**
   * make sure you don't use [Integer.MAX_VALUE-1, Integer.MAX_VALUE] as BasicItemViewType
   */
  public abstract int getBasicItemType(int position);

  ///////////////////////////////////////////////////////////////////////////
  // Header methods
  ///////////////////////////////////////////////////////////////////////////

  public abstract boolean useHeader();

  public abstract RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType);

  public abstract void onBindHeaderView(RecyclerView.ViewHolder holder, int position);
}
