package com.worldreader.core.application.ui.adapter;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import java.util.Arrays;
import java.util.List;

public abstract class EnumSpinnerAdapter<E extends Enum<E>>
    implements SpinnerAdapter {

  private final DataSetObservable dataSetObservable = new DataSetObservable();

  protected final Context context;
  protected final List<E> enumConstants;

  public EnumSpinnerAdapter(Context context, Class<E> enumType) {
    this.context = context;
    this.enumConstants = Arrays.asList(enumType.getEnumConstants());
  }

  @Override public int getCount() {
    return enumConstants.size();
  }

  @Override public boolean isEmpty() {
    return enumConstants.isEmpty();
  }

  @Override public E getItem(int position) {
    return enumConstants.get(position);
  }

  @Override public boolean hasStableIds() {
    return true;
  }

  @Override public int getViewTypeCount() {
    return 1;
  }

  @Override public void registerDataSetObserver(DataSetObserver observer) {
    dataSetObservable.registerObserver(observer);
  }

  @Override public void unregisterDataSetObserver(DataSetObserver observer) {
    dataSetObservable.unregisterObserver(observer);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemViewType(int position) {
    return 0;
  }

  @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getView(position, convertView, parent);
  }

  public abstract int getPosition(E element);

}
