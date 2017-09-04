package com.worldreader.reader.wr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.worldreader.core.R;
import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.BasicAnalyticsEvent;
import com.worldreader.core.application.helper.ui.adapters.decoration.ItemDividerDecoration;
import com.worldreader.core.application.ui.widget.BookView;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.dto.TocEntry;
import com.worldreader.reader.wr.adapter.TocEntriesAdapter;
import jedi.option.Option;

import java.util.*;

import static jedi.functional.FunctionalPrimitives.isEmpty;

public class BookIndexFragment extends Fragment implements TocEntriesAdapter.OnClickBookSectionListener {

  private BookIndexListener listener;

  private View progressContainer;
  private RecyclerView bookIndexRv;
  private View emptyView;

  public interface BookIndexListener {

    void onBookSectionSelected(TocEntry tocEntry);

    void onClickBackButton();
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    try {
      listener = (BookIndexListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.book_reading_index_fragment, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    this.progressContainer = view.findViewById(R.id.progress_dialog_container);
    this.bookIndexRv = ((RecyclerView) view.findViewById(R.id.book_reading_index_fragment_book_index_rv));
    this.emptyView = view.findViewById(R.id.book_reading_index_fragment_empty_toc_tv);

    bookIndexRv.setHasFixedSize(true);
    bookIndexRv.setLayoutManager(new LinearLayoutManager(getContext()));
    bookIndexRv.addItemDecoration(new ItemDividerDecoration(getContext()));
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId) {
      case android.R.id.home:
        if (listener != null) {
          listener.onClickBackButton();
        }
        return true;
      default:
        return false;
    }
  }

  public void onBookTableOfContentsLoaded(Option<List<TocEntry>> tocEntries) {
    if (hasTableOfContents(tocEntries)) {
      fillRecyclerViewWithEntries(tocEntries);
      showBookIndexContentView();
    } else {
      showEmptyView();
    }
  }

  private boolean hasTableOfContents(Option<List<TocEntry>> tocEntries) {
    return !isEmpty(tocEntries.getOrElse(new ArrayList<TocEntry>()));
  }

  private void fillRecyclerViewWithEntries(Option<List<TocEntry>> tocEntries) {
    bookIndexRv.setAdapter(new TocEntriesAdapter(getContext(), tocEntries.getOrElse(new ArrayList<TocEntry>()), this));
  }

  private void showBookIndexContentView() {
    progressContainer.setVisibility(View.GONE);
    bookIndexRv.setVisibility(View.VISIBLE);
    emptyView.setVisibility(View.GONE);
  }

  private void showEmptyView() {
    progressContainer.setVisibility(View.GONE);
    bookIndexRv.setVisibility(View.GONE);
    emptyView.setVisibility(View.VISIBLE);
  }

  @Override public void onBookSectionSelected(TocEntry tocEntry) {
    if (listener != null) {
      listener.onBookSectionSelected(tocEntry);
    }
  }
}
