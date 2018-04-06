package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import android.content.Context;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.wr.models.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import com.worldreader.reader.wr.helper.WasabiManager;
import jedi.option.Option;
import org.javatuples.Pair;

import java.io.*;
import java.lang.ref.WeakReference;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

public class OpenFileEpubBookTask extends QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> {

  private static final String TAG = OpenFileEpubBookTask.class.getSimpleName();

  private final WeakReference<Context> context;
  private final BookMetadata bookMetadata;
  private final WasabiManager wasabiManager;
  private final TextLoader textLoader;
  private final int storedIndex;
  private final Logger logger;

  OpenFileEpubBookTask(final Context c, final WasabiManager wm, final BookMetadata bm, final TextLoader tl, final int storedIndex, final Logger l) {
    this.context = new WeakReference<>(c);
    this.wasabiManager = wm;
    this.bookMetadata = bm;
    this.textLoader = tl;
    this.storedIndex = storedIndex;
    this.logger = l;
  }

  @Override public Option<Pair<Book, PageTurnerSpine>> doInBackground(Void... params) {
    try {
      File file = ((File) bookMetadata.extras.get(BookMetadata.BOOK_FILE_EXTRA));
      final Boolean isEncrypted = (Boolean) bookMetadata.extras.get(BookMetadata.BOOK_ENCRYPTED_EXTRA);
      if (isEncrypted != null && isEncrypted) {
        if (!wasabiManager.isPersonalized()) { // Lazy init wasabi SDK the very first time we encounter a DRM book
          wasabiManager.personalize();
        }
        file = wasabiManager.decrypt(bookMetadata.bookId, file); // Proceed to decrypt the content
      }

      final Book book = textLoader.initBook(file);

      final Context context = this.context.get();
      if (context == null) {
        return none();
      }

      final PageTurnerSpine spine = new PageTurnerSpine(context, book);
      spine.navigateByIndex(storedIndex);

      return some(Pair.with(book, spine));
    } catch (Exception e) {
      logger.sendIssue(TAG, "Exception opening file book with id: " + bookMetadata.bookId + " . Current exception: " + Throwables.getStackTraceAsString(e));
      return none();
    }
  }
}