package com.worldreader.reader.wr.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldreader.reader.wr.models.ReaderBookMetadata;
import com.worldreader.reader.wr.configuration.ReaderConfig;

import java.util.*;

public class ReaderBookMetadataManager {

  private static final String KEY_READER_BOOKS = "reader.metadata";

  private static final String KEY_READER_BOOK_METADATA = "KEY.READER.BOOK.METADATA";

  private final SharedPreferences sp;
  private final Gson gson;

  private Map<String, ReaderBookMetadata> metadata;

  public ReaderBookMetadataManager(Context context, Gson gson) {
    this.sp = context.getSharedPreferences(KEY_READER_BOOKS, Context.MODE_PRIVATE);
    this.gson = gson;
  }

  public void savePageOffsets(ReaderConfig rc, String bookId, List<List<Integer>> offsets) {
    load();
    final ReaderBookMetadata bm = getReaderBookMetadata(rc, bookId);
    bm.offsets = offsets;
    save();
  }

  public List<List<Integer>> retrievePageOffsets(ReaderConfig rc, String bookId) {
    load();
    final ReaderBookMetadata bm = getReaderBookMetadata(rc, bookId);
    return bm.offsets;
  }

  public void saveLastPositionRead(ReaderConfig rc, String bookId, int position) {
    load();
    final ReaderBookMetadata bm = getReaderBookMetadata(rc, bookId);
    bm.position = position;
    save();
  }

  public int retrieveLastPositionRead(ReaderConfig rc, String bookId) {
    load();
    final ReaderBookMetadata bm = getReaderBookMetadata(rc, bookId);
    return bm.position;
  }

  public void saveLastIndex(ReaderConfig rc, String bookId, int index) {
    load();
    final ReaderBookMetadata bm = getReaderBookMetadata(rc, bookId);
    bm.index = index;
    save();
  }

  public int retrieveLastIndex(ReaderConfig rc, String bookId) {
    load();
    final ReaderBookMetadata bm = getReaderBookMetadata(rc, bookId);
    return bm.index;
  }

  public void removeReaderBookMetadata(String bookId) {
    load();
    metadata.remove(bookId);
    save();
  }

  public void nuke() {
    sp.edit().clear().apply();
  }

  private void save() {
    final String json = gson.toJson(metadata);
    sp.edit().putString(KEY_READER_BOOK_METADATA, json).commit();
  }

  private void load() {
    if (metadata == null) {
      final String json = sp.getString(KEY_READER_BOOK_METADATA, "");
      if (!TextUtils.isEmpty(json)) {
        final Map<String, ReaderBookMetadata> raw = gson.fromJson(json, new TypeToken<Map<String, ReaderBookMetadata>>() {}.getType());
        if (raw != null) {
          metadata = raw;
        }
        return;
      }
      metadata = new HashMap<>();
    }
  }

  private ReaderBookMetadata getReaderBookMetadata(ReaderConfig rc, String bookId) {
    ReaderBookMetadata bm = metadata.get(bookId);
    if (bm == null) {
      bm = ReaderBookMetadata.fromValues(rc);
      metadata.put(bookId, bm);
    }
    return bm;
  }

}
