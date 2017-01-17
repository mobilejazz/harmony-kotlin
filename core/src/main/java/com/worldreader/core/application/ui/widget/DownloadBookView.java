package com.worldreader.core.application.ui.widget;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.worldreader.core.R;

public class DownloadBookView extends LinearLayout {

  private SwitchCompat switchButton;
  private FrameLayout containerView;

  private OnDownloadBookClickListener listener;

  public interface OnDownloadBookClickListener {

    void onClick(DownloadBookView view);
  }

  public DownloadBookView(Context context) {
    this(context, null);
  }

  public DownloadBookView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DownloadBookView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    View rootView =
        LayoutInflater.from(getContext()).inflate(R.layout.partial_downloadbook_view, this, true);

    switchButton = ((SwitchCompat) rootView.findViewById(R.id.downloadbook_view_switch));
    containerView = ((FrameLayout) rootView.findViewById(R.id.downloadbook_view_container));

    containerView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        switchButton.toggle();
        if (listener != null) {
          listener.onClick(DownloadBookView.this);
        }
      }
    });
  }

  public boolean isChecked() {
    return switchButton.isChecked();
  }

  public void setChecked(boolean value) {
    switchButton.setChecked(value);
  }

  public void setOnDownloadBookViewClickListener(OnDownloadBookClickListener listener) {
    this.listener = listener;
  }

}
