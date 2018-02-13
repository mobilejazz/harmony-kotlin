package com.worldreader.reader.wr.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.FrameLayout;
import com.worldreader.core.R;

// TODO: Work in progress not usable by any means
public class WordDefinitionsDialogFragmentSheet extends BottomSheetDialogFragment {

  public static final String TAG = WordDefinitionsDialogFragmentSheet.class.getSimpleName();

  @Override
  public void setupDialog(final Dialog dialog, int style) {
    super.setupDialog(dialog, style);

    final View contentView = View.inflate(getContext(), R.layout.fragment_definition_view, null);
    dialog.setContentView(contentView);
    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface d) {
        final FrameLayout bottomSheet = dialog.getWindow().findViewById(android.support.design.R.id.design_bottom_sheet);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) bottomSheet.getParent();
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(392);

        coordinatorLayout.getParent().requestLayout();
      }
    });

    final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
    final CoordinatorLayout.Behavior behavior = params.getBehavior();

    if (behavior != null && behavior instanceof BottomSheetBehavior) {
      //((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
      ((BottomSheetBehavior) behavior).setPeekHeight(392);
    }
  }

}
