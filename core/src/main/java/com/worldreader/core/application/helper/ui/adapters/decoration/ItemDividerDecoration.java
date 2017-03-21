package com.worldreader.core.application.helper.ui.adapters.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.worldreader.core.application.helper.ui.Dimens;

public class ItemDividerDecoration extends RecyclerView.ItemDecoration {

  private static final int[] ATTRS = new int[] { android.R.attr.listDivider };

  private final Context context;
  private final Drawable divider;

  public ItemDividerDecoration(Context context) {
    this.context = context.getApplicationContext();
    final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
    this.divider = styledAttributes.getDrawable(0);
    styledAttributes.recycle();
  }

  @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    final int left = parent.getPaddingLeft() + Dimens.dpToPx(context, 16);
    final int right = parent.getWidth() - parent.getPaddingRight() - Dimens.dpToPx(context, 16);

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = parent.getChildAt(i);

      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

      int top = child.getBottom() + params.bottomMargin;
      int bottom = top + divider.getIntrinsicHeight();

      divider.setBounds(left, top, right, bottom);
      divider.draw(c);
    }
  }
}
