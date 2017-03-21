package com.worldreader.reader.wr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.dto.TocEntry;

import java.util.List;

public class TocEntriesAdapter extends RecyclerView.Adapter<TocEntriesAdapter.TocEntriesHolder> {

  public interface OnClickBookSectionListener {
    void onBookSectionSelected(TocEntry tocEntry);
  }

  private OnClickBookSectionListener listener;

  private final Context context;

  private List<TocEntry> tocEntries;

  public TocEntriesAdapter(Context context) {
    this(context, null, null);
  }

  public TocEntriesAdapter(Context context, List<TocEntry> tocEntries, OnClickBookSectionListener listener) {
    this.context = context;
    this.tocEntries = tocEntries;
    this.listener = listener;
  }

  @Override public TocEntriesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(context).inflate(R.layout.toc_entries_adapter, parent, false);
    return new TocEntriesHolder(v);
  }

  @Override public void onBindViewHolder(TocEntriesHolder holder, int position) {
    final TocEntry tocEntry = tocEntries.get(position);
    holder.sectionTv.setText(tocEntry.getTitle());
    holder.sectionTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (listener != null) {
          listener.onBookSectionSelected(tocEntry);
        }
      }
    });
  }

  @Override public int getItemCount() {
    return tocEntries.size();
  }

  public static class TocEntriesHolder extends RecyclerView.ViewHolder {

    TextView sectionTv;

    public TocEntriesHolder(View itemView) {
      super(itemView);
      this.sectionTv = ((TextView) itemView.findViewById(R.id.toc_entries_adapter_section_tv));
    }

  }
}
