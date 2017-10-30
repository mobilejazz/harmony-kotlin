package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import android.content.Context;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import jedi.option.Option;
import org.javatuples.Pair;

import java.io.*;

import static jedi.option.Options.none;
import static jedi.option.Options.some;

public class OpenFileEpubBookTask extends QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> {

  private static final String TAG = OpenStreamingBookTask.class.getSimpleName();

  private final Context context;
  private final TextLoader textLoader;
  private final ResourcesLoader resourcesLoader;
  private final int storedIndex;
  private final Logger logger;

  public OpenFileEpubBookTask(final Context context, final TextLoader textLoader, final ResourcesLoader resourcesLoader, final int storedIndex,
      final Logger logger) {
    this.context = context.getApplicationContext();
    this.textLoader = textLoader;
    this.resourcesLoader = resourcesLoader;
    this.storedIndex = storedIndex;
    this.logger = logger;
  }

  // TODO: 26/10/2017 Load properly book from resources and remove hardcoded check
  @Override public Option<Pair<Book, PageTurnerSpine>> doInBackground(Void... paramses) {
    try {
      final File f = new File(context.getCacheDir() + "/alice.epub");

      if (!f.exists()) {
        try {
          final InputStream is = context.getAssets().open("alice.epub");
          int size = is.available();
          byte[] buffer = new byte[size];
          is.read(buffer);
          is.close();

          final FileOutputStream fos = new FileOutputStream(f);
          fos.write(buffer);
          fos.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      final Book book = textLoader.initBook(f);

      final PageTurnerSpine spine = new PageTurnerSpine(book);
      spine.navigateByIndex(storedIndex);

      return some(Pair.with(book, spine));
    } catch (Exception e) {
      logger.sendIssue(TAG, "Exception while trying to open book. Current exception: " + Throwables.getStackTraceAsString(e));
      return none();
    }
  }

}
