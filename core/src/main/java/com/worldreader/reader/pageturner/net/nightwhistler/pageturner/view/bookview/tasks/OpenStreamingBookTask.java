package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import jedi.option.Option;
import org.javatuples.Pair;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

public class OpenStreamingBookTask extends QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> {

  private static final String TAG = OpenStreamingBookTask.class.getSimpleName();

  private final TextLoader textLoader;
  private final int storedIndex;
  private final String contentOpf;
  private final String tocResourcePath;
  private final Logger logger;

  public OpenStreamingBookTask(TextLoader textLoader, int storedIndex, String contentOpf, String tocResourcePath, Logger logger) {
    this.textLoader = textLoader;
    this.storedIndex = storedIndex;
    this.contentOpf = contentOpf;
    this.tocResourcePath = tocResourcePath;
    this.logger = logger;
  }

  @SafeVarargs @Override public final Option<Pair<Book, PageTurnerSpine>> doInBackground(Void... params) {
    try {
      final Book book = textLoader.initBook(contentOpf, tocResourcePath);

      final PageTurnerSpine spine = new PageTurnerSpine(book);
      spine.navigateByIndex(storedIndex);

      return some(Pair.with(book, spine));
    } catch (Exception e) {
      logger.sendIssue(TAG, "Exception while trying to open book. Current exception: " + Throwables.getStackTraceAsString(e));
      return none();
    }
  }

}
