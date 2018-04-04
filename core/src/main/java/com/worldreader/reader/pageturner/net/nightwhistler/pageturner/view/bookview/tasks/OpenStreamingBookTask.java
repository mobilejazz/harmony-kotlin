package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import android.content.Context;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.wr.models.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import jedi.option.Option;
import org.javatuples.Pair;

import java.lang.ref.WeakReference;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

public class OpenStreamingBookTask extends QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> {

  private static final String TAG = OpenStreamingBookTask.class.getSimpleName();

  private final WeakReference<Context> context;
  private final BookMetadata bm;
  private final TextLoader textLoader;
  private final int storedIndex;
  private final String contentOpf;
  private final String tocResourcePath;
  private final Logger logger;

  OpenStreamingBookTask(final Context c, final BookMetadata bm, TextLoader tl, int storedIndex, String contentOpf, String tocResourcePath, Logger l) {
    this.context = new WeakReference<>(c);
    this.bm = bm;
    this.textLoader = tl;
    this.storedIndex = storedIndex;
    this.contentOpf = contentOpf;
    this.tocResourcePath = tocResourcePath;
    this.logger = l;
  }

  @SafeVarargs @Override public final Option<Pair<Book, PageTurnerSpine>> doInBackground(Void... params) {
    try {
      final Book book = textLoader.initBook(bm.contentOpfPath, contentOpf, tocResourcePath);

      final Context context = this.context.get();
      if (context == null) {
        return none();
      }

      final PageTurnerSpine spine = new PageTurnerSpine(context, book);
      spine.navigateByIndex(storedIndex);

      return some(Pair.with(book, spine));
    } catch (Exception e) {
      logger.sendIssue(TAG, "Exception while trying to open book with id: " + bm.bookId + ". Current exception: " + Throwables.getStackTraceAsString(e));
      return none();
    }
  }
}
