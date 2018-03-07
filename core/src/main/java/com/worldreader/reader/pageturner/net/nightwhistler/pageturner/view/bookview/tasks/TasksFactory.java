package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import android.content.Context;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Book;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import com.worldreader.reader.wr.helper.WasabiManager;
import org.javatuples.Pair;

public class TasksFactory {

  private TasksFactory() {
    throw new AssertionError("No instances allowed!");
  }

  public static QueueableAsyncTask<Void, Void, Pair<Book, PageTurnerSpine>> createOpenBookTask(Context context, WasabiManager wm, BookMetadata bm, TextLoader tl, int index, Logger logger) {
    final int mode = bm.mode;
    switch (mode) {
      case BookMetadata.FILE_MODE:
        return new OpenFileEpubBookTask(context, wm, bm, tl, index, logger);
      case BookMetadata.STREAMING_MODE:
        return new OpenStreamingBookTask(context, bm, tl, index, bm.contentOpfName, bm.tocResourceName, logger);
      default:
        throw new IllegalStateException("BookMetadata mode not recognized!");
    }
  }

}
