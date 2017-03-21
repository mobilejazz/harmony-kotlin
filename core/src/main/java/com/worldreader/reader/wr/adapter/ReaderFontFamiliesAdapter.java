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

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView;
    ReaderFontFamilies fontFamily = getItem(position);

    if (convertView == null) {
      textView =
          (TextView) LayoutInflater.from(context).inflate(R.layout.reader_enum_adapter, parent, false);
    } else {
      textView = (TextView) convertView;
    }

    textView.setText(fontFamily.getStringId());

    return textView;
  }

  @Override public int getPosition(ReaderFontFamilies element) {
    return enumConstants.indexOf(element);
  }
}
