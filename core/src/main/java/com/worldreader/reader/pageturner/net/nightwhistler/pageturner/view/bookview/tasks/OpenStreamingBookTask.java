package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.R;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import jedi.option.Option;
import org.javatuples.Pair;

import java.util.*;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

public class OpenStreamingBookTask extends QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> {

  private static final String TAG = OpenStreamingBookTask.class.getSimpleName();

  @SuppressLint("StaticFieldLeak") private final Context context;
  private final BookMetadata bm;
  private final TextLoader textLoader;
  private final int storedIndex;
  private final String contentOpf;
  private final String tocResourcePath;
  private final Logger logger;

  public OpenStreamingBookTask(final Context context, final BookMetadata bm, TextLoader textLoader, int storedIndex, String contentOpf, String tocResourcePath, Logger logger) {
    this.context = context.getApplicationContext();
    this.bm = bm;
    this.textLoader = textLoader;
    this.storedIndex = storedIndex;
    this.contentOpf = contentOpf;
    this.tocResourcePath = tocResourcePath;
    this.logger = logger;
  }

  @SafeVarargs @Override public final Option<Pair<Book, PageTurnerSpine>> doInBackground(Void... params) {
    try {
      final Book book = textLoader.initBook(bm.contentOpfPath, contentOpf, tocResourcePath);

      final PageTurnerSpine spine = new PageTurnerSpine(book, createBlackList(context));
      spine.navigateByIndex(storedIndex);

      return some(Pair.with(book, spine));
    } catch (Exception e) {
      logger.sendIssue(TAG, "Exception while trying to open book with id: " + bm.bookId + ". Current exception: " + Throwables.getStackTraceAsString(e));
      return none();
    }
  }

  private Map<String, String> createBlackList(Context c) {
    final Resources r = c.getResources();

    return new HashMap<String, String>() {{
      put("toc", r.getString(R.string.ls_toc));
      put("nav", r.getString(R.string.ls_toc));
      put("copy", r.getString(R.string.ls_copy));
      put("copyright", r.getString(R.string.ls_copy));
      put("title", r.getString(R.string.ls_title));
      put("dedi", r.getString(R.string.ls_dedi));
      put("dedication", r.getString(R.string.ls_dedi));
      put("epiloge", r.getString(R.string.ls_epilogue));
      put("ack", r.getString(R.string.ls_ack));
      put("acknowledgements", r.getString(R.string.ls_ack));
      put("backcover", r.getString(R.string.ls_back));
      put("back", r.getString(R.string.ls_back));
      put("bcover", r.getString(R.string.ls_back));
      put("index", r.getString(R.string.ls_index));
      put("contents", r.getString(R.string.ls_toc));
      put("credits", r.getString(R.string.ls_credits));
      put("morebyauthor", r.getString(R.string.ls_moreByAuthor));
      put("morebypublisher", r.getString(R.string.ls_moreByPublisher));
    }};
  }

}
