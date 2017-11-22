package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import jedi.option.Option;
import org.javatuples.Pair;

import java.io.*;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

public class OpenFileEpubBookTask extends QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> {

  private static final String TAG = OpenStreamingBookTask.class.getSimpleName();

  private final BookMetadata bookMetadata;
  private final TextLoader textLoader;
  private final int storedIndex;
  private final Logger logger;

  public OpenFileEpubBookTask(final BookMetadata bm, final TextLoader textLoader, final int storedIndex, final Logger logger) {
    this.bookMetadata = bm;
    this.textLoader = textLoader;
    this.storedIndex = storedIndex;
    this.logger = logger;
  }

  @Override public Option<Pair<Book, PageTurnerSpine>> doInBackground(Void... paramses) {
    try {
      final File file = ((File) bookMetadata.extras.get(BookMetadata.BOOK_FILE_EXTRA));

      final Book book = textLoader.initBook(file);

      final PageTurnerSpine spine = new PageTurnerSpine(book);
      spine.navigateByIndex(storedIndex);

      return some(Pair.with(book, spine));
    } catch (Exception e) {
      logger.sendIssue(TAG, "Exception while trying to open book. Current exception: " + Throwables.getStackTraceAsString(e));
      return none();
    }
  }
}