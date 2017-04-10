package com.worldreader.core.application.ui.adapter.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.worldreader.core.R;

public class GridDividerDecoration extends RecyclerView.ItemDecoration {

  private Drawable mDivider;
  private int mInsets;

  public GridDividerDecoration(Context context) {
    if (context != null) {
      mDivider = new ColorDrawable(context.getResources().getColor(android.R.color.transparent));
    } else {
      mDivider = new ColorDrawable(Color.TRANSPARENT);
    }

    mInsets = context.getResources().getDimensionPixelSize(R.dimen.grid_insets);
  }

  @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    drawVertical(c, parent);
    drawHorizontal(c, parent);
  }

  /** Draw dividers at each expected grid interval */
  public void drawVertical(Canvas c, RecyclerView parent) {
    if (parent.getChildCount() == 0) return;

    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();

    final View child = parent.getChildAt(0);
    if (child.getHeight() == 0) return;

    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
    int top = child.getBottom() + params.bottomMargin + mInsets;
    int bottom = top + mDivider.getIntrinsicHeight();

    final int parentBottom = parent.getHeight() - parent.getPaddingBottom();
    while (bottom < parentBottom) {
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);

      top += mInsets + params.topMargin + child.getHeight() + params.bottomMargin + mInsets;
      bottom = top + mDivider.getIntrinsicHeight();
    }
  }

  /** Draw dividers to the right of each child view */
  public void drawHorizontal(Canvas c, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int left = child.getRight() + params.rightMargin + mInsets;
      final int right = left + mDivider.getIntrinsicWidth();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    //We can supply forced insets for each item view here in the Rect
    outRect.set(mInsets, mInsets, mInsets, mInsets);
  }
}
