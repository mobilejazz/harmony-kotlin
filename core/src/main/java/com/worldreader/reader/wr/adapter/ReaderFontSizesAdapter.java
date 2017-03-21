package com.worldreader.reader.wr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.core.application.ui.adapter.EnumSpinnerAdapter;

public class ReaderFontSizesAdapter extends EnumSpinnerAdapter<ReaderFontSizes> {

  public ReaderFontSizesAdapter(Context context) {
    super(context, ReaderFontSizes.class);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView;
    ReaderFontSizes fontsize = getItem(position);

    if (convertView == null) {
      textView =
          (TextView) LayoutInflater.from(context).inflate(R.layout.reader_enum_adapter, parent, false);
    } else {
      textView = (TextView) convertView;
    }

    textView.setText(fontsize.getStringId());

    return textView;
  }

  @Override public int getPosition(ReaderFontSizes element) {
    return enumConstants.indexOf(element);
  }
}
