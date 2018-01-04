package com.worldreader.reader.wr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.core.application.ui.adapter.EnumSpinnerAdapter;

public class ReaderFontFamiliesAdapter extends EnumSpinnerAdapter<ReaderFontFamilies> {

  public ReaderFontFamiliesAdapter(Context context) {
    super(context, ReaderFontFamilies.class);
  }

  @Override public View getView(int position, View cv, ViewGroup parent) {
    final ReaderFontFamilies fontFamily = getItem(position);

    final TextView textView = cv == null ? (TextView) LayoutInflater.from(context).inflate(R.layout.reader_enum_adapter, parent, false) : (TextView) cv;
    textView.setText(fontFamily.getStringId());

    return textView;
  }

  @Override public int getPosition(ReaderFontFamilies element) {
    return enumConstants.indexOf(element);
  }
}
